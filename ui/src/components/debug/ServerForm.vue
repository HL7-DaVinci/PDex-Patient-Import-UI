<script lang="ts">
import { defineComponent } from "vue";
import _ from "../../vendors/lodash";
import ServerConfirmDialog from "./ServerConfirmDialog.vue";

const DEFAULT_SERVER = { name: "", fhirServerUri: "", clientId: "", scope: "openid fhirUser offline_access user/*.read" };

export default defineComponent({
	name: "ServerForm",
	components: {ServerConfirmDialog},
	props: {
		data: {
			type: Object,
			default: () => ({ ...DEFAULT_SERVER })
		},
		editMode: {
			type: Boolean,
			default: false
		}
	},
	emits: ["hide-form"],
	data() {
		return {
			form: { ...DEFAULT_SERVER } as any,
			rules: {
				name: [{ required: true, message: "This field is required", trigger: "change" }],
				fhirServerUri: [{ required: true, message: "This field is required", trigger: "change" }],
				clientId: [{ required: true, message: "This field is required", trigger: "change" }],
				scope: [{ required: true, message: "This field is required", trigger: "change" }]
			},
			isSaving: false,
			showCancelDialog: false,
			showDeleteDialog: false
		};
	},
	computed: {
		isFormInvalid(): boolean {
			return !this.form ||
				!this.form.name.length ||
				!this.form.fhirServerUri.length ||
				!this.form.clientId.length ||
				!this.form.scope.length;
		},
		hasChanges(): boolean {
			return !_.isEqual(this.form, this.data);
		},
		disableField(): boolean {
			return this.isSaving || (this.editMode && this.data.lastImported !== null);
		},
		deleteDialogOptions(): any {
			return {
				title: "Delete Server",
				primaryText: `Are you sure you want to delete "${this.data.name}" server?`,
				secondaryText: "Deleting this server will erase all data that has been imported from it.",
				primaryButton: "Delete",
				secondaryButton: "Cancel"
			}
		},
		cancelDialogOptions(): any {
			return {
				title: `Cancel ${this.editMode ? 'Edit Server' : 'Add New Server'}`,
				primaryText: "Are you sure you want to cancel?",
				secondaryText: "All entered parameters will be lost.",
				primaryButton: "Yes, Cancel",
				secondaryButton: "No, Continue"
			}
		},
		activePayer() {
			return this.$store.getters.activePayer;
		}
	},
	mounted() {
		this.form = { ...this.form, ...this.data };
	},
	methods: {
		//
		// Add new server or save changes, show notification on success save
		//
		onSave() {
			this.isSaving = true;
			if (!this.editMode) {
				this.$store.dispatch("addServer", this.form)
				.then(() => {
					this.$emit("hide-form");
					this.$notify({
						title: "Success",
						message: `"${this.form.name}" server was successfully added.`,
						type: "success"
					} as any);
				})
				.finally(() => {
					this.isSaving = false;
				});
			} else {
				this.$store.dispatch("changeServer", this.form)
				.then(() => {
					this.$notify({
						title: "Success",
						message: `"${this.form.name}" server was successfully changed.`,
						type: "success"
					} as any);
				})
				.finally(() => {
					this.isSaving = false;
				});
			}
		},
		//
		// Show delete dialog
		//
		onDelete() {
			this.showDeleteDialog = true;
		},
		//
		// Delete server, show notification about success delete, hide form
		//
		deleteServer() {
			this.$store.dispatch("deleteServer", this.form)
			.then(() => {
				// means active payer was deleted so we do a redirect on home page
				if (!this.activePayer) {
					this.$router.push("/");
				}
				this.$emit("hide-form");
				this.$notify({
					title: "Success",
					message: `"${this.data.name}" server was successfully deleted.`,
					type: "success"
				} as any);
			})
			.finally(() => {
				this.showDeleteDialog = false;
			});
		},
		//
		// Show cancel dialog
		//
		onCancel() {
			if (!this.hasChanges) {
				this.$emit('hide-form')
				return;
			}

			this.showCancelDialog = true;
		},
		//
		// hide dialog and form section and reset fields
		//
		cancelEdit() {
			this.showCancelDialog = false;
			this.$emit('hide-form')
			// todo: reset form using this.$refs.form.resetFields()
			this.form = {...this.form, ...this.data};
		}
	}
});
</script>

<template>
	<div class="server-form">
		<div
			v-if="!editMode"
			class="header"
		>
			<h2 class="title">
				Create New Server
			</h2>
		</div>
		<el-form
			ref="form"
			:model="form"
			:rules="rules"
			label-width="140px"
			label-position="left"
		>
			<el-form-item
				label="Server Name"
				prop="name"
			>
				<el-input
					v-model="form.name"
					:disabled="isSaving"
				/>
			</el-form-item>
			<el-form-item
				label="Server URI"
				prop="fhirServerUri"
			>
				<el-input
					v-model="form.fhirServerUri"
					:disabled="disableField"
				/>
			</el-form-item>
			<el-form-item
				label="Client ID"
				prop="clientId"
			>
				<el-input
					v-model="form.clientId"
					:disabled="disableField"
				/>
			</el-form-item>
			<el-form-item
				label="Scope"
				prop="scope"
			>
				<el-input
					v-model="form.scope"
					:disabled="disableField"
				/>
			</el-form-item>
			<el-form-item
				v-if="form.authorizeUri"
				label="Auth URI"
			>
				<el-input
					v-model="form.authorizeUri"
					disabled
				/>
			</el-form-item>
			<el-form-item
				v-if="form.tokenUri"
				label="Token URI"
			>
				<el-input
					v-model="form.tokenUri"
					disabled
				/>
			</el-form-item>
		</el-form>
		<div
			v-if="editMode"
			class="footnote"
		>
			<div v-if="form.lastImported !== null">
				Server parameters cannot be edited if the data was imported from it.
			</div>
			<div>Deleting server will erase all data that has been imported from it.</div>
		</div>
		<div class="footer">
			<el-button
				type="primary"
				:disabled="isFormInvalid || !hasChanges"
				:loading="isSaving"
				@click="onSave"
			>
				Save Changes
			</el-button>
			<el-button
				v-if="editMode"
				:disabled="isSaving"
				@click="onDelete"
			>
				Delete Server
			</el-button>
			<el-button
				:disabled="isSaving"
				@click="onCancel"
			>
				Cancel
			</el-button>
		</div>
		<ServerConfirmDialog
			:show-dialog="showDeleteDialog"
			:options="deleteDialogOptions"
			@hide-dialog="showDeleteDialog = false"
			@confirm="deleteServer"
		/>
		<ServerConfirmDialog
			:show-dialog="showCancelDialog"
			:options="cancelDialogOptions"
			@hide-dialog="showCancelDialog = false"
			@confirm="cancelEdit"
		/>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
$form-left-right-padding: $global-margin-medium + 24px; // 24 px - size of el-collapse-item__arrow

.server-form {
	.header {
		background: $global-background-gray;
		padding: $global-margin $global-margin-medium;

		.title {
			font-size: $global-medium-font-size;
			font-weight: $global-font-weight-normal;
			line-height: 20px;
			margin: 0;
		}
	}

	.el-form {
		padding: $global-margin-medium $form-left-right-padding 0;
	}

	.footnote {
		font-size: $global-xsmall-font-size;
		color: $global-text-muted-color;
		padding: 0 $form-left-right-padding $global-margin-medium;
	}

	.footer {
		background: $global-background-gray;
		border-bottom: $global-base-border;
		padding: $global-margin-small $form-left-right-padding;
		display: flex;
		justify-content: flex-end;
	}
}
</style>
