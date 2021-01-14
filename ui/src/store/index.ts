import { createStore } from "vuex";
import auth from "./modules/auth";
import servers from "./modules/servers";
import payer from "./modules/payer";
import patient from "./modules/patient";

export default createStore({
	state: {
	},
	mutations: {
	},
	actions: {
	},
	modules: {
		auth,
		payer,
		servers,
		patient
	}
});
