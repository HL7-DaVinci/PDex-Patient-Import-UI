<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "CustomDialogWithClose",
	setup(_, { emit }) {
		// component emits will remove all event handlers from $attrs that are listed inside
		// we don't want that, cause we are firing cancel from this component and want to listen cancel from original dialog
		// eslint-disable-next-line
		const handleClose = (): void => emit("cancel");

		return {
			handleClose
		};
	}
});
</script>

<template>
	<van-dialog
		v-bind="$attrs"
	>
		<template #title>
			<button
				class="header-button"
				type="button"
				@click="handleClose"
			>
				<span class="close-icon"></span>
			</button>
			{{ $attrs.title }}
		</template>
		<template
			v-for="(_, slot) of $slots"
			#[slot]="scope"
		>
			<slot
				:name="slot"
				v-bind="scope"
			>
			</slot>
		</template>
	</van-dialog>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.header-button {
	position: absolute;
	top: 20px;
	right: 20px;
	padding: 0;
	cursor: pointer;
	background: transparent;
	border: none;
}

.close-icon {
	color: $active-color;

	@include mask-icon("~@/assets/images/close-icon.svg", 15px);
}
</style>
