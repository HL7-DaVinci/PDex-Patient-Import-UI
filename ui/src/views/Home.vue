<script lang="ts">
import { defineComponent } from "vue";
import PayerSelection from "../components/Phone/PayerSelection.vue";
import YourData from "../components/Phone/YourData.vue";

export default defineComponent({
	name: "Home",
	components: { YourData, PayerSelection },
	data() {
		return {
			showSelectPayer: false
		};
	},
	computed: {
		importedPayers(): any {
			return this.$store.getters.servers.filter((payer: any) => payer.lastImported !== null);
		}
	}
});
</script>

<template>
	<PayerSelection
		v-if="showSelectPayer || !importedPayers.length"
		@hide-payer-select="showSelectPayer = false"
	/>
	<YourData
		v-else
		@add-payer-data="showSelectPayer = true"
	/>
</template>
