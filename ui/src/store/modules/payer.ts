import { removePayerData, importPayerData, getResourcesOverview, getTotalResourcesOverview, removeAllData } from "../../api/api";

const state = {
	resourcesOverview: [],
	totalResourcesOverview: []
};

const getters = {
	resourcesOverview(state: any) {
		return state.resourcesOverview;
	},
	totalResourcesOverview(state: any) {
		return state.totalResourcesOverview;
	}
};

const mutations = {
	setResourcesOverview(state: any, data: any) {
		state.resourcesOverview = data;
	},
	setTotalResourcesOverview(state: any, data: any) {
		state.totalResourcesOverview = data;
	}
};

const actions = {
	importPayerData({ commit }: any, payload: any) {
		return importPayerData(payload.payerId, payload.patientId, payload.accessToken)
			.then((response: any) => {
				commit("setResourcesOverview", response.data);
				return response.data;
			})
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	getResourcesOverview({ commit }: any, payerId: string) {
		return getResourcesOverview(payerId)
			.then((response: any) => {
				commit("setResourcesOverview", response.data);
				return response.data;
			})
			.catch((error: any) => {
				console.log(error);
				throw error;
			});
	},
	getTotalResourcesOverview({ commit }: any) {
		return getTotalResourcesOverview()
			.then((response: any) => {
				commit("setTotalResourcesOverview", response.data);
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
