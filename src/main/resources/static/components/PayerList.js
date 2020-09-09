import { errorMessage } from "../main.js";

export default {
	name: "PayerList",
	template: `
		<div>
			<b-row
				:gutter="25"
				class="mb-3 card-container"
				v-loading="loading"
			>
				<b-col
					cols="12"
					md="6"
					lg="4"
					v-for="(payer, index) in payers"
					:key="index"
				>
					<b-card
						v-bind:header="payer.resource.name"
					>
						<div class="payer-sync">
							Synchronized at {{ syncDate }} 
							<el-button
								icon="el-icon-refresh-right"
								size="small"
								circle
							>
							</el-button>
						</div>
						<b-button
							block
							variant="outline-secondary"
							@click="handleDetailsClick(payer.resource)"
							class="payer-details-button"
						>
							Show
						</b-button>
						<el-button
							icon="el-icon-close"
							class="payer-delete-button"
							circle
							@click="handlePayerDelete(payer.resource, index)"
						>
						</el-button>
					</el-card>
				</b-col>
			</b-row>
			<b-row class="center-button">
				<b-col
					cols="12"
					md="6"
					lg="4"
				>
					<b-button
						block
						variant="primary"
						v-b-modal.modal
					>
						Connect new payer
						<b-icon icon="plus"></i>
					</b-button>

					<b-modal
						title="Select From Payer List"
						id="modal"
						hide-footer
						:append-to-body="true"
						:destroy-on-close="true"
						custom-class="new-payer-dialog"
					>
						<el-form
							class="new-payer-form"
							:model="form"
							method="post"
							action="/gotopayer"
						>
							<el-radio
								v-model="form.payer"
								label="Diamond Health"
								name="payer"
							>
								<svg width='40' height='40' viewBox='0 0 177 100' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M98.9315 39.4284C96.1883 36.6852 92.9316 34.5091 89.3475 33.0245C85.7633 31.5399 81.9219 30.7758 78.0424 30.7758C74.1629 30.7758 70.3215 31.5399 66.7373 33.0245C63.1532 34.5091 59.8965 36.6852 57.1533 39.4284L78.0424 60.3174L98.9315 39.4284Z' fill='#FFD200'/><path d='M78.0686 60.3475C80.8118 63.0907 84.0684 65.2667 87.6526 66.7513C91.2367 68.2359 95.0782 69 98.9577 69C102.837 69 106.679 68.2359 110.263 66.7513C113.847 65.2667 117.104 63.0907 119.847 60.3475L98.9577 39.4584L78.0686 60.3475Z' fill='#06E07F'/><path d='M78.017 60.3429C75.2738 63.0861 72.0171 65.2621 68.433 66.7467C64.8488 68.2313 61.0074 68.9954 57.1279 68.9954C53.2484 68.9954 49.407 68.2313 45.8228 66.7467C42.2387 65.2621 38.982 63.0861 36.2388 60.3429L57.1279 39.4538L78.017 60.3429Z' fill='#E3073C'/><path d='M98.9831 39.433C101.726 36.6898 104.983 34.5138 108.567 33.0292C112.151 31.5446 115.993 30.7805 119.872 30.7805C123.752 30.7805 127.593 31.5446 131.177 33.0292C134.761 34.5138 138.018 36.6898 140.761 39.433L119.872 60.3221L98.9831 39.433Z' fill='#1F84EF'/></svg>
								<span>Diamond Health</span>
							</el-radio>
							<el-radio
								v-model="form.payer"
								label="New Way Insurance"
								name="payer"
							>
								<svg width='40' height='40' viewBox='0 0 177 100' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M88 32.5C88 42.165 80.165 50 70.5 50H53V32.5C53 22.835 60.835 15 70.5 15C80.165 15 88 22.835 88 32.5Z' fill='#17CF97'/><path d='M88 67.5C88 57.835 95.835 50 105.5 50H123V67.5C123 77.165 115.165 85 105.5 85C95.835 85 88 77.165 88 67.5Z' fill='#17CF97'/><path d='M53 67.5C53 77.165 60.835 85 70.5 85H88V67.5C88 57.835 80.165 50 70.5 50C60.835 50 53 57.835 53 67.5Z' fill='#17CF97'/><path d='M123 32.5C123 22.835 115.165 15 105.5 15H88V32.5C88 42.165 95.835 50 105.5 50C115.165 50 123 42.165 123 32.5Z' fill='#17CF97'/></svg>
								<span>New Way Insurance</span>
							</el-radio>
							<el-radio
								v-model="form.payer"
								label="Premium"
								name="payer"
							>
								<svg width='40' height='40' viewBox='0 0 177 100' fill='none' xmlns='http://www.w3.org/2000/svg'><circle cx='72' cy='50' r='30' fill='#68DBFF'/><ellipse cx='104.647' cy='50' rx='29.7059' ry='30' fill='#FF7917'/><path fill-rule='evenodd' clip-rule='evenodd' d='M88.4039 75.1221C96.5911 69.7652 102 60.5143 102 50C102 39.4858 96.5911 30.2348 88.4039 24.878C80.2971 30.2348 74.9412 39.4858 74.9412 50C74.9412 60.5143 80.2971 69.7652 88.4039 75.1221Z' fill='#5D2C02'/></svg>
								<span>Premium</span>
							</el-radio>
							<el-radio
								v-model="form.payer"
								label="Public Insurance"
								name="payer"
							>
								<svg width='40' height='40' viewBox='0 0 177 100' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M52 59.0502C52 55.3864 52.7216 51.7585 54.1237 48.3737C55.5258 44.9888 57.5808 41.9132 60.1715 39.3225C62.7622 36.7318 65.8377 34.6768 69.2226 33.2747C72.6075 31.8727 76.2354 31.151 79.8992 31.151L79.8992 59.0502L52 59.0502Z' fill='#442781'/><path d='M52 59.0502C52 62.714 52.7216 66.3419 54.1237 69.7268C55.5258 73.1117 57.5808 76.1872 60.1715 78.7779C62.7622 81.3686 65.8377 83.4236 69.2226 84.8257C72.6075 86.2278 76.2354 86.9494 79.8992 86.9494L79.8992 59.0502L52 59.0502Z' fill='#442781'/><path d='M107.798 59.0502C107.798 62.714 107.077 66.3419 105.675 69.7268C104.273 73.1117 102.218 76.1872 99.6269 78.7779C97.0362 81.3686 93.9606 83.4236 90.5757 84.8257C87.1909 86.2278 83.563 86.9494 79.8992 86.9494L79.8992 59.0502L107.798 59.0502Z' fill='#442781'/><path d='M125 34.9478C125 46.517 115.621 55.8957 104.052 55.8957H83.1043V34.9478C83.1043 23.3787 92.483 14 104.052 14V14C115.621 14 125 23.3787 125 34.9478V34.9478Z' fill='#FF7917'/></svg>
								<span>Public Insurance</span>
							</el-radio>
							<el-radio
								v-model="form.payer"
								label="Insurre"
								name="payer"
							>
								<svg width='40' height='40' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 177.778 100'><path d='M31.941,62.825h25.65V37.175H31.941ZM52.654,47.532h-5.42v-5.42h5.42Zm-15.777-5.42H42.3V52.468H52.654v5.42H36.877Zm37.417-4.937A12.825,12.825,0,1,0,87.119,50,12.84,12.84,0,0,0,74.294,37.175Zm0,20.713A7.888,7.888,0,1,1,82.182,50,7.9,7.9,0,0,1,74.294,57.888Zm58.719-20.713A12.825,12.825,0,1,0,145.837,50,12.84,12.84,0,0,0,133.013,37.175Zm0,20.713A7.888,7.888,0,1,1,140.9,50,7.9,7.9,0,0,1,133.013,57.888Zm-29.36-20.713A12.825,12.825,0,1,0,116.478,50,12.84,12.84,0,0,0,103.653,37.175Zm0,20.713a7.888,7.888,0,1,1,7.488-10.356h-7.488v4.936h7.488A7.9,7.9,0,0,1,103.653,57.888Z' fill='#394149'/></svg>
								<span>Insurre</span>
							</el-radio>

							<b-button
								block
								variant="primary"
								native-type="submit"
							>
								Add Payer
							</b-button>
						</el-form>
					</b-modal>
				</b-col>
			</b-row>
		</div>
	`,
	data() {
		return {
			loading: false,
			payers: [],
			form: {
				payer: ""
			}
		};
	},
	computed: {
		syncDate() {
			// as a dummy date always show today's
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
		// to fully delete organization we also need to delete related patient with cascade,
		// but we don't have patient for this organization, so first pull one and then delete.
		// should be considered as workaround.
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
};
