<script lang="ts">
import "./assets/scss/styles.scss";
import { defineComponent } from "vue";
import MobileSimulator from "./views/MobileSimulator.vue";
import DebugScreen from "./views/DebugScreen.vue";
import axios from "axios";

export default defineComponent({
	name: "App",
	components: {
		MobileSimulator,
		DebugScreen
	},
	data() {
		return {
			ready: false
		};
	},
	computed: {
		token() {
			return this.$store.getters.token;
		},
		isServerDeleting() {
			return this.$store.getters.isServerDeleting;
		},
		activePayer() {
			return this.$store.getters.activePayer;
		}
	},
	created() {
		this.$store.dispatch("loadServers").finally(() => {
			this.ready = true;

			// url contain payer id
			// payers were loaded
			// check if payer with such id exist and it`s data were imported
			// if not redirect on mobile home page
			if(this.$route.params.id && !this.activePayer) {
				this.$router.push("/");
			}
		});
		if (this.token) {
			this.$store.dispatch("authVerify", { token: this.token }).then(null, () => this.$router.push("/login"));
			axios.defaults.headers.common.Authorization = `Bearer ${this.token}`;
		}
	}
});

</script>

<template>
	<div
		v-if="ready"
		v-loading="isServerDeleting"
	>
		<splitpanes>
			<pane>
				<MobileSimulator />
			</pane>
			<pane min-size="50">
				<DebugScreen />
			</pane>
		</splitpanes>
	</div>
</template>

<style scoped lang="scss">

.splitpanes__pane {
	min-width: 460px;
}

::v-deep(.splitpanes__splitter) {
	position: relative;
}

::v-deep(.splitpanes__splitter::before) {
	content: '';
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	transition: 0.4s;
}

.splitpanes--vertical > ::v-deep(.splitpanes__splitter::before) {
	left: -10px;
	right: -10px;
}

.splitpanes--horizontal > ::v-deep(.splitpanes__splitter::before) {
	top: -10px;
	bottom: -10px;
}
</style>
