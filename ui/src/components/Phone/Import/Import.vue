<script lang="ts">
import { defineComponent, ref, computed, watch } from "vue";
import { PayersModule } from "@/store/modules/payers";
import { PayerModule } from "@/store/modules/payer";
import PayerSelection from "./PayerSelection.vue";
import ProgressScreen from "@/components/Phone/ProgressScreen.vue";
import { Payer } from "@/types";
import AppConfirmDialog from "@/components/AppConfirmDialog.vue";
import { handleMobileLogout } from "@/utils/handleMobileLogout";
import { showDefaultErrorNotification } from "@/utils/utils";

export default defineComponent({
	components: { AppConfirmDialog, PayerSelection, ProgressScreen },
	setup() {
		const phase = ref<"select" | "import" | "finish">(PayerModule.importProgress && PayerModule.importProgress.type === "IMPORT" && PayerModule.importProgress.status !== "FAILED" ? "import" : "select");
		const chosenPayerId = ref<number | null>(null);

		watch(() => PayerModule.importProgress, (newVal, oldVal) => {
			if (newVal?.status === "FAILED") {
				phase.value = "select";
				showDefaultErrorNotification();
			} else if (newVal && !oldVal) {
				phase.value = "import";
			} else if (oldVal && !newVal) {
				phase.value = "finish";
				PayersModule.loadPayers();
			}
		});

		const percentage = computed<number>(() => PayerModule.progressPercentage);
		const canLogout = computed<boolean>(() => phase.value !== "import");
		const canGoBack = computed<boolean>(() => phase.value !== "import" && PayersModule.importedPayers.length > 0);
		const chosenPayerName = computed<string | undefined>(() => {
			const payerId = chosenPayerId.value === null ? PayersModule.activePayerId : chosenPayerId.value;
			const payer = PayersModule.payers.find(p => p.id === payerId);

			return payer ? payer.name : "";
		});
		const activePayerId = computed<number | null>(() => PayersModule.activePayerId);

		const startImport = async (payer: Payer) => {
			phase.value = "import";
			chosenPayerId.value = payer.id;
			await PayersModule.updateActivePayerId(payer.id);
			try {
				const progress = await PayerModule.importPayerData(payer);
				if (progress.status === "COMPLETED") {
					await PayersModule.loadPayers();
				}
			} catch {
				phase.value = "select";
				PayerModule.setPayerAuthInProgress(false);
				showDefaultErrorNotification();
			}
		};

		const { confirmOptions, showDialog, handleLogout, clearAllRequests } = handleMobileLogout();

		return {
			phase,
			chosenPayerId,
			canLogout,
			canGoBack,
			chosenPayerName,
			confirmOptions,
			showDialog,
			handleLogout,
			startImport,
			clearAllRequests,
			percentage,
			activePayerId
		};
	}
});
</script>

<template>
	<div class="import">
		<div class="header">
			<van-nav-bar>
				<template #left>
					<van-button
						:icon="require('@/assets/images/arrow-left.svg')"
						size="mini"
						:disabled="!canGoBack"
						@click="$router.push('/')"
					/>
				</template>
				<template #right>
					<van-button
						class="logout"
						:disabled="!canLogout"
						@click="handleLogout"
					>
						Log Out
					</van-button>
				</template>
			</van-nav-bar>
		</div>

		<div class="content">
			<ProgressScreen
				v-if="phase === 'import'"
				title="Data Import In Progress"
				:percentage="percentage"
				description="Please wait, data import may take a few minutes to complete."
			/>
			<PayerSelection
				v-if="phase === 'select'"
				@import="startImport"
			/>
			<transition
				name="fade"
				appear
			>
				<div
					v-if="phase === 'finish'"
					class="import-completed"
				>
					<h2 class="title">
						Data Import Completed!
					</h2>
					<p class="description">
						Data was successfully imported from the <span class="payer-name">{{ chosenPayerName }}</span>.
					</p>
					<p class="finish-icon-container">
						<i class="finish-icon">
							<i class="done-icon"></i>
						</i>
					</p>
					<van-button
						type="primary"
						@click="$router.push(`/payer/${chosenPayerId ? chosenPayerId : activePayerId}`)"
					>
						View Imported Data
					</van-button>
				</div>
			</transition>
		</div>
		<AppConfirmDialog
			:show-dialog="showDialog"
			:options="confirmOptions"
			@hide-dialog="showDialog = false"
			@confirm="clearAllRequests"
		/>
	</div>
</template>


<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.import {
	height: 100%;
	display: flex;
	flex-direction: column;

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

	.content {
		flex: 1;
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
			@include mask-icon("~@/assets/images/done.svg", 32px, 27px);
		}
	}
}

.fade-enter-active,
.fade-leave-active {
	transition: opacity 3s;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
}
</style>
