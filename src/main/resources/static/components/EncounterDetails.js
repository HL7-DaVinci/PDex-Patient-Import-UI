import EncounterInfo from "./EncounterInfo.js";
import { errorMessage } from "../main.js";

export default {
	name: "EncounterDetails",
	components: { EncounterInfo },
	template: `
		<el-row>
			<el-page-header @back="goBack"></el-page-header>
			<encounter-info :data="encounter.resource || {}"></encounter-info>
			<el-col
				v-if="encounterReady"
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
				</el-tabs>
			</el-col>
		</el-row>
	`,
	props: ["id"], // passed from router
	data() {
		return {
			loading: false,
			selectedResource: "observation",
			encounter: {},
			observations: []
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
