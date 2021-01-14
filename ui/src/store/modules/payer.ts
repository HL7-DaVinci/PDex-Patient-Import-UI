import { removePayerData, importPayerData, getResourceOverview, getTotalResourceOverview, removeAllData } from "../../api/api";

const state = {
	resourceOverview: [],
	totalResourceOverview: []
};

const getters = {
	resourceOverview(state: any) {
		return state.resourceOverview;
	},
	totalResourceOverview(state: any) {
		return state.totalResourceOverview;
	}
};

const mutations = {
	setResourceOverview(state: any, data: any) {
		state.resourceOverview = data;
	},
	setTotalResourceOverview(state: any, data: any) {
		state.totalResourceOverview = data;
	}
};

const actions = {
	importPayerData({ commit }: any, payload: any) {
		return importPayerData(payload.payerId, payload.patientId, payload.accessToken)
			.then((response: any) => {
				commit("setResourceOverview", response.data);
				return response.data;
			})
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	getResourceOverview({ commit }: any, payerId: string) {
		return getResourceOverview(payerId)
			.then((response: any) => {
				commit("setResourceOverview", response.data);
				return response.data;
			})
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	getTotalResourceOverview({ commit }: any) {
		return getTotalResourceOverview()
			.then((response: any) => {
				commit("setTotalResourceOverview", response.data);
				return response.data;
			})
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	removePayerData({ commit }: any, id: any) {
		return removePayerData(id)
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	removeAllData({ getters }: any) {
		return removeAllData(getters.importedPayers)
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	}
};

export default {
	state,
	getters,
	mutations,
	actions
};
