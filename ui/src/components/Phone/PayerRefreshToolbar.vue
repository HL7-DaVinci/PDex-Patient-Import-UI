<script lang="ts">
import { defineComponent, PropType } from "vue";
import ProgressBar from "@/components/Phone/ProgressBar.vue";

export default defineComponent({
	components: { ProgressBar },
	emits: ["update:showing-import-overview", "refresh"],
	props: {
		refreshInProgress: {
			type: Boolean,
			default: false
		},
		refreshPercentage: {
			type: Number,
			default: 0
		},
		showingImportOverview: {
			type: Boolean,
			default: false
		},
		importOverview: {
			type: Object as PropType<{ createdCount: number, updatedCount: number }>,
			default: () => ({ createdCount: 0, updatedCount: 0 })
		},
		lastImported: {
			type: String,
			default: ""
		}
	}
});
</script>

<template>
	<div class="refresh-toolbar">
		<div
			v-if="refreshInProgress"
			class="refresh-progress"
		>
			<div class="label">
				Refreshing data...
			</div>
			<ProgressBar
				:percentage="refreshPercentage"
				:thin="true"
			/>
		</div>

		<div
			v-else-if="showingImportOverview"
			class="import-stats"
		>
			<div>
				<div>Showing the refreshed data:</div>
				<div>{{ importOverview.createdCount }} new records, {{ importOverview.updatedCount }} updated records</div>
			</div>
			<span
				class="dismiss"
				@click="$emit('update:showing-import-overview', false)"
			>
				Got it!
			</span>
		</div>

		<div
			v-else
			class="import-info"
		>
			<span>
				Last Import: {{ lastImported ? $filters.formatDate(lastImported) : "" }}
			</span>
			<span class="action-wrap">
				<van-button
					:icon="require('@/assets/images/time-icon.svg')"
					size="mini"
					@click="$emit('update:showing-import-overview', true)"
				/>
				<van-button
					:icon="require('@/assets/images/refresh.svg')"
					size="mini"
					@click="$emit('refresh')"
				/>
			</span>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.refresh-toolbar {
	background-color: $mirage;
	border-top: 1px solid $platinum;
	border-bottom: 1px solid $platinum;
	font-size: 15px;
	font-weight: $global-font-weight-normal;
	padding: 0 $global-margin-large;

	.refresh-progress {
		padding: $global-margin-small 0 $global-margin 0;

		.label {
			margin: 0 0 $global-margin-small 2px;
		}
	}

	.import-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 50px;

		.action-wrap {
			line-height: 0;

			.van-button + .van-button {
				margin-left: $global-margin-large;
			}
		}
	}

	.import-stats {
		padding: 7px 0;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.dismiss {
			font-weight: $global-font-weight-medium;
			font-size: $global-large-font-size;
			color: $active-color;
			cursor: pointer;
		}
	}
}
</style>
