<script lang="ts">
import { defineComponent, ref, watch, computed } from "vue";
import CustomRadioButton from "@/components/Phone/CustomRadioButton.vue";
import { showDefaultErrorNotification } from "@/utils/utils";
import { Payer } from "@/types";
import { PayersModule } from "@/store/modules/payers";
import { PayerModule } from "@/store/modules/payer";

export default defineComponent( {
	name: "PayerSelection",
	components: { CustomRadioButton },
	emits: ["import"],
	setup(_, { emit }) {
		const selectedPayerId = ref<number | null>(null);

		const payerList = computed<Payer[]>(() => PayersModule.notImportedPayers);
		const selectedPayer = computed<Payer | undefined>(() => payerList.value.find(payer => payer.id === selectedPayerId.value));
		const importButtonDisabled = computed<boolean>(() => !selectedPayerId.value || payerAuthInProgress.value);
		const payerAuthInProgress = computed<boolean>(() => PayerModule.payerAuthInProgress);

		watch(payerList, val => {
			if (selectedPayerId.value && !val.find(payer => payer.id === selectedPayerId.value)) {
				selectedPayerId.value = null;
			}
		});

		const handleImport = async () => {
			try {
				emit("import", selectedPayer.value);
			} catch {
				showDefaultErrorNotification();
			}
		};

		return {
			payerAuthInProgress,
			selectedPayerId,
			payerList,
			selectedPayer,
			importButtonDisabled,
			handleImport
		};
	}
});
</script>

<template>
	<div class="payer-selection">
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
				:loading="payerAuthInProgress"
				loading-text="Authorization..."
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

.payer-selection {
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
