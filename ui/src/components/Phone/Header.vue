<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		title: {
			type: String,
			required: true
		}
	},
	emits: ["back"]
});
</script>

<template>
	<div class="header">
		<van-nav-bar
			:title="title"
		>
			<template #left>
				<van-button
					:icon="require('@/assets/images/arrow-left.svg')"
					size="mini"
					@click="$emit('back')"
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
