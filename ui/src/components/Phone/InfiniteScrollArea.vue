<script lang="ts">
import { defineComponent, computed, ref } from "vue";

export default defineComponent({
	props: {
		distance: {
			type: Number,
			default: 200
		},
		loading: {
			type: Boolean,
			default: false
		}
	},
	emits: ["more"],
	setup(props, { emit }) {
		const scrollEl = ref<HTMLElement>();
		const isSpinnerShown = computed<boolean>(() => props.loading && (scrollEl.value ? getDistance(scrollEl.value) <= props.distance : false));

		const handleScroll = ({ target }: Event): void => {
			const tillBottom: number = getDistance(target as HTMLElement);

			if (tillBottom <= props.distance) {
				emit("more");
			}
		};
		const getDistance = (el: HTMLElement): number => el.scrollHeight - el.clientHeight - el.scrollTop;

		return {
			handleScroll,
			isSpinnerShown,
			scrollEl
		};
	}
});
</script>

<template>
	<div
		ref="scrollEl"
		class="area"
		@scroll="handleScroll"
	>
		<slot></slot>
		<div
			v-show="isSpinnerShown"
			class="spinner"
			v-loading="true"
		></div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts.scss";

.area {
	overflow-y: overlay;
}

.spinner {
	height: 50px;

	::v-deep(.el-loading-mask) {
		background-color: transparent;
	}
}
</style>
