<script lang="ts">
import { defineComponent } from "vue";
import { mapGetters } from "vuex";

export default defineComponent({
	name: "ResourceHeader",
	props: {
		resourceType: {
			type: String,
			required: true
		},
		id: {
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
	}
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
					@click="$router.push(`/payer/${id}`)"
				/>
			</template>

			<!-- <template #right>
				<van-button
					:icon="require('@/assets/images/slider.svg')"
					size="mini"
					:disabled="!sortEnabled"
					@click="$emit('open-sort-menu')"
				/>
			</template> -->
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
