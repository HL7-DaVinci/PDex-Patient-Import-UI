<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "SortMenu",
	props: {
		show: {
			type: Boolean,
			default: false
		},
		value: {
			type: String,
			default: ""
		},
		options: {
			type: Array,
			default: []
		}
	},
	data() {
		return {
			activeIcon: require("../../../assets/images/done.svg"),
			inactiveIcon: ""
		};
	},
});
</script>

<template>
	<div class="sort-menu">
		<van-popup
			:show="show"
			position="bottom"
			@update:show="$emit('update:show', $event)"
		>
			<van-cell>
				<div class="cell-content menu-header">
					<div class="label">SORT BY :</div>
					<span
						class="icon"
						@click="$emit('update:show', false)"
					>
					</span>
				</div>
			</van-cell>

			<div class="options">
				<van-cell
					clickable
					v-for="(option, index) in options"
					:key="index"
				>
					<van-checkbox
						:name="option"
						:modelValue="value === option"
						@update:modelValue="$emit('update:value', $event ? option : '')"
					>
						<template #icon="props">
							<img
								class="img-icon"
								:src="props.checked ? activeIcon : inactiveIcon"
							>
						</template>
						{{ option }}
					</van-checkbox>
				</van-cell>
			</div>
		</van-popup>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts";

.sort-menu {
	::v-deep(.van-popup),
	::v-deep(.van-overlay) {
		position: absolute;
	}

	::v-deep(.van-overlay) {
		backdrop-filter: blur(3px);
	}

	::v-deep(.van-popup) {
		background-color: $mirage;
		border-top: 1px solid $platinum;
	}

	.menu-header {
		height: 60px;
		display: flex;
		align-items: center;
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
		position: relative;

		.label {
			width: 100%;
			text-align: center;
		}

		.icon {
			position: absolute;
			right: 15px;
			cursor: pointer;
			color: $active-color;

			@include icon("~@/assets/images/close-icon.svg", 15px);
		}
	}

	.options {
		max-height: 400px;
		overflow-y: overlay;
		padding-left: 30px;
	}

	::v-deep(.van-cell) {
		padding: 0;
		position: relative;
		border-bottom: 1px solid rgba(231, 231, 231, 0.2);

		.van-checkbox {
			height: 63px;

			&__label {
				font-size: $global-xlarge-font-size;
				font-weight: $global-font-weight-medium;
				margin-left: 0;
				padding-left: 15px;
			}

			&__icon {
				width: 20px;
			}

			&[aria-checked="true"] .van-checkbox__label {
				color: $active-color;
			}
		}
	}
}
</style>
