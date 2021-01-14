<script lang="ts">
import { defineComponent } from "vue";
import { mapGetters } from "vuex";
import PayerHeader from "./PayerHeader.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import ResourceList from "@/components/Phone/ResourceList.vue";
import ProgressScreen from "../ProgressScreen.vue";

export default defineComponent({
	components: {
		ResourceList,
		CollapseGroup,
		PayerHeader,
		ProgressScreen
	},
	props: {
		id: {
			type: String,
			required: true
		}
	},
	data(){
		return {
			showConfirm: false,
			isRemoving: false
		};
	},
	computed: {
		...mapGetters([
			"resourceOverview",
			"patient",
			"activePayer",
			"activePayerId"
		])
	},
	created() {
		this.getPatientInfo();
		this.getResourceOverview();
	},
	methods: {
		removePayerData() {
			this.showConfirm = false;
			this.isRemoving = true;
			this.$store.dispatch("removePayerData", this.activePayerId)
				.then(() => {
					this.$store.dispatch("changeServerLastImportedDate", { id: this.activePayerId, lastImported: null });
					this.$router.push("/");
				});
		},
		getPatientInfo() {
			this.$store.dispatch("getPatientInfo", this.activePayerId);
		},
		getResourceOverview() {
			this.$store.dispatch("getResourceOverview", this.activePayerId);
		},
		goToResource(resourceType){
			this.$router.push(`/payer/${this.id}/${resourceType}`);
		},
		//
		// To avoid patient name duplication
		//
		omitPatientName(patientInfo) {
			const { name, ...patientInfoWithoutName } = patientInfo;
			return patientInfoWithoutName;
		}
	}
});
</script>

<template>
	<div
		v-if="activePayer"
		class="payer"
	>
		<PayerHeader
			:class="{'eclipse': isRemoving }"
			:title="activePayer.name"
			@showRemovePayerDialog="showConfirm = true"
		/>
		<div
			v-if="!isRemoving"
			class="scroll-area"
		>
			<div class="sub-header">
				<span>
					Last Import: {{ $filters.formatDate(activePayer.lastImported) }}
				</span>
				<span class="action-wrap">
					<a
						href="#"
						@click.prevent
					>
						<img
							src="~@/assets/images/time-icon.svg"
							alt="time"
						>
					</a>
					<a
						href="#"
						@click.prevent
					>
						<img
							src="~@/assets/images/refresh.svg"
							alt="refresh"
						>
					</a>
				</span>
			</div>

			<h2 class="section-header">
				GENERAL INFO
			</h2>
			<CollapseGroup
				v-if="Object.keys(patient).length"
				:items="[patient]"
			>
				<template #title="{ item }">
					<div class="field patient-title">
						<div class="label">
							Patient name
						</div>
						<div
							class="value"
							:class="{ 'no-data': !item.name.value }"
						>
							{{ item.name.value || "no data" }}
						</div>
					</div>
				</template>

				<template #default="{ item }">
					<div
						v-for="(field, index) in omitPatientName(item)"
						:key="index"
						class="field"
					>
						<div class="label">
							{{ field.label }}
						</div>
						<div
							:class="{ 'no-data': !field.value }"
							class="value"
						>
							{{ field.value || "no data" }}
						</div>
					</div>
				</template>
			</CollapseGroup>

			<h2 class="section-header">
				RESOURCES
			</h2>
			<ResourceList
				v-if="resourceOverview.length > 0"
				:resources="resourceOverview"
				@click-resource="goToResource"
			/>
			<div
				v-else
				class="no-resources"
			>
				<div class="icon"></div>
				<div class="primary">No data to display</div>
				<div class="secondary">There are no resources from this payer.</div>
			</div>
		</div>
		<van-dialog
			:show="showConfirm"
			title="Are you sure?"
			show-cancel-button
			confirm-button-text="Delete"
			cancel-button-text="Cancel"
			@cancel="showConfirm = false"
			@confirm="removePayerData"
		>
			Once you confirm, imported payer data will be permanently deleted.
		</van-dialog>
		<ProgressScreen
			v-if="isRemoving"
			title="Clear Data in progress"
			description="Please wait, it may take a few seconds to delete all your information."
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.payer {
	background-color: $black-russian;
	position: relative;
	display: flex;
	flex-direction: column;
	height: 100%;

	.scroll-area {
		overflow-y: overlay;
		flex: 1;
		display: flex;
		flex-direction: column;
		& > * {
			flex-shrink: 0;
		}
	}

	.no-resources {
		padding: $global-margin-large $global-margin-medium;
		flex: 1;
		background-color: $mirage;
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;

		.icon {
			color: $pinkish-grey;
			@include icon("~@/assets/images/no-data.svg", 100px);
		}

		.primary {
			font-size: $global-xxlarge-font-size;
			font-weight: $global-font-weight-medium;
			margin-top: $global-margin-large;
		}

		.secondary {
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-normal;
			margin-top: $global-margin-small;
		}
	}
}

.eclipse {
	opacity: .2;
}

.sub-header {
	background-color: $mirage;
	padding: $global-margin $global-margin-large;
	display: flex;
	align-items: center;
	justify-content: space-between;
	border-top: 1px solid $platinum;
	border-bottom: 1px solid $platinum;
	margin-top: $global-margin-medium;
	font-size: $global-font-size;
	font-weight: $global-font-weight-normal;

	.action-wrap {
		display: flex;

		a:last-child {
			margin-left: $global-margin;
		}
	}
}

.section-header {
	height: 50px;
	background-color: $black-russian;
	border: 1px solid $platinum;
	font-size: 13px;
	font-weight: 300;
	padding: 25px 0 0 30px;
	text-transform: uppercase;
	margin: 0;
}

.field:not(:first-child) {
	margin-top: 13px;
}

.label {
	font-size: 14px;
	font-weight: 300;
	margin-bottom: 8px;
}

.value {
	font-size: 18px;
	font-weight: 400;
}

.patient-title {
	padding: 13px 0;
}

::v-deep(.van-overlay) {
	backdrop-filter: blur(3px);
}

.no-data {
	color: $pinkish-grey;
}
</style>