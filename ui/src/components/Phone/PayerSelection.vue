<script lang="ts">
import { defineComponent } from "vue";
import CustomRadioButton from "./CustomRadioButton.vue";

function createUUID() {
	return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, c => {
		const r = Math.random() * 16 | 0,
			v = c == "x" ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}

export default defineComponent( {
	name: "PayerSelection",
	components: {
		CustomRadioButton
	},
	emits: ["hide-payer-select"],
	data() {
		return {
			selectedPayerId: ""
		};
	},
	computed: {
		importedPayers() {
			return this.$store.getters.importedPayers;
		},
		payerList() {
			return this.$store.getters.servers.filter((payer: any) => payer.lastImported === null);
		},
		importButtonDisabled(): boolean {
			return !this.selectedPayerId;
		},
		selectedPayer(): any {
			return this.payerList.find((item: any) => item.id === this.selectedPayerId);
		}
	},
	watch: {
		payerList(val) {
			if (this.selectedPayerId && !val.find(payer => payer.id === this.selectedPayerId)) {
				this.selectedPayerId = "";
			}
		}
	},
	methods: {
		//
		// simply logout and redirect to login page
		//
		handleLogout() {
			this.$store.dispatch("authLogout").then(() => this.$router.push("/login"));
		},
		//
		// save data about selected payer in localStorage for having access to it after login in payer server
		// redirect to selected payer authorize Uri
		//
		handleImport() {
			localStorage.setItem("selectedPayer", JSON.stringify(this.selectedPayer));

			window.location.href = `${this.selectedPayer.authorizeUri}?
										aud=${this.selectedPayer.fhirServerUri}
										&client_id=${this.selectedPayer.clientId}
										&scope=${this.selectedPayer.scope}
										&state=${createUUID()}
										&response_type=code
										&redirect_uri=http://localhost:8080
									`;
		}
	}
});
</script>

<template>
	<div class="header">
		<van-nav-bar>
			<template
				v-if="importedPayers.length"
				#left
			>
				<van-button
					:icon="require('@/assets/images/arrow-left.svg')"
					size="mini"
					@click="$emit('hide-payer-select')"
				/>
			</template>
			<template #right>
				<van-button
					class="logout"
					@click="handleLogout"
				>
					Log Out
				</van-button>
			</template>
		</van-nav-bar>
	</div>
	<div class="select-payer">
		<img
			class="logo"
			src="~@/assets/images/davinci-logo.svg"
			alt="logo"
		>
		<div class="header-main-text">
			Select Payer
		</div>
		<template
			v-if="payerList.length"
		>
			<div class="header-secondary-text">
				Select a Payer you want to import the data from.
			</div>
			<van-radio-group
				v-model="selectedPayerId"
			>
				<CustomRadioButton
					v-for="(item, index) in payerList"
					:key="index"
					:name="item.id"
				>
					{{ item.name }}
				</CustomRadioButton>
			</van-radio-group>
			<van-button
				type="primary"
				:disabled="importButtonDisabled"
				@click="handleImport"
			>
				Import
			</van-button>
		</template>
		<template
			v-else
		>
			<img
				class="no-data-logo"
				src="~@/assets/images/no-data.svg"
				alt="no-data"
			>
			<div class="no-data-header">
				No data to display
			</div>
			<div class="no-data-text">
				Please configure connection to servers in the Manage Servers panel on the right.
			</div>
		</template>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.header {
	height: 80px;
	background-color: $mirage;
	display: flex;
	align-items: flex-end;

	.logout {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		color: $active-color;
		padding: 0;
		border: none;
		background: none;
	}
}

.select-payer {
	display: flex;
	flex-direction: column;
	padding: 0 $global-margin-large;
	background-color: $mirage;
	height: 100%;

	.logo {
		margin-bottom: $global-margin-xlarge;
		width: 125px;
		align-self: center;
	}

	.no-data-logo {
		margin-bottom: $global-margin-large;
		width: 100px;
		align-self: center;
	}

	.no-data-header,
	.no-data-text {
		text-align: center;
	}

	.header-main-text,
	.no-data-header {
		font-size: $global-xxlarge-font-size;
		font-weight: $global-font-weight-medium;
		margin-bottom: $global-margin-medium;
	}

	.header-secondary-text {
		width: 80%;
	}

	.header-secondary-text,
	.no-data-text {
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
	}

	.van-radio-group {
		margin: 45px 0 50px 0;
		max-height: 205px; // roughly 5 items to display
	}

	::v-deep(.van-radio__label) {
		@include dont-break-out();
	}

	.van-radio + .van-radio {
		margin-top: $global-margin-medium;
	}
}

</style>
