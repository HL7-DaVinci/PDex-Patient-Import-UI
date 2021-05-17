import { createStore } from "vuex";
import { IAuth } from "@/store/modules/auth";
import { IPatient } from "@/store/modules/patient";
import { IPayer } from "@/store/modules/payer";
import { IPayers } from "@/store/modules/payers";
import { ICalls } from "@/store/modules/calls";
import { config } from "vuex-module-decorators";
// Set rawError to true by default on all @Action decorators
config.rawError = true;

export interface IRootState {
	auth: IAuth,
	patient: IPatient,
	payer: IPayer,
	payers: IPayers,
	calls: ICalls
}

export default createStore<IRootState>({
	strict: process.env.NODE_ENV !== "production"
});
