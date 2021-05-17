<script lang="ts">
import { defineComponent, ref, computed, watch } from "vue";
import PayerHeader from "./PayerHeader.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import ResourceList from "@/components/Phone/ResourceList.vue";
import ProgressScreen from "../ProgressScreen.vue";
import CustomDialogWithClose from "../CustomDialogWithClose.vue";
import Mappings from "@/utils/resourceMappings";
import { PatientModule } from "@/store/modules/patient";
import { PayerModule } from "@/store/modules/payer";
import { PayersModule } from "@/store/modules/payers";
import { CallsModule } from "@/store/modules/calls";
import _ from "@/vendors/lodash";
import { goToImportOrHome, showDefaultErrorNotification } from "@/utils/utils";
import router from "@/router";
import PayerRefreshToolbar from "@/components/Phone/PayerRefreshToolbar.vue";

export default defineComponent({
	components: {
		ResourceList,
		CollapseGroup,
		PayerHeader,
		ProgressScreen,
		CustomDialogWithClose,
		PayerRefreshToolbar
	},
	props: {
		id: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const payerId = computed(() => props.id);

		const activePayer = computed(() => PayersModule.activePayer);

		watch(activePayer, payer => {
			if (payer) {
				PatientModule.getPatient({ payerId: payer.id, patientId: payer.sourcePatientId! });
				PayerModule.getResourcesOverview(payer.id);
			}
		}, { immediate: true });


		const mappedPatient = computed<{ [key: string]: { label: string, value: string } } | null>(() => PatientModule.patient ? Mappings.Patient.convert(PatientModule.patient) : null);

		const omitPatientName = (patientInfo: object): object => _.omit(patientInfo, "name");

		const showRemovalConfirm = ref<boolean>(false);
		const removeInProgress = ref<boolean>(false);
		const removalPercentage = computed<number>(() => PayerModule.progressStatus?.type === "CLEAR" ? PayerModule.progressPercentage : 0);
		const removePayerData = async () => {
			removeInProgress.value = true;
			showRemovalConfirm.value = false;
			const progress = await PayerModule.removePayerData(activePayer.value!.id);

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

		const goToResource = (resourceType: string) => {
			router.push(`/payer/${payerId.value}/${resourceType}`);
		};

		const resourcesOverview = computed(() => PayerModule.supportedResourcesOverview);

		const refreshInProgress = computed<boolean>(() => PayerModule.importProgress?.type === "REFRESH" || false);
		const refreshPercentage = computed<number>(() => refreshInProgress.value ? PayerModule.progressPercentage : 0);

		const showingImportOverview = ref(false);
		watch(refreshInProgress, async inProgress => {
			if (!inProgress) {
				await PayersModule.loadPayers();
				showImportOverview(true);
			}
		});

		const showImportOverview = (show: boolean) => {
			showingImportOverview.value = show;
			if (show) {
				PayerModule.getImportOverview(activePayer.value!);
			}
		};
		const importOverview = computed(() => PayerModule.importOverview || []);
		const totalImportOverview = computed<{ createdCount: number, updatedCount: number }>(() =>
			importOverview.value.reduce((acc, item) => ({
				createdCount: acc.createdCount + item.createdCount,
				updatedCount: acc.updatedCount + item.updatedCount
			}), { createdCount: 0, updatedCount: 0 })
		);

		const refresh = async () => {
			if (!activePayer.value) {
				return;
			}

			try {
				await PayerModule.refreshPayerData(activePayer.value);
			} catch {
				showDefaultErrorNotification();
				PayerModule.setPayerAuthInProgress(false);
			}
		};

		return {
			activePayer,
			goToResource,
			removeInProgress,
			removalPercentage,
			removePayerData,
			showRemovalConfirm,
			mappedPatient,
			omitPatientName,
			resourcesOverview,
			refreshInProgress,
			refreshPercentage,
			showingImportOverview,
			showImportOverview,
			importOverview,
			totalImportOverview,
			refresh
		};
	}
});
</script>

<template>
	<div
		v-if="activePayer"
		class="payer"
	>
		<PayerHeader
			:class="{'eclipse': removeInProgress }"
			:title="activePayer.name"
			:actions-disabled="refreshInProgress"
			@show-remove-payer-dialog="showRemovalConfirm = true"
		/>
		<div
			v-if="!removeInProgress"
			class="line"
		></div>
		<div
			v-if="!removeInProgress"
			class="scroll-area"
		>
			<PayerRefreshToolbar
				:refresh-in-progress="refreshInProgress"
				:refresh-percentage="refreshPercentage"
				:showing-import-overview="showingImportOverview"
				:import-overview="totalImportOverview"
				:last-imported="activePayer.lastImported"
				@refresh="refresh"
				@update:showing-import-overview="showImportOverview"
			/>

			<h2 class="section-header">
				GENERAL INFO
			</h2>
			<CollapseGroup
				v-if="mappedPatient"
				:items="[mappedPatient]"
			>
				<template #title="{ item }">
					<div class="field patient-title">
						<div class="label">
							Patient name
						</div>
						<div
							class="value"
							:class="{ 'no-data': !item.name.value }"
						>
							{{ item.name.value || "no data" }}
						</div>
					</div>
				</template>

				<template #default="{ item }">
					<div
						v-for="(field, index) in omitPatientName(item)"
						:key="index"
						class="field"
					>
						<div class="label">
							{{ field.label }}
						</div>
						<div
							:class="{ 'no-data': !field.value }"
							class="value"
						>
							{{ field.value || "no data" }}
						</div>
					</div>
				</template>
			</CollapseGroup>

			<h2 class="section-header">
				RESOURCES
			</h2>
			<ResourceList
				v-if="resourcesOverview.length > 0"
				:resources="resourcesOverview"
				:import-overview="showingImportOverview ? importOverview : null"
				@click-resource="goToResource"
			/>
			<div
				v-else
				class="no-resources"
			>
				<div class="icon"></div>
				<div class="primary">
					No data to display
				</div>
				<div class="secondary">
					There are no resources from this payer.
				</div>
			</div>
		</div>

		<CustomDialogWithClose
			:show="showRemovalConfirm"
			title="Are you sure?"
			show-cancel-button
			confirm-button-text="Delete"
			cancel-button-text="Cancel"
			@cancel="showRemovalConfirm = false"
			@confirm="removePayerData"
		>
			Once you confirm, imported payer data will be permanently deleted.
		</CustomDialogWithClose>

		<ProgressScreen
			v-if="removeInProgress"
			:percentage="removalPercentage"
			title="Clear Data in progress"
			description="Please wait, it may take a few seconds to delete all your information."
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.payer {
	background-color: $black-russian;
	position: relative;
	display: flex;
	flex-direction: column;
	height: 100%;

	.line {
		background-color: $black-russian;
		height: 20px;
	}

	.scroll-area {
		overflow-y: overlay;
		flex: 1;
		display: flex;
		flex-direction: column;

		> * {
			flex-shrink: 0;
		}
	}

	.no-resources {
		padding: $global-margin-large $global-margin-medium;
		flex: 1;
		background-color: $mirage;
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;

		.icon {
			color: $pinkish-grey;

			@include mask-icon("~@/assets/images/no-data.svg", 100px);
		}

		.primary {
			font-size: $global-xxlarge-font-size;
			font-weight: $global-font-weight-medium;
			margin-top: $global-margin-large;
		}

		.secondary {
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-normal;
			margin-top: $global-margin-small;
		}
	}
}

.eclipse {
	opacity: .2;
}

::v-deep(.van-overlay) {
	backdrop-filter: blur(3px);
}

.section-header {
	height: 50px;
	background-color: $black-russian;
	border: 1px solid $platinum;
	font-size: 13px;
	font-weight: 300;
	padding: 25px 0 0 30px;
	text-transform: uppercase;
	margin: 0;
}

.field:not(:first-child) {
	margin-top: 13px;
}

.label {
	font-size: 14px;
	font-weight: 300;
	margin-bottom: 8px;
}

.value {
	font-size: 18px;
	font-weight: 400;

	@include dont-break-out();
}

.patient-title {
	padding: 13px 0;
}

.no-data {
	color: $pinkish-grey;
}
</style>
