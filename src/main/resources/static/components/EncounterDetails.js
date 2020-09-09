import EncounterInfo from "./EncounterInfo.js";
import { errorMessage } from "../main.js";

export default {
	name: "EncounterDetails",
	components: { EncounterInfo },
	template: `
		<b-row>
			<el-page-header @back="goBack"></el-page-header>
			<encounter-info :data="encounter.resource || {}"></encounter-info>
			<b-col
				v-if="encounterReady"
				cols="12"
			>
				<b-tabs
					content-class="mt-3"
					v-model="selectedResource"
					pills
					fill
				>
					<b-tab
						label="Observation"
						name="observation"
						@click="handleResourceChange('observation')"
						:lazy="true"
						active
						:title-link-class="linkClass(0)"
					>
						<el-table
							:data="observations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="240" label="Observation Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.effectiveDateTime" min-width="180" label="Effective Date(s)" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.valueQuantity" min-width="180" label="Value" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.value }} {{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.unit }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="220" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.interpretation" min-width="260" label="Interpretation Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.interpretation }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>
				</b-tabs>
			</b-col>
		</b-row>
	`,
	props: ["id"], // passed from router
	data() {
		return {
			loading: false,
			selectedResource: "observation",
			encounter: {},
			observations: [],
			tabIndex: 0
		};
	},
	mounted() {
		this.loadEncounter().then(this.loadObservation);
	},
	computed: {
		encounterReady() {
			return Object.keys(this.encounter).length > 0;
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
		loadEncounter() {
			this.loading = true;
			return axios.get("/fhir/Encounter", { params: { _id: `${this.id}` } })
				.then(({ data }) => this.encounter = data.entry[0])
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadObservation() {
			this.loading = true;
			axios.get("/fhir/Observation", { params: { encounter: this.encounter.resource.id, _count: 100 } })
				.then(({ data }) => this.observations = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		handleResourceChange({ name }) {
			const API_MAP = {
				observation: this.loadObservation
			};

			API_MAP[name]();
		},
		goBack() {
			this.$router.go(-1);
		}
	}
};
