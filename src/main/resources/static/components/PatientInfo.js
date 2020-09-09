export default {
	name: "PatientInfo",
	template: `
		<b-card class="mb-2 patient-info-card" v-bind:header="fullName">
			<b-row class="patient-info-field">
				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Gender</div>
					<div class="patient-info-field">{{ gender }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">DOB</div>
					<div class="patient-info-field">{{ birthDate }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Email</div>
					<div class="patient-info-field">{{ email }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Phone</div>
					<div class="patient-info-field">{{ phone }}</div>
				</b-col>

				<b-col
					cols="6"
					class="mb-1"
				>
					<div class="text-muted patient-info-label">Address</div>
					<div class="patient-info-field">{{ address }}</div>
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
					<div class="text-muted patient-info-label">MRN</div>
					<div class="patient-info-field">{{ mrn }}</div>
				</b-col>
			</b-row>
		</b-card>
	`,
	props: ["data"],
	computed: {
		fullName() {
			const name = this.data.name && this.data.name[0];

			return name ? `${this.data.name[0].given.join(" ")} ${this.data.name[0].family}` : "";
		},
		gender() {
			return this.data.gender;
		},
		birthDate() {
			return this.data.birthDate;
		},
		email() {
			const email = this.data.telecom && this.data.telecom.find(i => i.system === "email");

			return email ? email.value : "";
		},
		phone() {
			const phone = this.data.telecom && this.data.telecom.find(i => i.system === "phone");

			return phone ? phone.value : "";
		},
		address() {
			const address = this.data.address && this.data.address[0];

			return address ? `${address.line && address.line.join(" ")},  ${address.postalCode}, ${address.city} ${address.country}` : "";
		},
		id() {
			return this.data.id;
		},
		mrn() {
			const id = this.data.identifier && this.data.identifier[0];

			return id ? id.value : "";
		}
	}
};
