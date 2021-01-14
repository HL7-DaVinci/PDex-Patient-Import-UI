<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "CustomRadioButton",
	data() {
		return {
			activeIcon: require("../../assets/images/radio-button-checked.svg"),
			inactiveIcon: require("../../assets/images/radio-button.svg")
		};
	}
});
</script>
<template>
	<van-radio
		v-bind="$attrs"
	>
		<template #icon="props">
			<img
				class="img-icon"
				:src="props.checked ? activeIcon : inactiveIcon"
				alt=""
			>
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
	</van-radio>
</template>
