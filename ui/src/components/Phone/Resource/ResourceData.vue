<script lang="ts">
import { defineComponent } from "vue";
import ResourceHeader from "./ResourceHeader.vue";
import SortMenu from "./SortMenu.vue";
import { mapGetters } from "vuex";
import ResourceLoader from "../../../utils/ResourceLoader";
import InfiniteScrollArea from "../InfiniteScrollArea.vue";
import CollapseGroup from "../CollapseGroup.vue";
import Mappings from "../../../utils/resourceMappings.js";

export default defineComponent({
	name: "ResourceData",
	components:{
		CollapseGroup,
		InfiniteScrollArea,
		ResourceHeader,
		SortMenu
	},
	props: {
		id: {
			type: String,
			required: true
		},
		resourceType: {
			type: String,
			required: true
		}
	},
	data() {
		return {
			menuOpened: false,
			resourceLoader: new ResourceLoader({ [this.id]: `/fhir/${this.resourceType}?_source=${this.id}` })
		};
	},
	computed: {
		...mapGetters([
			"patient",
			"servers",
			"patientId"
		]),
		rawResources() {
			return this.resourceLoader.results[this.id];
		},
		resources() {
			return this.rawResources.map(Mappings[this.resourceType].convert);
		},
		payer(): any {
			return this.servers.find((item: any) => item.id === +this.id);
		}
	},
	watch: {
		payer() {
			//todo - Should be reworked. App will be not ready while we are waiting for servers.
			this.loadData();
		}
	},
	mounted() {
		this.loadData();
	},
	methods: {
		loadData() {
			if (this.payer && this.payer.lastImported !== null) {
				this.getPatientInfo();
				this.resourceLoader.load();
				this.$store.dispatch("getResourceOverview", this.id);
			}
		},
		getResourceTitle(res) {
			const { value } = (Object.values(res) as any)[0];
			return value;
		},
		getPatientInfo() {
			if (this.patientId !== this.payer.id) {
				this.$store.dispatch("getPatientInfo", this.payer.id);
			}
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
	<div class="resource">
		<ResourceHeader
			@openSortMenu="menuOpened = true"
			:resource-type="resourceType"
			:id="patientId"
		/>
		<InfiniteScrollArea
			class="scroll-area"
			@more="resourceLoader.load()"
		>
			<div class="last-imported">
				{{ resources ? ` Last import: ${ $filters.formatDate(payer.lastImported) } ` : "" }}
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
				class="section-content"
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
							{{ item.name.value ? item.name.value : "no data" }}
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
			<CollapseGroup
				:items="resources"
				class="section-content"
			>
				<template #title="{ item }">
					<div
						:class="{ 'no-data': !getResourceTitle(item) }"
						class="resource-title"
					>
						{{ getResourceTitle(item) || "no data" }}
					</div>
				</template>

				<template
					#default="{ item }"
				>
					<div
						v-for="(field, index) in item"
						:key="index"
						class="field resource-field"
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
		</InfiniteScrollArea>
		<SortMenu v-model:show="menuOpened" />
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.resource {
	background-color: $black-russian;
	height: 100%;
	display: flex;
	flex-direction: column;

	.last-imported {
		background-color: $mirage;
		padding: $global-margin $global-margin-large;
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: $global-margin-medium;
		font-size: $global-font-size;
		font-weight: $global-font-weight-normal;
		border: 1px solid $platinum;

		.action-wrap {
			display: flex;

			a:last-child {
				margin-left: $global-margin;
			}
		}
	}

	.label {
		font-size: $global-small-font-size;
		font-weight: $global-font-weight-light;
	}

	.resource-title {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		padding: $global-margin-medium 0;
	}

	.resource-field {
		margin-left: 15px;
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
	}

	.no-data {
		color: $pinkish-grey;
	}

	.patient-title {
		padding: 13px 0;
	}

	.scroll-area {
		flex: 1;
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		font-size: 13px	;
		font-weight: 300;
		padding: 25px 0 0 30px;
		text-transform: uppercase;
		margin: 0;
	}

	.section-content {
		border: 1px solid $platinum;
		background-color: $mirage;
	}
}
</style>
