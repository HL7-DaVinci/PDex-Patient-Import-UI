import { ElNotification } from "element-plus";
import axios from "axios";
import router from "@/router/index";
import { PayersModule } from "@/store/modules/payers";

export const showDefaultErrorNotification = (): void => {
	ElNotification({
		title: "Error",
		type: "error",
		message: "Oops! Something went wrong."
	});
};

const TOKEN_KEY = "token";
export const getToken = (): string | null => localStorage.getItem(TOKEN_KEY);
export const setToken = (token: string): void => localStorage.setItem(TOKEN_KEY, token);
export const removeToken = (): void => localStorage.removeItem(TOKEN_KEY);

export const setRequestAuthHeader = (header: string): string => axios.defaults.headers.common.Authorization = header;
export const removeRequestAuthHeader = (): boolean => delete axios.defaults.headers.common.Authorization;


export function createUUID() {
	return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, c => {
		const r = Math.random() * 16 | 0,
			v = c == "x" ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}


export function authorizePayer(payer): Promise<{ authCode: string, authState: string }> {
	const url = `${location.origin}/api/payers/${payer.id}/authorize`;

	return new Promise((resolve, reject) => {
		const authWindow = window.open(url, "_blank", "width=600,height=600");
		if (!authWindow) {
			reject("Failed to open authorization window");
			return;
		}
		const pollId = setInterval(() => {
			if (authWindow.closed) {
				clearInterval(pollId);
				reject("Authorization window was closed before authorization completed");
			}
			try { // While auth url is opened in the window, cross-origin policy won't allow us to touch it.
				if (authWindow.location.origin === location.origin) {
					const params = new URL(authWindow.location.href).searchParams;
					const authCode = params.get("code");
					const authState = params.get("state");

					authWindow.close();
					clearInterval(pollId);
					if (authCode && authState) {
						resolve({ authCode, authState });
					} else {
						reject("Authorization did not return a code");
					}
				}
			} catch (e) {
				console.warn(e);
			}
		}, 50);
	});
}

export function poll<T>(
	doRequest: () => Promise<T>,
	handle: (resp: T) => Promise <boolean>,   // true means continue polling, false means stop
	ms: number
): void {
	const proceed = async () => {
		const resp = await doRequest();
		if (await handle(resp)) {
			setTimeout(proceed, ms);
		}
	};
	proceed();
}

export const goToImportOrHome = () => PayersModule.importedPayers.length === 0 ? router.push("/import") : router.push("/");
