import PatientInfo from "./PatientInfo.js";
import { errorMessage } from "../main.js";

export default {
	name: "PayerDetails",
	components: { PatientInfo },
	template: `
		<b-row>
			<patient-info :data="patient.resource || {}"></patient-info>
			<b-col
				v-if="patientReady"
				cols="12"
			>
				<b-tabs
					content-class="mt-3"
					v-model="selectedResource"
					pills
					fill
				>
					<b-tab
						title="Observation"
						name="observation"
						@click="handleResourceChange({'observation'})"
						:lazy="true"
						active
						:title-link-class="linkClass(0)"
					>
						<el-table
							style="width: 100%"
							:data="observations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="240" sortable label="Observation Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.effectiveDateTime" min-width="180" sortable label="Effective Date(s)">
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.valueQuantity" min-width="180" sortable label="Value">
								<template slot-scope="scope">
									{{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.value }} {{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.unit }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="220" sortable label="Category Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.interpretation" min-width="260" sortable label="Interpretation Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.interpretation }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Condition"
						name="condition"
						@click="handleResourceChange"
						:lazy="true"
						:title-link-class="linkClass(1)"
					>
						<el-table
							style="width: 100%"
							:data="conditions"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column min-width="220" sortable label="Condition Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Onset" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.onsetDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Asserted Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.recordedDate | dateTime}}
								</template>
							</el-table-column>
							<el-table-column min-width="240" sortable label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column min-width="240" sortable label="Severity Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.severity && scope.row.resource.severity.text }}
								</template>
							</el-table-column>
							<el-table-column min-width="200" sortable label="Note Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Verification Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.verificationStatus && scope.row.resource.verificationStatus.coding && scope.row.resource.verificationStatus.coding[0] && scope.row.resource.verificationStatus.coding[0].code }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Encounter"
						name="encounter"
						@click="handleResourceChange"
						:lazy="true"
						:title-link-class="linkClass(2)"
					>
						<el-table
							style="width: 100%"
							:data="encounters"
							:stripe="true"
							v-loading="loading"
							@row-click="handleEncounterRowClick"
							class="encounters-table"
						>
							<el-table-column min-width="180" sortable label="Class Code" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.class && scope.row.resource.class.code }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Period Start DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.start | dateTime }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Period End DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.end | dateTime }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Reason Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="MedicationRequest"
						name="medicationRequest"
						@click="handleResourceChange"
						:lazy="true"
						:title-link-class="linkClass(3)"
					>
						<el-table
							style="width: 100%"
							:data="medicationRequests"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column min-width="240" sortable label="Medication Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.medicationCodeableConcept && scope.row.resource.medicationCodeableConcept.text }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Authored On" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.authoredOn | dateTime }}
								</template>
							</el-table-column>
							<el-table-column min-width="220" sortable label="Reason Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode && scope.row.resource.reasonCode[0] && scope.row.resource.reasonCode[0].coding[0] && scope.row.resource.reasonCode[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column min-width="200" sortable label="Note Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Immunization"
						name="immunization"
						@click="handleResourceChange"
						:lazy="true"
						:title-link-class="linkClass(4)"
					>
						<el-table
							style="width: 100%"
							:data="immunizations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column min-width="240" sortable label="Vaccine Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.vaccineCode && scope.row.resource.vaccineCode.text }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.occurrenceDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column min-width="180" sortable label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>
				</el-tabs>
			</b-col>
		</b-row>
	`,
	props: ["id"], // passed from router
	data() {
		return {
			loading: false,
			selectedResource: "observation",
			patient: {},
			encounters: [],
			observations: [],
			conditions: [],
			medicationRequests: [],
			immunizations: []
		};
	},
	mounted() {
		this.loadPatient().then(this.loadObservation);
	},
	computed: {
		patientReady() {
			return Object.keys(this.patient).length > 0;
		}
	},
	methods: {
		linkClass(idx) {
			if (this.tabIndex === idx) {
				return ['bg-primary', 'text-light']
			} else {
				return ['bg-light', 'text-info']
			}
		},
		loadPatient() {
			this.loading = true;
			return axios.get("/fhir/Patient", { params: { identifier: `${this.id}|` } })
				.then(({ data }) => this.patient = data.entry[0])
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadEncounter() {
			this.loading = true;
			axios.get("/fhir/Encounter", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.encounters = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadObservation() {
			this.loading = true;
			axios.get("/fhir/Observation", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.observations = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadCondition() {
			this.loading = true;
			axios.get("/fhir/Condition", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.conditions = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadMedicationRequest() {
			this.loading = true;
			axios.get("/fhir/MedicationRequest", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.medicationRequests = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadImmunization() {
			this.loading = true;
			axios.get("/fhir/Immunization", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.immunizations = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		handleResourceChange({ name }) {
			const API_MAP = {
				encounter: this.loadEncounter,
				observation: this.loadObservation,
				condition: this.loadCondition,
				medicationRequest: this.loadMedicationRequest,
				immunization: this.loadImmunization
			};

			API_MAP[name]();
		},
		goBack() {
			this.$router.push({ path: "/" });
		},
		handleEncounterRowClick(row) {
			this.$router.push({ path: `/encounter/details/${encodeURIComponent(row.resource.id)}` });
		}
	}
};
