<script lang="ts">
import { defineComponent } from "vue";
import Servers from "../components/debug/Servers.vue";

export default defineComponent({
	name: "DebugScreen",
	components: { Servers }
});
</script>

<template>
	<div class="debug-section">
		<Servers />
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

.debug-section {
	height: 100vh;
	border-left: $global-base-border;
}
</style>
