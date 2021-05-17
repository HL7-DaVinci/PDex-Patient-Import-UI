<script lang="ts">
import { defineComponent } from "vue";
import ProgressBar from "./ProgressBar.vue";

export default defineComponent({
	components: { ProgressBar },
	props: {
		title: {
			type: String,
			default: "In Progress"
		},
		description: {
			type: String,
			default: "Please wait, it may take a few minutes."
		},
		percentage: {
			type: Number,
			required: true
		}
	}
});
</script>

<template>
	<div class="progress-screen">
		<h2 class="title">
			{{ title }}
		</h2>
		<p class="description">
			{{ description }}
		</p>
		<ProgressBar :percentage="percentage" />
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts";

.progress-screen {
	background-color: $mobile-background;
	height: 100%;
	margin-top: $global-margin-large;
	padding: 0 $global-margin-large;

	.title {
		font-size: $global-xxlarge-font-size;
		font-weight: $global-font-weight-medium;
	}

	.description {
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: $global-margin-large;
	}
}
</style>
