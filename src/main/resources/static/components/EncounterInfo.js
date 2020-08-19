export default {
	name: "EncounterInfo",
	template: `
		<el-card class="patient-info">
			<div
				slot="header"
				class="patient-name"
			>
				{{ classCode }}
			</div>
			<el-row class="patient-info-data">
				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Period Start</div>
					<div>{{ periodStart | dateTime }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Period End</div>
					<div>{{ periodEnd | dateTime }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Reason Code</div>
					<div>{{ reasonCode }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">ID</div>
					<div>{{ id }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Status</div>
					<div>{{ status }}</div>
				</el-col>
			</el-row>
		</el-card>
	`,
	props: ["data"],
	computed: {
		classCode() {
			return this.data.class && this.data.class.code;
		},
		periodStart() {
			return this.data.period && this.data.period.start;
		},
		periodEnd() {
			return this.data.period && this.data.period.end;
		},
		reasonCode() {
			return this.data.reasonCode;
		},
		status() {
			return this.data.status;
		},
		id() {
			return this.data.id;
		}
	}
};
