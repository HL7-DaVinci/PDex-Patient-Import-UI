<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from "vue";
import { CallWithExtendedData, SectionField } from "@/types";

export default defineComponent({
	props: {
		field: {
			type: Object as PropType<SectionField>,
			required: true
		},
		activeCall: {
			type: Object as PropType<CallWithExtendedData | null>,
			default: null
		}
	},
	setup(props) {
		const hasMatches = computed<boolean>(() => props.field.searchMatches ? props.field.searchMatches > 0 : false);
		const expanded = ref<boolean>(hasMatches.value);

		// if activeCall changed reset expanded property regarding has this field matches or not
		watch(() => props.activeCall, () => expanded.value = hasMatches.value);

		// if search were changed this value can be also changed; field should be expand if it has matches
		watch(hasMatches, () => expanded.value = hasMatches.value);

		return {
			expanded
		};
	}
});
</script>

<template>
	<div
		:class="{ expanded }"
		class="field"
		@click="expanded = !expanded"
	>
		<div class="key">
			<span v-html="field.key"></span>

			<span
				v-if="field.keyInfo"
				class="icon info"
			>
			</span>
		</div>

		<div class="value">
			<span
				v-if="field.valueIcon"
				:class="field.valueIcon"
				class="icon"
			>
			</span>
			<span v-html="field.value"></span>
		</div>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.field {
	width: 100%;
	display: flex;
	font-size: $global-font-size;
	font-weight: $global-font-weight-light;
	cursor: pointer;

	&:not(:first-child) {
		margin-top: $global-margin-small;
	}

	.icon {
		&.warning {
			@include icon("~@/assets/images/icon-warning.svg", 12px);
		}

		&.success {
			@include icon("~@/assets/images/icon-success.svg", 12px);
		}

		&.info {
			@include icon("~@/assets/images/icon-info.svg", 12px);
		}
	}

	.key {
		width: 160px;
		flex-shrink: 0;
		padding-right: $global-margin-small;

		.icon {
			margin-left: $global-margin-xsmall;
		}
	}

	.value {
		flex: 1;

		.icon {
			margin-right: $global-margin-xsmall;
		}
	}

	.key,
	.value {
		line-break: anywhere;
		@include dont-break-out();
	}

	&:not(.expanded) {
		.key,
		.value {
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
		}
	}
}
</style>
