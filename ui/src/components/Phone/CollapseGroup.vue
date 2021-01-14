<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		items: {
			type: Array,
			required: true
		}
	},
	data() {
		return {
			collapseModel: []
		};
	}
});
</script>

<template>
	<div class="collapse-group">
		<van-collapse
			v-model="collapseModel"
		>
			<van-collapse-item
				v-for="(item, index) in items"
				:key="index"
			>
				<template #title>
					<slot
						:item="item"
						name="title"
					>
					</slot>
				</template>

				<div class="hidden-content">
					<slot :item="item">
					</slot>
				</div>
			</van-collapse-item>
		</van-collapse>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
$side-gutter: 30px;

.collapse-group {
	::v-deep(.van-collapse) {
		background-color: $mirage;
	}

	::v-deep(.van-collapse-item:not(:last-child)) {
		border-bottom: 1px solid $platinum;
	}

	::v-deep(.van-collapse-item__content) {
		margin: 0;
		padding: 0;
	}

	::v-deep(.van-collapse-item__title) {
		padding: 0 $side-gutter;
	}

	::v-deep(.van-cell__title) {
		margin-right: 10px;
	}

	.hidden-content {
		padding: 0 $side-gutter 20px;
	}
}
</style>
