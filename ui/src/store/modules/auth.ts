import axios from "axios";
import { login, authVerify } from "../../api/api";

const state = {
	token: localStorage.getItem("token") || "",
	status: ""
};

const getters = {
	//todo: should I create State type?
	isAuthenticated(state: any): Boolean {
		return !!state.token;
	},
	token(state: any): String {
		return state.token;
	}
};

const mutations = {
	authRequest(state: any) {
		state.status = "loading";
	},
	authSuccess(state: any, token: String) {
		state.status = "success";
		state.token = token;
	},
	authError(state: any) {
		state.status = "error";
	},
	authLogout(state: any) {
		state.token = "";
		state.status = "";
	}
};

const actions = {
	authRequest({ commit }: any, payload: any) {
		commit("authRequest");

		return login(payload)
			.then(res => {
				const { token } = res.data;

				localStorage.setItem("token", token);
				axios.defaults.headers.common.Authorization = `Bearer ${token}`;
				commit("authSuccess", token);
			})
			.catch(err => {
				commit("authError", err);
				localStorage.removeItem("token");

				throw err;
			});
	},
	authLogout({ commit }: any) {
		return new Promise(resolve => {
			commit("authLogout");
			localStorage.removeItem("token");
			delete axios.defaults.headers.common.Authorization;
			resolve();
		});
	},
	authVerify({ commit }: any, payload: any) {
		return authVerify(payload)
			.then(({ data }) => {
				if (!data.active) {
					commit("authLogout");
					localStorage.removeItem("token");
					delete axios.defaults.headers.common.Authorization;

					throw new Error("inactive token");
				}
			});
	}
};

export default {
	state,
	getters,
	mutations,
	actions
};
