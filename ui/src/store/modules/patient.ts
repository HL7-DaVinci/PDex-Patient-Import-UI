import { getPatientInfo, getAllPatients } from "../../api/api";
import Mapper from "../../utils/resourceMappings.js";

const state = {
	patient: {},
	patientId: "",
	allPatients: []
};


const getters = {
	patient(state: any) {
		return !state.patient.total ? state.patient : Mapper.Patient.convert(state.patient.entry[0].resource);
	},
	patientId(state: any) {
		return state.patientId;
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
	},
	setPatientId(state: any, data: any) {
		state.patientId = data;
	}
};

const actions = {
	getPatientInfo({ commit }: any, id: string) {
		return getPatientInfo(id).then(({ data }) => {
			commit("setPatientInfo", data);
			commit("setPatientId", id);
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
