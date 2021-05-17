<script lang="ts">
import { defineComponent } from "vue";
import YourData from "@/components/Phone/YourData.vue";
import router from "@/router";

export default defineComponent({
	components: { YourData },
	setup() {
		const goToImport = () => router.push("/import");

		return {
			goToImport
		};
	}
});
</script>

<template>
	<YourData
		@add-payer-data="goToImport"
	/>
</template>
