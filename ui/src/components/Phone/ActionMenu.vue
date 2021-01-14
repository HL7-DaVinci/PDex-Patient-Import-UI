<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "ActionMenu",
	props: {
		show: {
			type: Boolean,
			default: false
		}
	},
	emits: ["clear-all", "update:show"],
	methods: {
		logout() {
			this.$store.dispatch("authLogout").then(() => this.$router.push("/login"));
		},
		handleClearAll() {
			this.$emit("update:show", false);
			this.$emit("clear-all", false);
		}
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
				@click="logout"
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
		padding: 10px 30px;
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
			@include icon("~@/assets/images/icon-trash.svg", 40px);
		}
	}

	.logout .icon {
		color: $active-color;

		@include icon("~@/assets/images/icon-logout.svg", 40px);
	}

	.close .icon {
		color: $active-color;

		@include icon("~@/assets/images/icon-x.svg", 40px);
	}
}
</style>
