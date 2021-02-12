<script lang="ts">
import { defineComponent } from "vue";
import UserTime from "./UserTime.vue";

export default defineComponent({
	name: "Phone",
	components: {
		UserTime
	}
});
</script>

<template>
	<div class="phone">
		<div class="status-bar">
			<UserTime />
			<div>
				<img
					src="~@/assets/images/connection-signal.svg"
					alt="batter"
				>
				<img
					src="~@/assets/images/wifi-signal.svg"
					alt="batter"
				>
				<img
					src="~@/assets/images/battery.svg"
					alt="batter"
				>
			</div>
		</div>
		<div class="content">
			<router-view />
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
$mobile-phone-inner-border-radius: 30px;

.phone {
	position: relative;
	flex-basis: 800px;
	min-height: 620px;
	width: 400px;
	border-radius: 50px;
	box-shadow: inset -10px 2px 20px rgba(0, 0, 0, 0.1), inset 10px 4px 20px rgba(0, 0, 0, 0.1);
	padding: 25px 12px;
	margin: auto;
	color: $mobile-default-text-color;

	@media screen and (max-width: $global-breakpoint-medium - 1) {
		width: 360px;
		padding: 20px 18px;
	}
}

.status-bar {
	position: absolute;
	z-index: 1;
	display: flex;
	justify-content: space-between;
	width: 376px; // width of phone container 400 - 12 * 2
	padding: $global-margin $global-margin-large 0;

	img {
		margin-left: 5px;
	}

	@media screen and (max-width: $global-breakpoint-medium - 1) {
		width: 324px; // 360 - 18*2 = 284px
	}
}

.content {
	overflow: hidden;
	position: relative;
	z-index: 0;
	height: 100%;
	background: $mobile-background;
	border-radius: $mobile-phone-inner-border-radius;
}
</style>
