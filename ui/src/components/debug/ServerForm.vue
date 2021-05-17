<script lang="ts">
import { defineComponent } from "vue";
import _ from "@/vendors/lodash";
import AppConfirmDialog from "@/components/AppConfirmDialog.vue";
import { PayersModule } from "@/store/modules/payers";
import { validatePayer } from "@/api/api";
import { NewPayerPayload } from "@/types";
import { PayerModule } from "@/store/modules/payer";

const DEFAULT_SERVER = { name: "", fhirServerUri: "", clientId: "", scope: "openid fhirUser offline_access user/*.read", tokenUri: "", authorizeUri: "" };

export default defineComponent({
	name: "ServerForm",
	components: { AppConfirmDialog },
	props: {
		id: {
			type: Number,
			default: null
		},
		data: {
			type: Object,
			default: () => ({ ...DEFAULT_SERVER })
		},
		editMode: {
			type: Boolean,
			default: false
		},
		lastImported: {
			type: String,
			default: null
		}
	},
	emits: ["hide-form", "changed"],
	data() {
		return {
			form: { ...DEFAULT_SERVER },
			rules: {
				name: [{ required: true, message: "This field is required", trigger: "change" }],
				fhirServerUri: [{ required: true, message: "This field is required", trigger: "change" }],
				clientId: [{ required: true, message: "This field is required", trigger: "change" }],
				scope: [{ required: true, message: "This field is required", trigger: "change" }]
			},
			isSaving: false,
			showCancelDialog: false,
			showDeleteDialog: false,
			isValidating: false,
			validatedFhirServerUri: ""
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
			return PayerModule.isActiveProgress || this.isSaving || (this.editMode && this.lastImported !== null);
		},
		deleteDialogOptions(): any {
			return {
				title: "Delete Server",
				primaryText: `Are you sure you want to delete "${this.data.name}" server?`,
				secondaryText: this.lastImported !== null ? "Deleting this server will erase all data that has been imported from it." : "",
				primaryButton: "Delete",
				secondaryButton: "Cancel"
			};
		},
		cancelDialogOptions(): any {
			return {
				title: `Cancel ${this.editMode ? "Edit Server" : "Add New Server"}`,
				primaryText: "Are you sure you want to cancel?",
				secondaryText: "All entered parameters will be lost.",
				primaryButton: "Yes, Cancel",
				secondaryButton: "No, Continue"
			};
		},
		disableButton(): boolean {
			return this.isActiveProgress || this.isFormInvalid;
		},
		isActiveProgress(): boolean | null {
			return PayerModule.isActiveProgress;
		}
	},
	watch: {
		hasChanges() {
			this.$emit("changed", this.hasChanges);
		}
	},
	mounted() {
		this.form = { ...this.form, ...this.data };
		this.scrollToElement();
	},
	methods: {
		//
		// scroll to the active element
		//
		scrollToElement() {
			this.$el.scrollIntoView({ behavior: "smooth" });
		},
		//
		// Add new server or save changes, show notification on success save.
		// Omit tokenUri and authorizeUri params from payload if you are creating new server and didn't validate before,
		// or when you are updating existing server and you changed fhirServerUri field.
		//
		async onSave() {
			this.isSaving = true;
			if (!this.editMode) {
				try {
					const payload: NewPayerPayload = this.validatedFhirServerUri === this.form.fhirServerUri ? this.form : _.omit(this.form, ["tokenUri", "authorizeUri"]);

					await PayersModule.addPayer(payload);
					this.$emit("hide-form");
					this.$notify({
						title: "Success",
						message: `"${this.form.name}" server was successfully added.`,
						type: "success"
					});
				} finally {
					this.isSaving = false;
				}
			} else {
				try {
					const payload: NewPayerPayload = _.isEqual(this.form.fhirServerUri, this.data.fhirServerUri) ? this.form : _.omit(this.form, ["tokenUri", "authorizeUri"]);

					await PayersModule.updatePayer({ id: this.id, data: payload });
					this.$notify({
						title: "Success",
						message: `"${this.form.name}" server was successfully changed.`,
						type: "success"
					});
				} finally {
					this.isSaving = false;
				}
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
		async deleteServer() {
			try {
				await PayersModule.deletePayer(this.id);

				if (PayersModule.activePayerId === this.id) {
					await this.$router.push("/");
				}
				this.$emit("hide-form");
				this.$emit("changed", false);
				this.$notify({
					title: "Success",
					message: `"${this.data.name}" server was successfully deleted.`,
					type: "success"
				});
			} finally {
				this.showDeleteDialog = false;
			}
		},
		//
		// Show cancel dialog
		//
		onCancel() {
			if (!this.hasChanges) {
				this.$emit("hide-form");
				return;
			}

			this.showCancelDialog = true;
		},
		//
		// hide dialog and form section and reset fields
		//
		cancelEdit() {
			this.showCancelDialog = false;
			if (!this.editMode) {
				this.$emit("hide-form");
			}
			// todo: reset form using this.$refs.form.resetFields()
			this.form = { ...this.form, ...this.data };
		},
		//
		// Validate button click handler.
		// If we get token and authorization uris that means everything is fine and connection is ok.
		// Show error/success message, populate form with given fields.
		// If we are editing existing payer and token or authorization changed do auto payer save.
		//
		async onValidate(): Promise<void> {
			this.isValidating = true;

			try {
				const { authorize, token } = await validatePayer(this.form.fhirServerUri);

				if (authorize && token) {
					const needSave: boolean = this.editMode && (this.form.authorizeUri !== authorize || this.form.tokenUri !== token);

					this.form.authorizeUri = authorize;
					this.form.tokenUri = token;
					this.$notify({
						title: "Success",
						message: "Connection successful with server.",
						type: "success"
					});
					this.validatedFhirServerUri = this.form.fhirServerUri;
					if (needSave) {
						await this.onSave();
					}
				} else {
					this.$notify({
						title: "Error",
						message: "Connection wasn't successful with server.",
						type: "error"
					});
				}
			} finally {
				this.isValidating = false;
			}
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
				New Server
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
			v-if="editMode && lastImported !== null"
			class="footnote"
		>
			<div>Deleting server will erase all data that has been imported from it.</div>
			<div>Server parameters cannot be edited if the data was imported from it.</div>
		</div>
		<div class="footer">
			<el-button
				size="small"
				:disabled="disableButton"
				:loading="isValidating"
				@click="onValidate"
			>
				Validate
			</el-button>
			<el-button
				size="small"
				:disabled="disableButton || !hasChanges"
				:loading="isSaving"
				@click="onSave"
			>
				{{ isSaving ? "Saving Changes" : "Save Changes" }}
			</el-button>
			<el-button
				v-if="editMode"
				size="small"
				:disabled="isSaving || disableButton"
				@click="onDelete"
			>
				Delete Server
			</el-button>
			<el-button
				size="small"
				:disabled="isSaving || isActiveProgress"
				@click="onCancel"
			>
				Cancel
			</el-button>
		</div>
		<AppConfirmDialog
			:show-dialog="showDeleteDialog"
			:options="deleteDialogOptions"
			@hide-dialog="showDeleteDialog = false"
			@confirm="deleteServer"
		/>
		<AppConfirmDialog
			:show-dialog="showCancelDialog"
			:options="cancelDialogOptions"
			@hide-dialog="showCancelDialog = false"
			@confirm="cancelEdit"
		/>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
$form-left-right-padding: $global-margin-medium;

.server-form {
	.header {
		padding: $global-margin $global-margin-medium;

		.title {
			font-size: $global-font-size;
			font-weight: $global-font-weight-medium;
			line-height: 20px;
			margin: 0;
		}
	}

	.el-form {
		padding: $global-margin-medium $form-left-right-padding 0 44px;
	}

	.footnote {
		font-size: $global-xsmall-font-size;
		color: $global-text-muted-color;
		padding: 0 $form-left-right-padding $global-margin-medium 44px;
	}

	.footer {
		border-bottom: $global-base-border;
		border-top: $global-base-border;
		padding: $global-margin-small $form-left-right-padding;
		display: flex;
		justify-content: flex-end;

		.el-button {
			min-width: 155px;
		}
	}
}
</style>
