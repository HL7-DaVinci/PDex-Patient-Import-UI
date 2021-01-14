<script lang="ts">
import { defineComponent } from "vue";
import ActionMenu from "./ActionMenu.vue";
import ProgressScreen from "./ProgressScreen.vue";

export default defineComponent({
	components: { ProgressScreen, ActionMenu },
	emits: ["add-payer-data"],
	data() {
		return {
			menuOpened: false,
			showConfirm: false,
			isRemoving: false
		};
	},
	computed: {
		payerList(): any {
			return this.$store.getters.servers.filter((payer: any) => payer.lastImported !== null);
		}
	},
	methods: {
		removeAllData() {
			this.showConfirm = false;
			this.isRemoving = true;
			this.$store.dispatch("removeAllData").finally(() => {
				this.$store.dispatch("loadServers").finally(() => this.isRemoving = false);
			});
		}
	}
});
</script>

<template>
	<div class="payers-list">
		<div class="header">
			<div class="header-text">
				Your Data
			</div>
			<a
				class="menu-icon"
				@click="menuOpened = !menuOpened"
			></a>
		</div>

		<div
			v-if="!isRemoving"
			class="scroll-area"
		>
			<van-cell-group v-if="payerList.length > 1">
				<van-cell
					is-link
					to="/all-data"
				>
					<div class="title">
						<div class="main">
							All Data
						</div>
						<div class="sub">
							Combined data imported from all payer
						</div>
					</div>
				</van-cell>
			</van-cell-group>

			<h2
				v-if="payerList.length > 1"
				class="section-header"
			>
				By Payer
			</h2>
			<van-cell-group>
				<van-cell
					v-for="(payer, index) in payerList"
					:key="index"
					is-link
					:to="`/payer/${payer.id}`"
				>
					<div class="title">
						<div class="main">
							{{ payer.name }}
						</div>
						<div class="sub">
							Imported at {{ $filters.formatDate(payer.lastImported) }}
						</div>
					</div>
				</van-cell>
			</van-cell-group>

			<div class="actions">
				<van-button
					type="primary"
					class="add-payer-data"
					@click="$emit('add-payer-data')"
				>
					Add Another Payer
				</van-button>
			</div>
		</div>
		<van-dialog
			:show="showConfirm"
			title="Are you sure?"
			show-cancel-button
			confirm-button-text="Delete"
			cancel-button-text="Cancel"
			@cancel="showConfirm = false"
			@confirm="removeAllData"
		>
			Once you confirm, all imported payer data will be permanently deleted.
		</van-dialog>
		<ProgressScreen
			v-if="isRemoving"
			title="Clear All Data in progress"
			description="Please wait, it may take a few seconds to delete all your information."
		/>
	</div>

	<ActionMenu
		v-model:show="menuOpened"
		@clear-all="showConfirm = true"
	/>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts";

.payers-list {
	background-color: $black-russian;
	height: 100%;
	display: flex;
	flex-direction: column;

	.scroll-area {
		flex: 1;
		overflow: overlay;
		padding-top: $global-margin-medium;
	}

	.header {
		height: 80px;
		display: flex;
		justify-content: space-between;
		align-items: flex-end;
		background-color: $mirage;
		border-bottom: 1px solid $platinum;
		padding: 0 30px 10px 30px;
		position: relative;

		&-text {
			font-size: $global-xxlarge-font-size;
			font-weight: $global-font-weight-medium;
		}

		.menu-icon {
			cursor: pointer;
			position: absolute;
			right: 15px;
			bottom: 0;
			color: $active-color;

			@include icon("~@/assets/images/icon-three-dots.svg", 40px);
		}
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		font-size: 13px;
		font-weight: $global-font-weight-light;
		padding: 25px 0 0 $global-margin-large;
		text-transform: uppercase;
		margin: 0;
	}

	.actions {
		margin: $global-margin-large $global-margin-large;
	}

	::v-deep(.van-cell) {
		padding: 20px 30px;
		border-bottom: 1px solid $platinum;

		&:first-child {
			border-top: 1px solid $platinum;
		}
	}

	.title {
		.main {
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-normal;
		}

		.sub {
			font-size: $global-font-size;
			font-weight: $global-font-weight-light;
			margin-top: 8px;
		}
	}
}
</style>
