<script lang="ts">
import { defineComponent, ref, computed, watch } from "vue";
import ResourceHeader from "./ResourceHeader.vue";
import SortMenu from "./SortMenu.vue";
import ResourceLoader from "@/utils/ResourceLoader";
import InfiniteScrollArea from "@/components/Phone/InfiniteScrollArea.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import Mappings from "@/utils/resourceMappings.js";
import { PatientModule } from "@/store/modules/patient";
import { PayerModule } from "@/store/modules/payer";
import { PayersModule } from "@/store/modules/payers";
import _ from "@/vendors/lodash";
import { showDefaultErrorNotification } from "@/utils/utils";
import PayerRefreshToolbar from "@/components/Phone/PayerRefreshToolbar.vue";

export default defineComponent({
	components: {
		CollapseGroup,
		InfiniteScrollArea,
		ResourceHeader,
		SortMenu,
		PayerRefreshToolbar
	},
	props: {
		id: {
			type: String,
			required: true
		},
		resourceType: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const activePayer = computed(() => PayersModule.activePayer);
		watch(activePayer, payer => {
			if (payer && payer.sourcePatientId) {
				PatientModule.getPatient({ payerId: payer.id, patientId: payer.sourcePatientId });
				PayerModule.getResourcesOverview(payer.id);
			}
		}, { immediate: true });

		const sortMenuOpened = ref(false);

		const totalResourcesCount = computed<number>(() => PayerModule.supportedResourcesOverview.find(item => item.resourceType === props.resourceType)?.count || 0);

		const availableSortParams = computed(() => Mappings[props.resourceType]?.sortParams || {});
		const availableSortKeys = computed<string[]>(() => Object.keys(availableSortParams.value));
		const sortKey = ref("");
		const sortParam = computed<string>(() => sortKey.value ? availableSortParams.value[sortKey.value] || "" : "");

		const resourceLoader = ref<ResourceLoader | null>(null);
		const updateResourceLoader = () => {
			if (props.resourceType && activePayer.value) {
				resourceLoader.value = new ResourceLoader({ currentPayer: `/fhir/${props.resourceType}?_source=${activePayer.value.id}&_sort=${sortParam.value}` });
				resourceLoader.value.load();
			} else {
				resourceLoader.value = null;
			}
		};
		watch(() => props.resourceType, updateResourceLoader);
		watch(activePayer, updateResourceLoader);
		watch(sortParam, updateResourceLoader);
		updateResourceLoader();

		const rawResources = computed<object[]>(() => resourceLoader.value?.results?.currentPayer || []);
		const resources = computed<object[]>(() => rawResources.value.map(Mappings[props.resourceType].convert));
		const getResourceTitle = (res: object): string => Object.values(res)[0]?.value;

		const mappedPatient = computed<object | null>(() => PatientModule.patient ? Mappings.Patient.convert(PatientModule.patient) : null);
		const omitPatientName = (patientInfo: object): object => _.omit(patientInfo, "name");

		const isResourceLoading = computed<boolean>(() => resourceLoader.value?.loading || false);

		const more = () => {
			resourceLoader.value?.load();
		};

		const refreshInProgress = computed<boolean>(() => PayerModule.importProgress?.type === "REFRESH" || false);
		const refreshPercentage = computed<number>(() => refreshInProgress.value ? PayerModule.progressPercentage : 0);

		const showingImportOverview = ref(false);
		watch(refreshInProgress, async inProgress => {
			if (!inProgress) {
				await PayersModule.loadPayers();
				showImportOverview(true);
				updateResourceLoader();
			}
		});

		const showImportOverview = (show: boolean) => {
			showingImportOverview.value = show;
			if (show) {
				PayerModule.getImportOverview(activePayer.value!);
			}
		};
		const importOverview = computed<{ createdCount: number, updatedCount: number }>(() => (PayerModule.importOverview || [])
			.find(item => item.resourceType === props.resourceType) || { createdCount: 0, updatedCount: 0 });

		const refresh = async () => {
			if (!activePayer.value) {
				return;
			}

			try {
				await PayerModule.refreshPayerData(activePayer.value, props.resourceType);
			} catch {
				showDefaultErrorNotification();
				PayerModule.setPayerAuthInProgress(false);
			}
		};

		return {
			activePayer,
			sortMenuOpened,
			sortKey,
			totalResourcesCount,
			availableSortKeys,
			isResourceLoading,
			mappedPatient,
			omitPatientName,
			resources,
			getResourceTitle,
			refreshInProgress,
			refreshPercentage,
			showingImportOverview,
			showImportOverview,
			importOverview,
			refresh,
			more
		};
	}
});
</script>

<template>
	<div class="resource">
		<ResourceHeader
			:resource-type="resourceType"
			:payer-id="id"
			:total="totalResourcesCount"
			:sort-enabled="availableSortKeys.length > 0"
			@openSortMenu="sortMenuOpened = true"
		/>
		<InfiniteScrollArea
			class="scroll-area"
			:loading="isResourceLoading"
			@more="more"
		>
			<PayerRefreshToolbar
				:refresh-in-progress="refreshInProgress"
				:refresh-percentage="refreshPercentage"
				:showing-import-overview="showingImportOverview"
				:import-overview="importOverview"
				:last-imported="activePayer?.lastImported || ''"
				class="refresh-toolbar"
				@refresh="refresh"
				@update:showing-import-overview="showImportOverview"
			/>

			<h2 class="section-header">
				GENERAL INFO
			</h2>
			<CollapseGroup
				v-if="mappedPatient"
				:items="[mappedPatient]"
				class="section-content"
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
							{{ item.name.value ? item.name.value : "no data" }}
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
			<CollapseGroup
				:items="resources"
				class="section-content"
			>
				<template #title="{ item }">
					<div
						:class="{ 'no-data': !getResourceTitle(item) }"
						class="resource-title"
					>
						{{ getResourceTitle(item) || "no data" }}
					</div>
				</template>

				<template
					#default="{ item }"
				>
					<div
						v-for="(field, index) in item"
						:key="index"
						class="field resource-field"
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
		</InfiniteScrollArea>
		<SortMenu
			v-model:show="sortMenuOpened"
			v-model:value="sortKey"
			:options="availableSortKeys"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.resource {
	background-color: $black-russian;
	height: 100%;
	display: flex;
	flex-direction: column;

	.resource-title {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		padding: $global-margin-medium 0;
	}

	.resource-field {
		margin-left: 15px;
	}

	.field:not(:first-child) {
		margin-top: 13px;
	}

	.label {
		font-size: $global-font-size;
		font-weight: $global-font-weight-light;
		margin-bottom: 8px;
	}

	.value {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;

		@include dont-break-out();
	}

	.no-data {
		color: $pinkish-grey;
	}

	.patient-title {
		padding: 13px 0;
	}

	.scroll-area {
		flex: 1;
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		font-size: 13px;
		font-weight: 300;
		padding: 25px 0 0 30px;
		text-transform: uppercase;
		margin: 0;
	}

	.section-content {
		border: 1px solid $platinum;
		background-color: $mirage;
	}

	.refresh-toolbar {
		margin-top: $global-margin-medium;
	}
}
</style>
