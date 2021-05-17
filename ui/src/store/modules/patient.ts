import { getPatient, getAllPatients } from "@/api/api";
import { IPatient as IFhirPatient } from "@ahryman40k/ts-fhir-types/lib/R4";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";

export interface IPatient {
	patient: IFhirPatient | null,
	allPatients: IFhirPatient[]
}

@Module({ dynamic: true, store, name: "patient" })
class Patient extends VuexModule implements IPatient {
	patient: IFhirPatient | null = null;
	allPatients: IFhirPatient[] = [];

	@Mutation
	setPatientInfo(patient: IFhirPatient): void {
		this.patient = patient;
	}

	@Mutation
	setAllPatients(patients: IFhirPatient[]): void {
		this.allPatients = patients;
	}

	@Action
	async getPatient(payload: { payerId: number, patientId: string }): Promise<void> {
		const { data } = await getPatient(payload);
		this.setPatientInfo(data);
	}

	@Action
	async getAllPatients(): Promise<void> {
		const { data } = await getAllPatients();
		const patients = data.entry ? data.entry.map(entry => entry.resource) : [];
		this.setAllPatients(patients);
	}
}

export const PatientModule = getModule(Patient);
