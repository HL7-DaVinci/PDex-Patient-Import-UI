<script lang="ts">
import { defineComponent, ref, computed, watch, PropType, toRefs } from "vue";
import { CallWithExtendedData, Section } from "@/types";
import CallDetailsField from "./CallDetailsField.vue";
import ResponseContentSection from "./ResponseContentSection.vue";

const RESPONSE_CONTENT_SECTION_KEY = "response-content";

export default defineComponent({
	components: {
		CallDetailsField,
		ResponseContentSection
	},
	props: {
		activeCall: {
			type: Object as PropType<CallWithExtendedData | null>,
			default: null
		},
		searchMode: {
			type: Boolean,
			default: false
		},
		callType: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const expandModel = ref<string[]>([]);
		const { activeCall } = toRefs(props);

		const sections = computed<Section[]>(() => activeCall.value ? activeCall.value.sections || [] : []);
		const totalMatches = computed<number>(() => activeCall.value ? activeCall.value.searchMatches.totalDetails || 0 : 0);

		watch(activeCall, () => {
			if(!activeCall.value) {
				return;
			}
			// expand sections which have matches
			expandModel.value = sections.value.reduce((result: string[], section: Section) => {
				if (section.searchMatches > 0) {
					result.push(section.key);
				}
				return result;
			}, []);
		});

		const expandAll = () => {
			expandModel.value = [...sections.value.map(sec => sec.key), RESPONSE_CONTENT_SECTION_KEY];
		};

		const collapseAll = () => {
			expandModel.value = [];
		};

		const respContentSectionOpened = computed<boolean>(() => expandModel.value.includes(RESPONSE_CONTENT_SECTION_KEY));

		return {
			expandModel,
			sections,
			totalMatches,
			expandAll,
			collapseAll,
			respContentSectionOpened,
			RESPONSE_CONTENT_SECTION_KEY
		};
	}
});
</script>

<template>
	<div class="details">
		<div class="header">
			<div>
				<span>Request Details</span>
				<span
					v-if="searchMode && activeCall"
					class="total-search-info"
				>
					<span class="icon-search"></span>
					<span v-if="totalMatches">{{ totalMatches }} found</span>
					<span v-else>No results found</span>
				</span>
			</div>

			<div>
				<el-button
					size="small"
					:disabled="!activeCall"
					@click="collapseAll"
				>
					<span class="icon el-icon-arrow-up"></span>
					<span>Collapse All</span>
				</el-button>
				<el-button
					size="small"
					:disabled="!activeCall"
					@click="expandAll"
				>
					<span class="icon el-icon-arrow-down"></span>
					<span>Expand All</span>
				</el-button>
			</div>
		</div>

		<div class="content">
			<div
				v-if="!activeCall"
				class="no-selected"
			>
				<img :src="require(`@/assets/images/no-data.svg`)">
				<div class="main">
					No {{ callType }} Request Selected
				</div>
				<div class="sub">
					Select a {{ callType }} Request from the list above to view its details.
				</div>
			</div>

			<el-collapse
				v-else
				v-model="expandModel"
				class="collapse"
			>
				<el-collapse-item
					v-for="section in sections"
					:key="section.key"
					:name="section.key"
					:title="section.title"
					:disabled="section.disabled"
				>
					<div class="section-content">
						<CallDetailsField
							v-for="field in section.content"
							:key="field.key"
							:field="field"
							:active-call="activeCall"
						/>
					</div>
				</el-collapse-item>

				<ResponseContentSection
					v-if="activeCall"
					:key="RESPONSE_CONTENT_SECTION_KEY"
					:collapse-item-key="RESPONSE_CONTENT_SECTION_KEY"
					:call="activeCall"
					:opened="respContentSectionOpened"
				/>
			</el-collapse>
		</div>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.details {
	border-top: $global-base-border;
	height: 100%;
	width: 100%;
	display: flex;
	flex-direction: column;

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: $global-margin $global-margin-medium;
		font-size: $global-font-size;
		font-weight: $global-font-weight-medium;
		border-bottom: $global-base-border;

		::v-deep(.el-button) .icon {
			font-size: $global-small-font-size;
		}

		.total-search-info {
			color: $silver;
			padding: 0 $global-margin-small;
			font-size: $global-small-font-size;
			font-weight: $global-font-weight-normal;
		}

		.icon-search {
			@include mask-icon("~@/assets/images/icon-search.svg", 11px);

			margin-right: $global-margin-xsmall;
			background-color: $silver;
		}
	}

	.content {
		flex: 1;
		overflow: hidden;
		margin-right: 8px;
	}

	.collapse {
		height: 100%;
		overflow-y: overlay;
		border: none;
	}

	::v-deep(.el-collapse-item) {
		border-bottom: $global-base-border;

		.el-collapse-item__header {
			background: none;
			border: none;
			height: 45px;
			font-weight: $global-font-weight-normal;
		}

		.el-collapse-item__arrow {
			transform: rotate(90deg);

			&.is-active {
				transform: rotate(-90deg);
			}
		}
	}

	.section-content:not(:empty) {
		padding: 0 $global-margin $global-margin 40px;
	}

	.no-selected {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin: 60px 0;
		font-weight: $global-font-weight-normal;
		color: $grey;

		img {
			width: 128px;
		}

		.main {
			margin-top: $global-margin-large;
			font-size: $global-xxlarge-font-size;
		}

		.sub {
			margin-top: $global-margin;
			font-size: $global-large-font-size;
		}
	}
}
</style>
