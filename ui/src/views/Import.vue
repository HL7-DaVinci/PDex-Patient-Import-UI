<script lang="ts">
import { defineComponent } from "vue";
import jwt_decode from "jwt-decode";
import ProgressScreen from "../components/Phone/ProgressScreen.vue";
import { showDefaultErrorNotification } from "../utils/utils";

export default defineComponent({
	components: { ProgressScreen },
	data() {
		return {
			code: "",
			payer: JSON.parse(localStorage.getItem("selectedPayer") as any),
			isImporting: true
		};
	},
	mounted() {
		this.getPayerToken();
		localStorage.removeItem("selectedPayer");
	},
	methods: {
		//
		// Log out user and go to login page
		//
		handleLogout() {
			this.$store.dispatch("authLogout").then(() => this.$router.push("/login"));
		},
		//
		// get token for access next request for payer data
		//
		getPayerToken() {
			this.$store.dispatch("getServerToken", { payerId: this.payer.id, authCode: this.$route.redirectedFrom.query.code })
			.then((data: any) => {
				//todo: if we have no patient nor id_token that means something went wrong during auth provider phase.
				//todo: think how we can handle this, think what scenarios can be, cause not all auth providers have same logic during errors.
				//todo: some of them return back with code param, so we start import, but have no tokens. and some just return with errors as params and no code.
				if (!data.patient && !data.id_token) {
					showDefaultErrorNotification();
					this.$router.push("/");
					return;
				}
				let patientId;

				if (data.patient) {
					patientId = data.patient;
				}

				if (data.id_token) {
					const decodedData: any = jwt_decode(data.id_token);
					const fhirUser = decodedData.fhirUser;
					patientId = fhirUser.startsWith("Patient/") ? fhirUser.slice(fhirUser.indexOf("/") + 1) : "";
				}

				this.importPayerData(this.payer.id, patientId, data.access_token);
			});
		},
		//
		// import payer data
		//
		importPayerData(payerId: any, patientId: any, accessToken: any) {
			this.$store.dispatch("importPayerData", { payerId, patientId, accessToken })
			.then((data: any) => {
				this.$store.dispatch("changeServerLastImportedDate", { id: this.payer.id, lastImported: new Date() });
				this.$store.dispatch("loadServers").then(() => this.isImporting = false);
			});
		}
	}
});
</script>

<template>
	<div class="header">
		<van-nav-bar>
			<template #left>
			  <van-button
				  :icon="require('@/assets/images/arrow-left.svg')"
				  size="mini"
				  :disabled="isImporting"
				  @click="$router.push('/')"
			  />
			</template>
			<template #right>
			  <van-button
				  class="logout"
				  :disabled="isImporting"
				  @click="handleLogout"
			  >
				Log Out
			  </van-button>
			</template>
		</van-nav-bar>
	</div>
	<div class="import-page">
		<ProgressScreen
			v-if="isImporting"
			title="Data Import In Progress"
			description="Please wait, data import may take a few minutes to complete."
		/>
		<div v-else class="import-completed">
			<h2 class="title">
				Data Import Completed!
			</h2>
			<p class="description">
				Data were successfully imported from the <span class="payer-name">{{ payer.name }}</span>.
			</p>
			<p class="finish-icon-container">
				<i class="finish-icon">
					<i class="done-icon" />
				</i>
			</p>
			<van-button
				type="primary"
				@click="$router.push(`/payer/${payer.id}`)"
			>
				View Imported Data
			</van-button>
		</div>
	</div>
</template>


<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.header {
	height: 80px;
	background-color: $mirage;
	display: flex;
	align-items: flex-end;

	.btn {
		height: 16px;

		.arrow-icon {
			color: $active-color;

			@include icon("~@/assets/images/arrow-left.svg", 8px, 16px);
		}
	}

	.logout {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		color: $active-color;
		padding: 0;
		border: none;
		background: none;
	}
}

.import-completed {
	padding: 0 $global-margin-large;

	.finish-icon-container {
		margin: 35px 0;
		display: flex;
		justify-content: center;
	}

	.finish-icon {
		display: flex;
		width: 86px;
		height: 86px;
		border: 3px $mobile-default-text-color solid;
		border-radius: 50%;
		justify-content: center;
		align-items: center;
	}

	.done-icon {
		@include icon("~@/assets/images/done.svg", 32px, 27px);
	}
}

.payer-name {
	@include dont-break-out();
}
</style>
