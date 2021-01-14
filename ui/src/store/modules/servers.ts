import { getAllServers, addServer, deleteServer, changeServer, getServerToken } from "../../api/api";

const state = {
	servers: [],
	isServerDeleting: false,
	activePayerId: ""
};

const getters = {
	servers(state: any) {
		return state.servers;
	},
	importedPayers(state: any, getters: any) {
		return getters.servers.filter(server => server.lastImported !== null);
	},
	isServerDeleting(state: any) {
		return state.isServerDeleting;
	},
	activePayer(state: any, getters: any) {
		return !getters.importedPayers ? {} : getters.importedPayers.find((item: any) => item.id === +state.activePayerId);
	},
	activePayerId(state: any) {
		return state.activePayerId;
	}
};

const mutations = {
	setServers(state: any, data: any) {
		state.servers = data;
	},
	addServer(state: any, newServer: any) {
		state.servers = [...state.servers, newServer];
	},
	deleteServer(state: any, id: string) {
		state.servers = state.servers.filter((item :any) => item.id !== id);
	},
	changeIsDeleting(state: any) {
		state.isServerDeleting = !state.isServerDeleting;
	},
	changeServer(state: any, data: any) {
		state.servers = state.servers.map((server: any) => {
			if (server.id === data.id) {
				return { ...server, ...data };
			}

			return server;
		});
	},
	setActivePayerId(context: any, id: any) {
		state.activePayerId = id;
	}
};

const actions = {
	loadServers(context: any) {
		return getAllServers()
			.then((response: any) => {
				context.commit("setServers", response.data);

				return response.data;
			})
			.catch((error: string) => {
				console.log(error);
				throw error;
			});
	},
	addServer(context: any, payload: any) {
		return addServer(payload)
			.then((response: any) => {
				context.commit("addServer", response.data);

				return response.data;
			})
			.catch((error: string) => {
				console.log(error);
				throw error;
			});
	},
	deleteServer(context: any, payload: any) {
		context.commit("changeIsDeleting");
		return deleteServer(payload.id)
			.then((response: any) => {
				context.commit("deleteServer", payload.id);

				return response.data;
			})
			.catch((error: string) => {
				console.log(error);
				throw error;
			})
			.finally(() => context.commit("changeIsDeleting"));
	},
	changeServer(context: any, payload: any) {
		return changeServer(payload)
			.then((response: any) => {
				context.commit("changeServer", response.data);

				return response.data;
			})
			.catch((error: string) => {
				console.log(error);
				throw error;
			});
	},
	getServerToken(context: any, payload: any) {
		return getServerToken(payload.payerId, payload.authCode)
			.then((response: any) => response.data)
			.catch((error: string) => {
				console.log(error);
				throw error;
			});
	},
	changeServerLastImportedDate(context: any, payload: any) {
		context.commit("changeServer", payload);
	},
	setActivePayerId(context: any, id: any) {
		context.commit("setActivePayerId", id);
	}
};

export default {
	state,
	getters,
	mutations,
	actions
};
