import SockJS from "sockjs-client";
import Stomp, { Client } from "webstomp-client";
import { Call } from "@/types";
import { Ref, ref, watch, computed } from "vue";


type Callback<T> = (t: T) => unknown

type Subscription = {
	unsubscribe: () => void
}

type Connection = {
	subscribe: (topic: string, cb: Callback<string>) => Subscription
}


function getConnection(authToken: Ref<string | null>): Ref<Connection | null> {
	const connection = ref<Connection | null>(null);
	let stompClient: Client | null = null;
	function disconnect() {
		if (stompClient) {
			connection.value = null;
			stompClient.disconnect(() => {});
			stompClient = null;
		}
	}
	watch(authToken, (token, _, onInvalidate) => {
		onInvalidate(() => {
			disconnect();
		});
		disconnect();
		if (token) {
			const header = { "Authorization": `Bearer ${token}` };
			stompClient = Stomp.over(new SockJS("/api/ws"), { protocols: ["v10.stomp", "v11.stomp", "v12.stomp"], debug: false });
			stompClient.connect(header, () => {
				connection.value = {
					subscribe(topic, cb) {
						const sub = stompClient?.subscribe(topic, message => {
							cb(message.body);
						});
						return {
							unsubscribe: () => sub?.unsubscribe()
						};
					}
				};
			});
		}
	}, { immediate: true });

	return connection;
}


//
// Connection type where you can be subscribed up to a singe stream at any time.
//
type SingleStreamConnection = {
	subscribe: (topic: string, cb: Callback<string>) => void,
	unsubscribe: () => void
}


function makeSingleStream(conn: Connection): SingleStreamConnection {
	let sub: Subscription | null = null;
	return {
		subscribe(topic: string, cb: Callback<string>) {
			if (sub) {
				sub.unsubscribe();
			}
			sub = conn.subscribe(topic, cb);
		},
		unsubscribe() {
			if (sub) {
				sub.unsubscribe();
				sub = null;
			}
		}
	};
}


//
// When both connection and topic are ok - makes sure that subscription is active.
//
function keepSubscribed(
	connection: Ref<Connection | null>,
	topic: Ref<string | null>,
	callback: Callback<string>
): void {
	const singleStreamCon = computed<SingleStreamConnection | null>(() => connection.value ? makeSingleStream(connection.value) : null);
	watch(() => ({ con: singleStreamCon.value, topic: topic.value }), ({ con, topic }, _, onInvalidate) => {
		if (con) {
			con.unsubscribe();
			if (topic) {
				con.subscribe(topic, callback);
			}
			onInvalidate(() => {
				con.unsubscribe();
			});
		}
	});
}


export function useFhirCallStream(
	authToken: Ref<string | null>,
	payerId: Ref<number | null>,
	callback: Callback<Call>
): void {
	const connection = getConnection(authToken);
	const topic = computed(() => payerId.value ? `/import/${payerId.value}` : null);
	const cb = (message: string) => {
		const call: Call = JSON.parse(message);
		callback(call);
	};
	keepSubscribed(connection, topic, cb);
}
