const errorMessage = res => res && res.data && res.data.message ? `Error: ${res.data.message}` : 'Error happened.';

const PayerList = Vue.component("PayerList", {
	template: "#payer-list",
	data() {
		return {
			loading: false,
			payers: [],
			payerDialogVisible: false,
			form: {
				payer: ""
			}
		};
	},
	computed: {
		syncDate() {
			return moment().format("MMM DD, YYYY");
		}
	},
	mounted() {
		this.loading = true;
		axios.get("/fhir/Organization", { params: { type: "pay" } })
			.then(({ data }) => this.payers = data.entry)
			.catch(error => this.$message.error(errorMessage(error.response)))
			.finally(() => this.loading = false);
	},
	methods: {
		handleDetailsClick({ identifier }) {
			this.$router.push({ path: `/details/${encodeURIComponent(identifier[0].system)}` });
		},
		handlePayerDelete(payer, index) {
			const payerIdentifier = payer.identifier[0].system;

			this.loading = true;
			axios.delete(`/fhir/Organization/${payer.id}`)
				.then(() => axios.get("/fhir/Patient", { params: { identifier: `${payerIdentifier}|` } }))
				.then(({ data }) => axios.delete(`/fhir/Patient/${data.entry[0].resource.id}?_cascading=true&_cascade=delete`))
				.then(() => this.payers.splice(index, 1))
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		}
	}
});

Vue.component("patient-info", {
	template: "#patient-info",
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
});

const PayerDetails = Vue.component("PayerDetails", {
	template: "#payer-details",
	props: ["id"],
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
});

Vue.component("encounter-info", {
	template: "#encounter-info",
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
});

const EncounterDetails = Vue.component("EncounterDetails", {
	template: "#encounter-details",
	props: ["id"],
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
});

const NotFound = Vue.component("NotFound", {
	template: "#not-found"
});

Vue.use(VueRouter);
ELEMENT.locale(ELEMENT.lang.en);

const router = new VueRouter({
	routes: [{
		path: "/",
		component: PayerList
	},
	{
		path: "/details/:id",
		component: PayerDetails,
		props: true
	},
	{
		path: "/encounter/details/:id",
		component: EncounterDetails,
		props: true
	},
	{
		path: "*",
		component: NotFound
	}]
});

Vue.filter("dateTime", val => val ? moment(val).format("YYYY-MM-DD HH:ss") : "");
axios.defaults.headers.common["Cache-Control"] = "no-cache";

new Vue({
	el: "#app",
	router
});
