<script lang="ts">
import { defineComponent, PropType, computed } from "vue";
import { ImportOverview } from "@/types";

export default defineComponent({
	props: {
		resources: {
			type: Array,
			required: true
		},
		importOverview: {
			type: Array as PropType<ImportOverview[] | null>
		}
	},
	emits: ["click-resource"],
	setup(props) {
		const importOverviewHash = computed(() => Object.fromEntries(props.importOverview?.map(item => [item.resourceType, item]) || []));

		const secondaryText = (resourceType: string) => {
			const item = importOverviewHash.value[resourceType];
			if (!item) {
				return "";
			}
			return [
				item.createdCount ? [`${item.createdCount} new records`] : [],
				item.updatedCount ? [`${item.updatedCount} updated records`] : []
			].flat().join(",");
		};

		return {
			secondaryText
		};
	}
});
</script>

<template>
	<van-cell-group>
		<van-cell
			v-for="(item, index) in resources"
			:key="index"
			class="resource"
			is-link
			@click="$emit('click-resource', item.resourceType)"
		>
			<template #title>
				<div>
					{{ item.resourceType }} ({{ item.count }})
				</div>
				<div class="secondary-text">
					{{ secondaryText(item.resourceType) }}
				</div>
			</template>
		</van-cell>
	</van-cell-group>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.resource {
	padding: $global-margin-medium $global-margin-large;
	border-bottom: 1px solid $platinum;

	.secondary-text:not(:empty) {
		font-size: $global-font-size;
		font-weight: $global-font-weight-light;
		margin-top: 8px;
	}
}
</style>
