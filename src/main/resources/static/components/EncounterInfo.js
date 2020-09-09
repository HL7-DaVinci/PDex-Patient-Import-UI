export default {
	name: "EncounterInfo",
	template: `
		<<b-card class="mb-2 patient-info-card" v-bind:header="classCode">
			<b-row class="patient-info-field">
				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Period Start</div>
					<div class="patient-info-field">{{ periodStart | dateTime }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Period End</div>
					<div class="patient-info-field">{{ periodEnd | dateTime }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Reason Code</div>
					<div class="patient-info-field">{{ reasonCode }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">ID</div>
					<div class="patient-info-field">{{ id }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Status</div>
					<div class="patient-info-field">{{ status }}</div>
				</b-col>
			</b-row>
		</b-card>
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
