<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		distance: {
			type: Number,
			default: 400
		}
	},
	methods: {
		handleScroll(event) {
			const tillBottom = event.target.scrollHeight - event.target.clientHeight - event.target.scrollTop;
			if (tillBottom <= this.distance) {
				this.$emit("more");
			}
		}
	}
});
</script>

<template>
	<div
		class="area"
		@scroll="handleScroll"
	>
		<slot></slot>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts.scss";

.area {
	overflow-y: overlay;
}
</style>
