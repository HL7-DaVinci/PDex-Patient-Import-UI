<script lang="ts">
import { defineComponent, ref, computed, watch, toRefs, PropType } from "vue";
import { CallsModule } from "@/store/modules/calls";
import { Call } from "@/types";
import { getResponseBody } from "@/api/api";
import JsonViewer from "./JsonViewer.vue";
import CopyButton from "@/components/debug/CopyButton.vue";

//
// This component is meant to be placed as one of <el-collapse-items> inside <el-collapse> inside CallDetails.vue.
//
export default defineComponent({
	components: { JsonViewer, CopyButton },
	props: {
		call: {
			type: Object as PropType<Call>,
			required: true
		},
		collapseItemKey: {
			type: String,
			required: true
		},
		opened: {
			type: Boolean,
			required: true
		}
	},
	setup(props) {
		const { call, opened } = toRefs(props);

		const callFinished = computed<boolean>(() => call.value.requestStatus === "COMPLETED");

		const contentState = ref<"not-loading" | { loading: true, id: string } | { loaded: true, id: string, data: string }>("not-loading");

		const idToLoad = computed<string | null>(() => (callFinished.value && opened.value) ? call.value.requestId : null);

		watch(idToLoad, id => {
			if (id && (contentState.value === "not-loading" || contentState.value.id !== id)) {
				loadContent(id);
			}
		});

		const loadContent = async (id: string) => {
			if (CallsModule.lastImportedPayerId) {
				contentState.value = { loading: true, id };
				const body = await getResponseBody(CallsModule.lastImportedPayerId, id);
				contentState.value = { loaded: true, id, data: body };
			}
		};

		const prettyRawModel = ref<"Pretty" | "Raw">("Pretty");
		const showPretty = computed<boolean>(() => prettyRawModel.value === "Pretty");

		const searchActive = ref<boolean>(false);
		const maximized = ref<boolean>(false);

		watch(opened, newVal => {
			if (!newVal) {
				maximized.value = false;
			}
		});

		return {
			prettyRawModel,
			showPretty,
			callFinished,
			contentState,
			searchActive,
			maximized
		};
	}
});
</script>

<template>
	<el-collapse-item
		:name="collapseItemKey"
		:disabled="false"
		class="resp-content-section"
		:class="{ maximized }"
	>
		<template #title>
			<div class="section-header">
				<span class="left-group">
					<span class="title">
						Response Content
					</span>
					<button
						v-if="opened"
						class="icon-button"
						@click.stop="maximized = !maximized"
					>
						<span class="icon-maximize"></span>
					</button>
				</span>

				<span
					v-if="opened"
					@click.stop
				>
					<el-radio-group
						v-model="prettyRawModel"
					>
						<el-radio-button label="Pretty" />
						<el-radio-button label="Raw" />
					</el-radio-group>
				</span>

				<span
					v-if="opened"
					@click.stop
				>
					<CopyButton
						v-if="contentState.loaded"
						:text="contentState.data"
					/>
					<button
						class="icon-button"
						@click="searchActive = !searchActive"
					>
						<span class="icon-search"></span>
					</button>
				</span>
			</div>
		</template>

		<div class="section-content">
			<div
				v-if="contentState.loading || !callFinished"
				v-loading="true"
				class="loader"
			>
			</div>
			<JsonViewer
				v-else-if="contentState.loaded"
				v-model:search-active="searchActive"
				:data="contentState.data"
				:pretty="showPretty"
			/>
		</div>
	</el-collapse-item>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.resp-content-section {
	overflow: hidden;
	display: flex;
	flex-direction: column;
	background-color: $white;

	&.maximized {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 1;
	}

	&.is-active {
		height: 100%;
	}

	::v-deep(.el-collapse-item__wrap) {
		overflow: hidden;
		flex: 1;
	}

	::v-deep(.el-collapse-item__content) {
		overflow: hidden;
		height: 100%;
	}

	.section-content {
		padding-right: 10px;
		overflow: hidden;
		height: 100%;

		.loader {
			height: 100%;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}

	&:not(.is-active) {
		.section-content {
			height: 0;
		}
	}

	.section-header {
		width: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.left-group {
			display: flex;
			align-items: center;
		}

		.title {
			margin-right: 10px;
		}

		::v-deep(.el-radio-button) {
			.el-radio-button__inner {
				width: 67px;
				height: 25px;
				line-height: 25px;
				padding: 0;
			}

			&:not(.is-active) .el-radio-button__inner {
				color: $grey;
			}
		}
	}

	.icon-maximize {
		@include mask-icon("~@/assets/images/icon-maximize.svg", 15px);
	}

	.icon-search {
		@include mask-icon("~@/assets/images/icon-search.svg", 15px);
	}

	.icon-button {
		background: none;
		border: none;
		cursor: pointer;
		line-height: 0;
		padding: 5px;
		color: $grey;

		&:hover {
			color: $night-rider;
		}
	}
}
</style>
