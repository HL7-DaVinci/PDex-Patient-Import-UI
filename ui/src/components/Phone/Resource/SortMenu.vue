<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "SortMenu",
	props: {
		show: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			checked: [],
			activeIcon: require("../../../assets/images/done.svg"),
			inactiveIcon: ""
		};
	},
	computed: {
		sortArray() {
			return ["Observation Code Display", "Effective Date", "Value", "Category Code Display", "Interpretation Code Display"];
		}
	},
	methods: {
	}
});
</script>

<template>
	<div class="sort-menu">
		<van-popup
			:show="show"
			position="bottom"
		>
			<van-cell>
				<div class="cell-content sort-by">
					<span class="label">SORT BY :</span>
					<span
						class="icon"
						@click="$emit('update:show', false)"
					>
					</span>
				</div>
			</van-cell>
			<van-checkbox-group
				v-model="checked"
				:max="1"
			>
				<van-checkbox
					v-for="(sort, index) in sortArray"
					:key="index"
					:name="sort"
				>
					<template #icon="props">
						<img
							class="img-icon"
							:src="props.checked ? activeIcon : inactiveIcon"
						>
					</template>
					{{ sort }}
				</van-checkbox>
			</van-checkbox-group>
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

	::v-deep(.van-cell) {
		padding: 0 15px 0 30px;
		position: relative;
		border: 1px solid rgba(231, 231, 231, 0.2);
	}

	.sort-by {
		height: 60px;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-normal;
		line-height: 19px;

		.label {
			margin-right: 85px;
		}

		.icon {
			cursor: pointer;
			color: $active-color;
			@include icon("~@/assets/images/icon-x.svg", 40px);
		}
	}
}

::v-deep(.van-checkbox-group) {
	background-color: $mirage;
	padding: 0 0 0 30px;

	.van-checkbox {
		height: 63px;
		position: relative;

		&__label {
			font-size: $global-xlarge-font-size;
			font-weight: $global-font-weight-medium;
			line-height: 23px;
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

	.van-checkbox::after {
		content: "";
		position: absolute;
		bottom: 0;
		right: 0;
		width: 100%;
		border-bottom: 1px solid rgba(231, 231, 231, 0.2);
	}
}
</style>
