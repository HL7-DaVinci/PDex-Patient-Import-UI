<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "ResourceHeader",
	props: {
		resourceType: {
			type: String,
			required: true
		},
		payerId: {
			type: [String, Number],
			required: true
		},
		total: {
			type: [String, Number],
			default: ""
		},
		sortEnabled: {
			type: Boolean,
			default: true
		}
	},
	emits: ["open-sort-menu"]
});
</script>

<template>
	<div class="header">
		<van-nav-bar
			:title="`${resourceType} (${total})`"
		>
			<template #left>
				<van-button
					:icon="require('@/assets/images/arrow-left.svg')"
					size="mini"
					@click="$router.push(`/payer/${payerId}`)"
				/>
			</template>

			<template #right>
				<van-button
					:icon="require('@/assets/images/slider.svg')"
					size="mini"
					:disabled="!sortEnabled"
					@click="$emit('open-sort-menu')"
				/>
			</template>
		</van-nav-bar>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/abstracts";

.header {
	height: 80px;
	background-color: $mirage;
	display: flex;
	align-items: flex-end;
	border-bottom: 1px solid $platinum;
}
</style>
