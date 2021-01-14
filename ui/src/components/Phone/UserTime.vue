<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "UserTime",
	data() {
		return {
			time: ""
		};
	},
	created() {
		this.updateTime();
		setInterval(this.updateTime, 1000);
	},
	methods: {
		//
		// Update time, correctly handles leading zero.
		//
		updateTime(): void {
			const date = new Date();

			this.time = [date.getHours(), date.getMinutes()].map(num => `0${num}`.slice(-2)).join(":");
		}
	}
});
</script>

<template>
	<div class="user-time">
		{{ time }}
	</div>
</template>

<style lang="scss" scoped>
.user-time {
	text-align: center;
}
</style>
