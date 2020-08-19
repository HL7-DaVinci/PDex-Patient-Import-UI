import PatientInfo from "./PatientInfo.js";
import { errorMessage } from "../main.js";

export default {
	name: "PayerDetails",
	components: { PatientInfo },
	template: `
		<el-row>
			<el-page-header @back="goBack"></el-page-header>
			<patient-info :data="patient.resource || {}"></patient-info>
			<el-col
				v-if="patientReady"
				:span="24"
			>
				<el-tabs
					v-model="selectedResource"
					type="border-card"
					@tab-click="handleResourceChange"
				>
					<el-tab-pane
						label="Observation"
						name="observation"
						:lazy="true"
					>
						<el-table
							:data="observations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column label="Observation Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column label="Effective Date(s)">
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Value">
								<template slot-scope="scope">
									{{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.value }} {{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.unit }}
								</template>
							</el-table-column>
							<el-table-column label="Category Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column label="Interpretation Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.interpretation }}
								</template>
							</el-table-column>
						</el-table>
					</el-tab-pane>

					<el-tab-pane
						label="Condition"
						name="condition"
						:lazy="true"
					>
						<el-table
							:data="conditions"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column label="Condition Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column label="Onset">
								<template slot-scope="scope">
									{{ scope.row.resource.onsetDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Asserted Date Time">
								<template slot-scope="scope">
									{{ scope.row.resource.recordedDate | dateTime}}
								</template>
							</el-table-column>
							<el-table-column label="Category Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column label="Severity Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.severity && scope.row.resource.severity.text }}
								</template>
							</el-table-column>
							<el-table-column label="Note Date Time">
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column label="Verification Status">
								<template slot-scope="scope">
									{{ scope.row.resource.verificationStatus && scope.row.resource.verificationStatus.coding && scope.row.resource.verificationStatus.coding[0] && scope.row.resource.verificationStatus.coding[0].code }}
								</template>
							</el-table-column>
						</el-table>
					</el-tab-pane>

					<el-tab-pane
						label="Encounter"
						name="encounter"
						:lazy="true"
					>
						<el-table
							:data="encounters"
							:stripe="true"
							v-loading="loading"
							@row-click="handleEncounterRowClick"
							class="encounters-table"
						>
							<el-table-column label="Class Code">
								<template slot-scope="scope">
									{{ scope.row.resource.class && scope.row.resource.class.code }}
								</template>
							</el-table-column>
							<el-table-column label="Period Start DateTime">
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.start | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Period End DateTime">
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.end | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Reason Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode }}
								</template>
							</el-table-column>
							<el-table-column label="Status">
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</el-tab-pane>

					<el-tab-pane
						label="MedicationRequest"
						name="medicationRequest"
						:lazy="true"
					>
						<el-table
							:data="medicationRequests"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column label="Medication Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.medicationCodeableConcept && scope.row.resource.medicationCodeableConcept.text }}
								</template>
							</el-table-column>
							<el-table-column label="Authored On">
								<template slot-scope="scope">
									{{ scope.row.resource.authoredOn | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Reason Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode && scope.row.resource.reasonCode[0] && scope.row.resource.reasonCode[0].coding[0] && scope.row.resource.reasonCode[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column label="Note Date Time">
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column label="Status">
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</el-tab-pane>

					<el-tab-pane
						label="Immunization"
						name="immunization"
						:lazy="true"
					>
						<el-table
							:data="immunizations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column label="Vaccine Code Display">
								<template slot-scope="scope">
									{{ scope.row.resource.vaccineCode && scope.row.resource.vaccineCode.text }}
								</template>
							</el-table-column>
							<el-table-column label="Date Time">
								<template slot-scope="scope">
									{{ scope.row.resource.occurrenceDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column label="Status">
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</el-tab-pane>
				</el-tabs>
			</el-col>
		</el-row>
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
