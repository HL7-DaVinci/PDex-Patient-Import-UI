<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		options: {
			type: Object,
			required: true
		},
		showDialog: {
			type: Boolean,
			default: false
		}
	},
	emits: ["hide-dialog", "confirm"]
});
</script>

<template>
	<el-dialog
		:model-value="showDialog"
		:title="options.title"
		width="550px"
		:destroy-on-close="true"
		:append-to-body="true"
		:before-close="() => $emit('hide-dialog')"
	>
		<div>{{ options.primaryText }}</div>
		<div class="text-muted">
			{{ options.secondaryText }}
		</div>

		<template #footer>
			<div class="dialog-footer">
				<el-button
					type="primary"
					@click="$emit('confirm')"
				>
					{{ options.primaryButton }}
				</el-button>
				<el-button @click="$emit('hide-dialog')">
					{{ options.secondaryButton }}
				</el-button>
			</div>
		</template>
	</el-dialog>
</template>
