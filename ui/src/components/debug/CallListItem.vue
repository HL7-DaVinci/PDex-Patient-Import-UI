<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { CallWithExtendedData, RequestStatus } from "@/types";
import CopyButton from "@/components/debug/CopyButton.vue";

export default defineComponent({
	components: { CopyButton },
	props: {
		call: {
			type: Object as PropType<CallWithExtendedData>,
			required: true
		},
		selected: {
			type: Boolean,
			required: true
		}
	},
	setup() {
		const hasError = (responseStatus: number): boolean => responseStatus >= 400;

		return {
			hasError,
			RequestStatus
		};
	}
});

</script>

<template>
	<li
		:class="{ selected }"
		class="calls-list-item"
	>
		<i
			v-if="hasError(call.responseStatus)"
			class="icon-warning icon"
		></i>
		<i
			v-else-if="call.requestStatus === RequestStatus.Pending || call.requestStatus === RequestStatus.Completed || call.requestStatus === 'UNDEFINED'"
			class="icon"
			:class="{
				'el-icon-loading' : call.requestStatus === RequestStatus.Pending,
				'icon-success': call.requestStatus === RequestStatus.Completed || call.requestStatus === 'UNDEFINED'
			}"
		></i>
		<div class="content">
			<span
				class="call-uri"
				v-html="call.uriWithHighlights"
			></span>
			<span
				v-if="call.searchMatches.totalDetails"
				class="matches-mark"
			>...</span>
		</div>

		<CopyButton
			:text="call.requestUri"
			class="copy-button"
		/>
	</li>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins.scss";

.calls-list-item {
	font-size: $global-font-size;
	font-weight: $global-font-weight-light;
	color: $global-text-color;
	line-height: 18px;
	display: flex;
	align-items: center;
	cursor: pointer;
	margin: 0 $global-margin-small;
	border: 1px solid transparent;

	.copy-button {
		margin-left: auto;
		visibility: hidden;
	}

	&.selected,
	&:hover {
		.copy-button {
			visibility: visible;
		}
	}

	&:hover:not(.selected) {
		box-shadow: $global-box-shadow;
		border: 1px solid $global-base-border-color;
		border-radius: 5px;
	}
}

.content {
	display: contents;

	.call-uri {
		text-overflow: ellipsis;
		overflow: hidden;
		overflow-wrap: anywhere;
		white-space: nowrap;
		margin-left: $global-margin-small;
		width: auto;
	}

	.matches-mark {
		padding: 0 $global-margin-xsmall;
		background-color: $global-search-highlight-color;
	}
}

.selected {
	border: 1px solid $dodger-blue;
	box-shadow: $global-box-shadow;
	border-radius: 5px;
	height: 100%;

	.content {
		display: block;
		margin-left: $global-margin-small;

		.call-uri {
			white-space: normal;
			margin-left: 0;
		}
	}
}

.icon {
	height: 12px;
	display: inline-block;
	margin-left: $global-margin-small;
	min-width: 12px;
}

.icon-warning {
	background: url("~@/assets/images/icon-warning.svg");
}

.icon-success {
	background: url("~@/assets/images/icon-success.svg");
}
</style>
