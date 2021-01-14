<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		resources: {
			type: Array,
			required: true
		}
	}
});
</script>

<template>
	<van-cell-group>
		<van-cell
			v-for="(item, index) in resources"
			:key="index"
			:title="`${ item.resourceType } (${ item.count })`"
			class="resource"
			is-link
			@click="$emit('click-resource', item.resourceType)"
		/>
	</van-cell-group>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.resource {
	height: 60px;
	padding: 0 30px;
	border-bottom: 1px solid $platinum;
}
</style>
