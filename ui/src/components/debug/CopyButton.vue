<script lang="ts">
import { defineComponent, ref } from "vue";
import useClipboard from "vue-clipboard3";

export default defineComponent({
	props: {
		text: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const { toClipboard } = useClipboard();
		const tooltipText = ref<string>("copy");

		const copy = async () => {
			try {
				await toClipboard(props.text);
				tooltipText.value = "copied";
				setTimeout(() => tooltipText.value = "copy", 3000);
			} catch (e) {
				console.error(e);
			}
		};

		return {
			copy,
			tooltipText
		};
	}
});

</script>

<template>
	<span class="copy-button">
		<el-tooltip
			popper-class="copy-button-tooltip"
			:content="tooltipText"
			placement="top"
		>
			<button
				class="button"
				@click.stop="copy"
			>
				<span class="icon-copy"></span>
			</button>
		</el-tooltip>
	</span>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins.scss";

.copy-button {
	.button {
		background: none;
		border: none;
		cursor: pointer;
		line-height: 0;
		padding: 5px;
		color: $grey;

		&:hover {
			color: $night-rider;
		}
	}

	.icon-copy {
		@include mask-icon("~@/assets/images/copy.svg", 16px);
	}
}
</style>

<style lang="scss">
@import "~@/assets/scss/abstracts/variables";

.el-popper.copy-button-tooltip {
	background: $global-primary-color;
	width: 45px;
	height: 20px;
	color: $global-background;
	border-radius: 4px;
	font-size: $global-small-font-size;
	font-weight: $global-font-weight-normal;
	text-align: center;
	line-height: 20px;
	padding: 0;

	.el-popper__arrow {
		display: none;
	}
}
</style>
