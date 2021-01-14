<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "NoServers",
	emits: ["add-new-server"]
});
</script>

<template>
	<div class="no-servers">
		<img
			class="no-data-image"
			:src="require(`@/assets/images/no-data.svg`)"
			alt="No data"
		>
		<h4 class="title">
			No data to display
		</h4>
		<p class="description">
			Please configure connection to servers to be able to import the data from them.
		</p>
		<el-button
			type="primary"
			class="add-new-server"
			@click="$emit('add-new-server')"
		>
			Configure Servers
		</el-button>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

.no-servers {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	text-align: center;
	height: 100%;
	width: 400px;
	margin: 0 auto;
	color: $global-text-muted-color;

	.no-data-image {
		width: 120px;
	}

	.title {
		font-size: $global-xxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin: $global-margin-large 0 $global-margin;
	}

	.description {
		font-size: $global-medium-font-size;
		line-height: 24px;
		margin: 0 0 $global-margin-xlarge;
	}

	.add-new-server {
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
		width: 320px;
	}
}
</style>
