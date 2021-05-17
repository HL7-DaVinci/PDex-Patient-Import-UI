<script lang="ts">
import { defineComponent, computed, ref, watch, reactive } from "vue";
import CallList from "./CallList.vue";
import CallDetails from "@/components/debug/CallDetails/CallDetails.vue";
import AppConfirmDialog from "@/components/AppConfirmDialog.vue";
import { CallsModule } from "@/store/modules/calls";
import { Call, CallWithExtendedData, SearchOption } from "@/types";
import { PayersModule } from "@/store/modules/payers";
import { getTotalMatches, getTotalURLMatches } from "@/utils/prepareCallsData";
import _ from "@/vendors/lodash";

export default defineComponent({
	components: { CallList, CallDetails, AppConfirmDialog },
	emits: ["back-to-servers"],
	setup(props, { emit }) {
		const activeTab = ref<string>("fhir");
		const showDialog = ref<boolean>(false);
		const searchValue = ref<string>("");
		const searchMode = ref<boolean>(false);

		const callList = computed<Call[]>(() => CallsModule.callList);
		const fhirList = computed<Call[]>(() => CallsModule.fhirList);
		const preparedFhirList = computed<CallWithExtendedData[]>(() => CallsModule.preparedFhirList);
		const fhirLoading = computed<boolean>(() => CallsModule.callList.some(call => call.requestStatus === "PENDING"));
		const authList = computed<Call[]>(() => CallsModule.authList);
		const preparedAuthList = computed<CallWithExtendedData[] >(() => CallsModule.preparedAuthList);
		const authLoading = computed<boolean>(() => CallsModule.authList.some(call => call.requestStatus === "PENDING"));
		const activeCallId = computed<string | null>(() => CallsModule.activeCallId);
		const activeCall = computed<CallWithExtendedData | null>(() => CallsModule.activeCall);
		const importedPayer = computed(() => CallsModule.lastImportedPayer);
		const totalMatches = computed<number>(() => getTotalMatches([...preparedFhirList.value, ...preparedAuthList.value]));
		const totalUrlMatches = computed<number>(() => getTotalURLMatches([...preparedFhirList.value, ...preparedAuthList.value]));
		const showMatches = computed<boolean>({
			get: () => CallsModule.showMatches,
			set: val => CallsModule.setShowMatches(val)
		});

		const handleSearch = (): void => {
			searchMode.value = Boolean(searchValue.value);
			CallsModule.setSearchedValue(searchValue.value.toLowerCase());
		};

		watch(searchValue, _.debounce(handleSearch, 300));

		watch(() => activeCall.value === null, (val: boolean) => {
			if (val) {
				CallsModule.setActiveCallId(null);
			}
		});

		// clear search field if we cleared list of calls or started new import
		const noCalls = computed<boolean>(() => callList.value.length === 0);
		watch(noCalls, () => {
			if(noCalls.value) {
				searchValue.value = "";
			}
		});

		watch(activeTab, () => {
			CallsModule.setActiveCallId(null);
		});

		const handleManage = (): void => {
			searchValue.value = "";
			emit("back-to-servers");
		};
		const handleCallSelect = (id: string): void => CallsModule.setActiveCallId(id);

		const clearAllRequests = (): void => {
			CallsModule.clearList();
			showDialog.value = false;
		};

		const searchOptions = computed<SearchOption[]>(() => CallsModule.searchOptions);

		// add new value to search suggestions
		const addOption = (): void => CallsModule.addSearchOption(searchValue.value.toLowerCase());

		// return list of suggested values for search
		const getSearchOptions = (queryString: string, cb: (arr: SearchOption[]) => (SearchOption[])) => {
			const results = queryString && searchOptions.value.length
				? searchOptions.value.filter(option => option.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
				: searchOptions.value;
			// call callback function to return suggestions
			cb(_.sortBy(results, ["value"]));
		};

		return {
			showMatches,
			searchValue,
			activeTab,
			noCalls,
			fhirList,
			preparedFhirList,
			fhirLoading,
			authList,
			preparedAuthList,
			authLoading,
			activeCallId,
			activeCall,
			importedPayer,
			showDialog,
			totalMatches,
			totalUrlMatches,
			searchMode,
			handleCallSelect,
			handleManage,
			clearAllRequests,
			getSearchOptions,
			addOption
		};
	}
});
</script>

<template>
	<div class="debug-panel">
		<div class="header">
			<span
				v-if="importedPayer"
				class="active-payer"
			>
				<span class="payer-name">{{ importedPayer.name }}</span>
				<span class="payer-uri">{{ importedPayer.fhirServerUri }}</span>
			</span>
			<span v-else>
				Server URI of the last imported server will appear here.
			</span>
			<el-button
				size="small"
				@click="handleManage"
			>
				Manage
			</el-button>
		</div>

		<div class="toolbar">
			<el-button
				size="small"
				:disabled="!fhirList.length"
				@click="showDialog = true"
			>
				<span class="icon-clear-all"></span>
				<span>Clear all</span>
			</el-button>
			<el-autocomplete
				v-model="searchValue"
				class="search-field"
				placeholder="Search..."
				:clearable="true"
				:disabled="noCalls"
				:fetch-suggestions="getSearchOptions"
				:trigger-on-focus="false"
				@focusout="addOption"
			>
				<template #prefix>
					<span class="icon-search"></span>
				</template>
				<template
					v-if="searchMode"
					#suffix
				>
					<span
						v-if="totalMatches === 0"
						class="total-matches"
					>
						No results
					</span>
					<span
						v-else
						class="total-matches"
					>
						{{ totalMatches }} found
					</span>
				</template>
			</el-autocomplete>
			<div class="match-switcher">
				<span class="switch-label">Show matches only</span>
				<el-switch
					v-model="showMatches"
					:disabled="noCalls"
					:width="46"
				/>
			</div>
		</div>

		<div class="splitpanes-wrapper">
			<splitpanes horizontal="horizontal">
				<pane class="resizable-area">
					<div
						v-if="searchMode"
						class="total-search-info"
					>
						<span class="icon-search"></span>
						<span v-if="totalUrlMatches">{{ totalUrlMatches }} found in URL</span>
						<span v-else>No results found in URL</span>
					</div>
					<el-tabs v-model="activeTab">
						<el-tab-pane name="fhir">
							<template #label>
								<span class="tab-title">
									FHIR Requests ({{ searchMode ? `${preparedFhirList.length} / ${fhirList.length}` : `${fhirList.length}` }})
								</span>
								<i
									v-if="fhirLoading"
									class="el-icon-loading icon"
								>
								</i>
							</template>
							<CallList
								:data="preparedFhirList"
								:active-id="activeCallId"
								:search-mode="searchMode"
								type="FHIR"
								@select="handleCallSelect"
							/>
						</el-tab-pane>
						<el-tab-pane name="auth">
							<template #label>
								<span class="tab-title">
									Authorization Requests ({{ searchMode ? `${preparedAuthList.length} / ${authList.length}` : `${authList.length}` }})
								</span>
								<i
									v-if="authLoading"
									class="el-icon-loading icon"
								>
								</i>
							</template>
							<CallList
								:data="preparedAuthList"
								:active-id="activeCallId"
								:search-mode="searchMode"
								type="Authorization"
								@select="handleCallSelect"
							/>
						</el-tab-pane>
					</el-tabs>
				</pane>
				<pane
					v-if="activeTab === 'fhir' ? preparedFhirList.length : preparedAuthList.length"
					min-size="20"
				>
					<CallDetails
						:active-call="activeCall"
						:search-mode="searchMode"
						:call-type="activeTab === 'fhir' ? 'FHIR' : 'Authorization'"
					/>
				</pane>
			</splitpanes>
		</div>
		<AppConfirmDialog
			:show-dialog="showDialog"
			:options="{
				primaryText: 'Are you sure you want to clear all FHIR and Authorization Requests?',
				secondaryText: 'Clearing FHIR and Authorization Requests will also erase details.',
				title: 'Clear all FHIR and Authorization Requests',
				primaryButton: 'Clear all',
				secondaryButton: 'Cancel'
			}"
			@hide-dialog="showDialog = false"
			@confirm="clearAllRequests"
		/>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.debug-panel {
	height: 100%;
	overflow: hidden;
	display: flex;
	flex-direction: column;

	.header {
		height: 60px;
		flex-shrink: 0;
		background-color: $white-smoke-2;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 $global-margin-medium;
		font-size: $global-font-size;
		font-weight: $global-font-weight-normal;
		border-bottom: $global-base-border;

		.active-payer {
			display: flex;
			align-items: center;
			flex: 1 1;
			overflow: hidden;

			.payer-name {
				max-width: 200px;
				flex-shrink: 1;

				@include text-ellipsis();
			}

			.payer-uri {
				font-weight: $global-font-weight-light;
				padding: $global-margin-xsmall $global-margin-small;
				background-color: $white;
				border: 1px solid $whisper;
				border-radius: 5px;
				margin: 0 $global-margin-xsmall;
				flex-grow: 1;
				flex-shrink: 1;

				@include text-ellipsis();
			}
		}
	}

	.splitpanes-wrapper {
		flex: 1;
		overflow: hidden;
		position: relative;
	}

	.resizable-area {
		min-height: 140px;
	}

	::v-deep(.el-tabs) {
		display: flex;
		height: 100%;
		flex-direction: column;
		position: relative;
		z-index: 0;
	}

	::v-deep(.el-tabs__content) {
		flex: 1;
		overflow-y: overlay;
		margin-right: 10px;
	}

	::v-deep(.el-tabs__header) {
		margin: 0;
	}

	.splitpanes--horizontal > ::v-deep(.splitpanes__splitter::before) {
		top: -10px;
		bottom: -10px;
	}

	.icon {
		position: absolute;
		right: 20px;
		top: 12px;
	}

	.tab-title {
		padding: 0 20px;
	}

	.total-search-info {
		position: absolute;
		right: 0;
		color: $silver;
		padding: $global-margin $global-margin-medium;
		font-size: $global-small-font-size;

		.icon-search {
			margin-right: $global-margin-xsmall;
			background-color: $silver;

			@include mask-icon("~@/assets/images/icon-search.svg", 11px);
		}
	}

	.toolbar {
		border-top: 1px solid $whisper;
		border-bottom: 1px solid $whisper;
		background-color: $white-smoke-3;
		padding: $global-margin-small $global-margin-medium;
		display: flex;
		justify-content: space-between;

		.icon-clear-all {
			margin-right: $global-margin-xsmall;

			@include mask-icon("~@/assets/images/icon-trash-debug-panel.svg", 9px, 11px);
		}

		::v-deep(.el-autocomplete).search-field {
			margin: 0 $global-margin-medium;
			flex-grow: 1;

			.el-input__prefix {
				padding: $global-margin-xsmall;
			}

			.total-matches {
				line-height: $global-form-line-height;
				padding: 0 25px;
			}

			.el-input__clear {
				position: absolute;
				right: 0;
			}

			.icon-search {
				background-color: $grey;

				@include mask-icon("~@/assets/images/icon-search.svg", 11px);
			}

			&.is-disabled .icon-search {
				background-color: $pinkish-grey;
			}
		}

		.match-switcher .switch-label {
			margin-right: $global-margin-small;
			font-size: $global-font-size;
			line-height: $global-form-line-height;
		}
	}
}
</style>
