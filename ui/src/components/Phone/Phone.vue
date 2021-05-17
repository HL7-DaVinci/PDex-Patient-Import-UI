<script lang="ts">
import { defineComponent, ref } from "vue";
import UserTime from "./UserTime.vue";
import { getAllProgress } from "@/api/api";
import { ProgressStatus } from "@/types";
import { PayerModule } from "@/store/modules/payer";
import { PayersModule } from "@/store/modules/payers";
import { CallsModule } from "@/store/modules/calls";
import { useRouter, useRoute, RouteLocation } from "vue-router";

export default defineComponent({
	name: "Phone",
	components: {
		UserTime
	},
	setup() {
		const shouldFade = ref<boolean>(true);
		const router = useRouter();
		const route = useRoute();

		const checkProgress = async () => {
			const progress = await getAllProgress();
			const unfinished = progress.find(item => item.status === "NEW" || item.status === "STARTED");

			const handleUnfinishedProcess = (progress: ProgressStatus) => {
				switch (progress.type) {
					case "IMPORT":
						PayersModule.updateActivePayerId(progress.id);
						CallsModule.setLastImportedPayerId(progress.id);
						PayerModule.setProgress(progress);
						PayerModule.startProgress(progress.id);
						router.push({
							path: "/import",
							query: {
								...route.query
							}
						});
						break;
					case "CLEAR":
						PayerModule.setProgress(progress);
						PayerModule.startProgress(progress.id);
						if (progress.id === -1) {
							router.push({
								path: "/",
								query: {
									...route.query
								}
							});
							return;
						}
						router.push(`/payer/${progress.id}`);
						break;
					case "REFRESH":
						PayersModule.updateActivePayerId(progress.id);
						CallsModule.setLastImportedPayerId(progress.id);
						PayerModule.setProgress(progress);
						PayerModule.startProgress(progress.id);
						router.push({
							path: `/payer/${progress.id}`,
							query: {
								...route.query
							}
						});
						break;
				}
			};

			if (unfinished) {
				handleUnfinishedProcess(unfinished);
			}
		};

		checkProgress();

		let currentRoute: RouteLocation | null = null;
		router.beforeEach((to, from) => {
			currentRoute = from;
		});

		const animate = (newRouteMeta): "fade" | "slide-back" | "slide" => {
			const currentDepth: number = currentRoute?.meta?.depth as number || 0;
			const newDepth: number = newRouteMeta?.meta?.depth || 0;
			if (!currentRoute && shouldFade.value) {
				shouldFade.value = false;
				return "fade";
			}

			return currentDepth > newDepth ? "slide-back" : "slide";
		};

		return {
			animate
		};
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
			<router-view v-slot="{ Component, route }">
				<transition
					:name="animate(route)"
					appear
				>
					<component
						:is="Component"
					/>
				</transition>
			</router-view>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";
$mobile-phone-inner-border-radius: 30px;

.slide-enter-active,
.slide-leave-active {
	@include slide();

	::v-deep(.scroll-area) {
		overflow: hidden;
	}
}

.slide-enter-from {
	transform: translateX(100%);
}

.slide-back-enter-active,
.slide-back-leave-active {
	@include slide();
}

.slide-back-enter-from {
	transform: translate(-100%);
}

.fade-enter-active,
.fade-leave-active {
	transition: 1s;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
}

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
