<script lang="ts">
import { defineComponent } from "vue";
import jwt_decode from "jwt-decode";
import ProgressScreen from "../components/Phone/ProgressScreen.vue";

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
				const decodedData: any = !data.patient ? jwt_decode(data.id_token) : "";
				const fhirUser = decodedData.fhirUser;
				const patientId = !decodedData ? data.patient : (fhirUser.startsWith("Patient/") ? fhirUser.slice(fhirUser.indexOf("/") + 1) : "");
				const accessToken = data.access_token;

				this.importPayerData(this.payer.id, patientId, accessToken);
			});
		},
		//
		// import payer data
		//
		importPayerData(payerId: any, patientId: any, accessToken: any) {
			this.$store.dispatch("importPayerData", { payerId, patientId, accessToken })
			.then((data: any) => {
				this.isImporting = false;
				this.$store.dispatch("changeServerLastImportedDate", { id: this.payer.id, lastImported: new Date() });
			});
		}
	}
});
</script>

<template>
	<div class="header">
		<van-nav-bar>
			<template #left>
				<img
					src="~@/assets/images/arrow.svg"
					alt="Return to previous screen"
					@click="$router.push('/')"
				>
			</template>
			<template #right>
				<van-cell
					clickable
					class="logout"
					@click="handleLogout"
				>
					Log Out
				</van-cell>
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
			<h2>Data Import Completed!</h2>
			<p>Data ware successfully imported from the {{ payer.name }}.</p>
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

.header {
	height: 80px;
	background-color: $mirage;
	display: flex;
	align-items: flex-end;

	.logout {
		padding: 0;

		::v-deep(.van-cell__value--alone) {
			color: $active-color;
		}
	}
}

.import-completed {
	padding: 0 $global-margin-large;
}
</style>
