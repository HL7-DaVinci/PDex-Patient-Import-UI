export default {
	name: "PatientInfo",
	template: `
		<el-card class="patient-info">
			<div
				slot="header"
				class="patient-name"
			>
				{{ fullName }}
			</div>
			<el-row class="patient-info-data">
				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Gender</div>
					<div>{{ gender }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">DOB</div>
					<div>{{ birthDate }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Email</div>
					<div>{{ email }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Phone</div>
					<div>{{ phone }}</div>
				</el-col>

				<el-col
					:lg="6"
					:md="8"
					:sm="12"
					:xs="12"
				>
					<div class="patient-info-label">Address</div>
					<div>{{ address }}</div>
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
					<div class="patient-info-label">MRN</div>
					<div>{{ mrn }}</div>
				</el-col>
			</el-row>
		</el-card>
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

			return address ? `${address.line.join(" ")},  ${address.postalCode}, ${address.city} ${address.country}` : "";
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
