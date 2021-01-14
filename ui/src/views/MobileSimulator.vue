<script lang="ts">
import { defineComponent } from "vue";
import Phone from "@/components/Phone/Phone.vue";

export default defineComponent({
	name: "MobileSimulator",
	components: {
		Phone
	}
});
</script>

<template>
	<div class="mobile-simulator">
		<Phone />
		<div class="footer">
			<div class="item">
				<img
					src="~@/assets/images/hlx-logo.svg"
					alt="HLX"
				>
				<span class="powered-by">
					Powered by
					<a
						href="https://www.healthlx.com/"
						target="_blank"
					>
						healthlx.com
					</a>
				</span>
			</div>
			<div class="item">
				<img
					src="~@/assets/images/github-logo.svg"
					alt="HLX"
				>
				<span class="powered-by">
					View on
					<a
						href="https://github.com/HL7-DaVinci/PDex-Patient-Import-UI"
						target="_blank"
					>
						GitHub Project
					</a>
				</span>
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.mobile-simulator {
	height: 100vh;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: space-between;
	overflow-y: overlay;
}

.footer {
	display: flex;
	justify-content: center;
	background-color: rgba($pinkish-grey, 0.15);
	width: 100%;
	padding: 15px;

	.item {
		font-size: $global-font-size;
		display: flex;
		align-items: center;

		&:first-child {
			margin-right: 60px;
		}
	}

	.powered-by {
		display: inline-block;
		width: 95px;
		margin-left: 10px;

		a {
			text-decoration: underline;
			color: $picton-blue;
		}
	}
}
</style>
