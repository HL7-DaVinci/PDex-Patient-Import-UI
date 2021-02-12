import { getPatientInfo, getAllPatients } from "../../api/api";

const state = {
	patient: null,
	allPatients: []
};


const getters = {
	patient(state: any) {
		return state.patient;
	},
	allPatients(state: any) {
		return state.allPatients;
	}
};

const mutations = {
	setPatientInfo(state: any, data: any) {
		state.patient = data;
	},
	setAllPatients(state: any, data: any) {
		state.allPatients = data;
	}
};

const actions = {
	getPatientInfo({ commit }: any, payload: any) {
		return getPatientInfo(payload).then(({ data }) => {
			commit("setPatientInfo", data);
		});
	},
	getAllPatients({ commit }: any) {
		return getAllPatients().then(({ data }) => {
			const patients = data.entry ? data.entry.map(entry => entry.resource) : [];
			commit("setAllPatients", patients);
		});
	}
};

export default {
	state,
	getters,
	mutations,
	actions
};
