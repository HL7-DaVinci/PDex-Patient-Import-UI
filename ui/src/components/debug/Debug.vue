<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import DebugPanel from "./DebugPanel.vue";
import Servers from "./Servers.vue";
import { useFhirCallStream } from "@/utils/useFhirCallStream";
import { Call } from "@/types";
import { CallsModule } from "@/store/modules/calls";
import { AuthModule } from "@/store/modules/auth";
import { PayersModule } from "@/store/modules/payers";

export default defineComponent({
	components: {
		DebugPanel,
		Servers
	},
	setup() {
		const screenToShow = ref<"servers" | "calls">("servers");
		const authToken = computed<string | null>(() => AuthModule.token);
		const payerId = computed<number | null>(() => PayersModule.activePayerId);

		useFhirCallStream(authToken, payerId, (call: Call) => {
			CallsModule.addCall(call);
		});

		return {
			screenToShow
		};
	}
});
</script>

<template>
	<div class="debug">
		<transition
			name="fade"
			mode="out-in"
		>
			<Servers
				v-if="screenToShow === 'servers'"
				key="servers"
				@back-to-calls="screenToShow = 'calls'"
			/>

			<DebugPanel
				v-else
				key="debug"
				@back-to-servers="screenToShow = 'servers'"
			/>
		</transition>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

.debug {
	height: 100%;
	width: 100%;
	border-left: $global-base-border;
}

.fade-enter-active {
	transition: opacity 0.15s ease-in;
}

.fade-leave-active {
	transition: opacity 0.15s ease-out;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
}
</style>
