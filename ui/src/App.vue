<script lang="ts">
import "./assets/scss/styles.scss";
import { defineComponent } from "vue";
import MobileSimulator from "./views/MobileSimulator.vue";
import Debug from "@/components/debug/Debug.vue";
import { AuthModule } from "@/store/modules/auth";
import { PayersModule } from "@/store/modules/payers";
import { setRequestAuthHeader, goToImportOrHome } from "@/utils/utils";
import { PayerModule } from "@/store/modules/payer";

export default defineComponent({
	name: "App",
	components: {
		MobileSimulator,
		Debug
	},
	data() {
		return {
			ready: false
		};
	},
	computed: {
		token() {
			return AuthModule.token;
		},
		isPayerDeleting() {
			return PayersModule.isPayerDeleting;
		},
		activePayer() {
			return PayersModule.activePayer;
		},
		showOverlay(): boolean {
			return PayerModule.payerAuthInProgress;
		}
	},
	async created() {
		try {
			await PayersModule.loadPayers();
		} finally {
			if (this.token) {
				setRequestAuthHeader(`Bearer ${this.token}`);
				try {
					await AuthModule.authVerify({ token: this.token });
				} catch (err) {
					await this.$router.push("/login");
				}
			}
			if (this.$router.name === "/" || this.$router.name === "/import") {
				await goToImportOrHome();
			}
			this.ready = true;

			// url contain payer id
			// payers were loaded
			// check if payer with such id exist and it`s data were imported
			// if not redirect on mobile home page
			if (this.$route.params.id && !this.activePayer) {
				await this.$router.push("/");
			}
		}
	}
});

</script>

<template>
	<div
		v-if="ready"
		v-loading="isPayerDeleting"
		class="app"
	>
		<splitpanes>
			<pane>
				<MobileSimulator />
			</pane>
			<pane min-size="50">
				<Debug />
			</pane>
		</splitpanes>
		<div
			v-if="showOverlay"
			class="overlay"
		>
		</div>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";

.app {
	width: 100vw;
	height: 100vh;

	.overlay {
		position: absolute;
		background: $whisper;
		width: 100%;
		height: 100%;
		top: 0;
		left: 0;
		opacity: .8;
		z-index: 2;
	}

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

	::v-deep(.splitpanes.splitpanes--dragging) {
		> .splitpanes__splitter {
			background-color: $global-primary-color;
		}
	}

	.splitpanes--vertical > ::v-deep(.splitpanes__splitter::before) {
		left: -10px;
		right: -10px;
	}

	.splitpanes--horizontal > ::v-deep(.splitpanes__splitter::before) {
		top: -10px;
		bottom: -10px;
	}
}
</style>
