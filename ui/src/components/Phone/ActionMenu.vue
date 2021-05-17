<script lang="ts">
import { defineComponent } from "vue";
import AppConfirmDialog from "@/components/AppConfirmDialog.vue";
import { handleMobileLogout } from "@/utils/handleMobileLogout";

export default defineComponent({
	name: "ActionMenu",
	components: { AppConfirmDialog },
	props: {
		show: {
			type: Boolean,
			default: false
		}
	},
	emits: ["clear-all", "update:show"],
	setup(props, { emit }) {
		const { confirmOptions, showDialog, handleLogout, clearAllRequests } = handleMobileLogout();

		const handleClearAll = (): void => {
			emit("update:show", false);
			emit("clear-all", false);
		};

		return {
			confirmOptions,
			showDialog,
			handleLogout,
			handleClearAll,
			clearAllRequests
		};
	}
});
</script>

<template>
	<div class="action-menu">
		<van-popup
			:show="show"
			position="bottom"
			@update:show="$emit('update:show', $event)"
		>
			<van-cell
				clickable
				@click="handleClearAll"
			>
				<div class="cell-content clear">
					<span class="icon"></span>
					<span>Clear All Data</span>
				</div>
			</van-cell>

			<van-cell
				clickable
				@click="handleLogout"
			>
				<div class="cell-content logout">
					<span class="icon"></span>
					<span>Log Out</span>
				</div>
			</van-cell>

			<van-cell
				clickable
				@click="$emit('update:show', false)"
			>
				<div class="cell-content close">
					<span class="icon"></span>
					<span>Close</span>
				</div>
			</van-cell>
		</van-popup>
	</div>
	<AppConfirmDialog
		:show-dialog="showDialog"
		:options="confirmOptions"
		@hide-dialog="showDialog = false"
		@confirm="clearAllRequests"
	/>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.action-menu {
	::v-deep(.van-popup),
	::v-deep(.van-overlay) {
		position: absolute;
	}

	::v-deep(.van-overlay) {
		backdrop-filter: blur(3px);
	}

	::v-deep(.van-cell) {
		padding: 20px 40px;
		border-bottom: 1px solid $platinum;
	}

	.cell-content {
		display: flex;
		align-items: center;

		.icon {
			margin-right: 15px;
		}
	}

	.clear {
		color: $torch-red;

		.icon {
			@include mask-icon("~@/assets/images/icon-trash.svg", 14px, 15px);
		}
	}

	.logout .icon {
		color: $active-color;

		@include mask-icon("~@/assets/images/icon-logout.svg", 15px);
	}

	.close .icon {
		color: $active-color;

		@include mask-icon("~@/assets/images/close-icon.svg", 15px);
	}
}
</style>
