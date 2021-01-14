<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "PayerHeader",
	props: {
		title: {
			type: String,
			required: true
		}
	}
});
</script>

<template>
	<div class="header">
		<van-nav-bar
			:title="title"
		>
			<template #left>
				<img
					src="~@/assets/images/arrow.svg"
					alt="Return to previous screen"
					@click="$router.push('/')"
				>
			</template>
			<template #right>
				<img
					src="~@/assets/images/trash.svg"
					alt="Delete payer"
					@click="$emit('showRemovePayerDialog')"
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
