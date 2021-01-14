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
			type: String,
			required: true
		}
	},
	computed: {
		...mapGetters([
			"resourceOverview"
		]),
		resource() {
			return this.resourceOverview.find(item => item.resourceType === this.resourceType);
		}
	}
});
</script>

<template>
	<div class="header">
		<van-nav-bar
			:title="`${resourceType} (${ resource ? resource.count: '' })`"
		>
			<template #left>
				<img
					src="~@/assets/images/arrow.svg"
					alt="Return to previous screen"
					@click="$router.push(`/payer/${id}`)"
				>
			</template>
		</van-nav-bar>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.header {
	height: 80px;
	background-color: $mirage;
	display: flex;
	align-items: flex-end;
	border-bottom: 1px solid $platinum;
}
</style>
