<script lang="ts">
import { defineComponent } from "vue";
import Header from "@/components/Phone/Header.vue";
import ResourceList from "@/components/Phone/ResourceList.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import Mappings from "../../utils/resourceMappings.js";

export default defineComponent({
	components: { Header, ResourceList, CollapseGroup },
	computed: {
		resources() {
			return this.$store.getters.totalResourcesOverview;
		},
		payers() {
			return this.$store.getters.importedPayers;
		},
		patients() {
			return this.$store.getters.allPatients;
		},
		payerPatient() {
			return this.payers.map(payer => ({
				payer,
				patient: this.findMatchingPatient(this.patients, payer)
			}));
		},
		goodPayerPatients() {
			return this.payerPatient.filter(pp => pp.payer && pp.patient);
		}
	},
	created() {
		this.$store.dispatch("getTotalResourcesOverview");
		this.$store.dispatch("getAllPatients");
	},
	methods: {
		goToResource(resourceType: string) {
			this.$router.push(`/all-data/${resourceType}`);
		},
		goBack() {
			this.$router.push("/");
		},
		findMatchingPatient(patients, payer) {
			const patient = patients.find(patient => patient.meta.source.replace(/#.*$/, "") === payer.id.toString());
			return patient ? Mappings.Patient.convert(patient) : null;
		}
	}
});
</script>

<template>
	<div class="all-data">
		<Header
			title="All Data"
			@back="goBack"
		/>

		<div class="scroll-area">
			<div class="sub-header">
				Combined imported data from all payers.
			</div>

			<h2 class="section-header">
				GENERAL INFO
			</h2>
			<CollapseGroup
				:items="goodPayerPatients"
			>
				<template #title="{ item }">
					<div class="payer-title">
						{{ item.payer.name }}
					</div>
				</template>

				<template #default="{ item }">
					<div
						v-for="field in Object.values(item.patient)"
						class="field"
					>
						<div class="label">{{ field.label }}</div>
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
				v-if="resources.length > 0"
				:resources="resources"
				@click-resource="goToResource"
			/>
			<div
				v-else
				class="no-resources"
			>
				<div class="icon"></div>
				<div class="primary">No data to display</div>
				<div class="secondary">There are no resources from this payers.</div>
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";
@import "~@/assets/scss/abstracts/mixins.scss";

.all-data {
	height: 100%;
	display: flex;
	flex-direction: column;
	background-color: $black-russian;

	.sub-header {
		background-color: $mirage;
		border-top: 1px solid $platinum;
		margin-top: $global-margin-medium;
		padding: $global-margin $global-margin-large;
		font-size: 15px;
		font-weight: $global-font-weight-normal;
	}

	.scroll-area {
		overflow-y: overlay;
		flex: 1;
		display: flex;
		flex-direction: column;
		& > * {
			flex-shrink: 0;
		}
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		border: 1px solid $platinum;
		font-size: 13px;
		font-weight: $global-font-weight-light;
		padding: 25px 0 0 $global-margin-large;
		text-transform: uppercase;
		margin: 0;
	}

	.payer-title {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		padding: $global-margin-medium 0;

		@include dont-break-out();
	}

	.field:not(:first-child) {
		margin-top: 13px;
	}

	.label {
		font-size: $global-font-size;
		font-weight: $global-font-weight-light;
		margin-bottom: 8px;
	}

	.value {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;

		@include dont-break-out();
	}

	.no-data {
		color: $pinkish-grey;
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
</style>
