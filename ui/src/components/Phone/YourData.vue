<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import ActionMenu from "./ActionMenu.vue";
import ProgressScreen from "./ProgressScreen.vue";
import CustomDialogWithClose from "./CustomDialogWithClose.vue";
import { PayerModule } from "@/store/modules/payer";
import { Payer } from "@/types";
import { PayersModule } from "@/store/modules/payers";
import { goToImportOrHome, showDefaultErrorNotification } from "@/utils/utils";
import { CallsModule } from "@/store/modules/calls";

export default defineComponent({
	components: {
		ProgressScreen,
		ActionMenu,
		CustomDialogWithClose
	},
	emits: ["add-payer-data"],
	setup() {
		const menuOpened = ref<boolean>(false);
		const showConfirm = ref<boolean>(false);
		const isRemoving = ref<boolean>(false);

		const payerList = computed<Payer[]>(() => PayersModule.importedPayers);
		const percentage = computed<number>(() => PayerModule.progressStatus?.type === "CLEAR" ? PayerModule.progressPercentage : 0);

		const removeAllData = async (): Promise<void> => {
			showConfirm.value = false;
			isRemoving.value = true;

			const progress = await PayerModule.removeAllData();
			if (progress.status === "FAILED") {
				showDefaultErrorNotification();
			}

			if (progress.status === "COMPLETED") {
				CallsModule.clearPreviousCallList();

				setTimeout(async () => {
					await PayersModule.loadPayers();
					await goToImportOrHome();
				}, 1000);
			}
		};

		const checkProgressStatus = () => {
			PayerModule.progressStatus && PayerModule.progressStatus.type === "CLEAR" && PayerModule.progressStatus.status !== "COMPLETED" ? isRemoving.value = true : isRemoving.value = false;
		};

		checkProgressStatus();

		return {
			menuOpened,
			showConfirm,
			isRemoving,
			payerList,
			removeAllData,
			percentage
		};
	}
});
</script>

<template>
	<div class="your-data">
		<div class="header">
			<div class="header-text">
				Your Data
			</div>
			<van-button
				:icon="require('@/assets/images/icon-three-dots.svg')"
				size="mini"
				@click="menuOpened = !menuOpened"
			/>
		</div>

		<ProgressScreen
			v-if="isRemoving"
			:percentage="percentage"
			title="Clear All Data in progress"
			description="Please wait, it may take a few seconds to delete all your information."
		/>
		<div
			v-else
			class="scroll-area"
		>
			<van-cell-group v-if="payerList.length > 1">
				<van-cell
					is-link
					to="/all-data"
				>
					<div class="cell-title">
						<div class="main">
							All Data
						</div>
						<div class="sub">
							Combined data imported from all payer
						</div>
					</div>
				</van-cell>
			</van-cell-group>

			<h2
				v-if="payerList.length > 1"
				class="section-header"
			>
				By Payer
			</h2>

			<van-cell-group class="payer-list">
				<van-cell
					v-for="(payer, index) in payerList"
					:key="index"
					is-link
					:to="`/payer/${payer.id}`"
				>
					<div class="cell-title">
						<div class="main">
							{{ payer.name }}
						</div>
						<div class="sub">
							Imported at {{ $filters.formatDate(payer.lastImported) }}
						</div>
					</div>
				</van-cell>
			</van-cell-group>

			<div class="actions">
				<van-button
					type="primary"
					class="add-payer-data"
					@click="$emit('add-payer-data')"
				>
					Add Another Payer
				</van-button>
			</div>
		</div>

		<CustomDialogWithClose
			:show="showConfirm"
			title="Are you sure?"
			show-cancel-button
			confirm-button-text="Delete"
			cancel-button-text="Cancel"
			@cancel="showConfirm = false"
			@confirm="removeAllData"
		>
			Once you confirm, all imported payer data will be permanently deleted.
		</CustomDialogWithClose>
		<ActionMenu
			v-model:show="menuOpened"
			@clear-all="showConfirm = true"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts";

.your-data {
	background-color: $black-russian;
	height: 100%;
	display: flex;
	flex-direction: column;

	::v-deep(.van-cell) {
		padding: 20px $global-margin-large;
		border-bottom: 1px solid $platinum;

		&:first-child {
			border-top: 1px solid $platinum;
		}
	}

	.header {
		height: 80px;
		display: flex;
		justify-content: space-between;
		align-items: flex-end;
		background-color: $mirage;
		border-bottom: 1px solid $platinum;
		padding: 0 30px 10px 30px;
		position: relative;

		&-text {
			font-size: $global-xxlarge-font-size;
			font-weight: $global-font-weight-medium;
		}

		::v-deep(.van-icon__image) {
			height: 4px;
			width: 16px;
			vertical-align: super;
		}
	}

	.cell-title {
		.main {
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-normal;
		}

		.sub {
			font-size: $global-font-size;
			font-weight: $global-font-weight-light;
			margin-top: 8px;
		}
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		font-size: 13px;
		font-weight: $global-font-weight-light;
		padding: 25px 0 $global-margin-small $global-margin-large;
		text-transform: uppercase;
		margin: 0;
	}

	.scroll-area {
		display: flex;
		flex-direction: column;
		flex: 1;
		overflow: overlay;
		padding-top: $global-margin-medium;
	}

	.payer-list {
		overflow: overlay;
	}

	.actions {
		margin: 25px $global-margin-large;
	}
}
</style>
