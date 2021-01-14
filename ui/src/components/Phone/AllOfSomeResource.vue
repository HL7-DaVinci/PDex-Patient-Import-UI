<script lang="ts">
import { defineComponent } from "vue";
import Header from "@/components/Phone/Header.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import ResourceLoader from "../../utils/ResourceLoader";
import Mappings from "../../utils/resourceMappings.js";
import InfiniteScrollArea from "./InfiniteScrollArea.vue";

export default defineComponent({
	components: { Header, CollapseGroup, InfiniteScrollArea },
	props: {
		resourceType: {
			type: String,
			required: true
		}
	},
	data() {
		return {
			resourceLoader: new ResourceLoader([])
		};
	},
	computed: {
		resourceCount() {
			const item = this.$store.getters.totalResourceOverview.find(x => x.resourceType === this.resourceType);
			return item ? item.count : "";
		},
		payers() {
			return this.$store.getters.importedPayers;
		},
		searchByPayer() {
			return Object.fromEntries(
				this.payers.map(payer => [payer.id, `/fhir/${this.resourceType}?_source=${payer.id}`])
			);
		},
		resourcesByPayer() {
			return this.resourceLoader.results;
		},
		payersWithResources() {
			return this.payers.filter(payer => this.resourcesByPayer[payer.id].length > 0);
		}
	},
	watch: {
		searchByPayer(searches) {
			this.resourceLoader = new ResourceLoader(searches, 20);
			this.resourceLoader.load();
		}
	},
	created() {
		this.resourceLoader = new ResourceLoader(this.searchByPayer, 20);
		this.resourceLoader.load();
		this.$store.dispatch("getTotalResourceOverview");
	},
	methods: {
		loadMore() {
			this.resourceLoader.load();
		},
		goToPayer(payerId: string) {
			this.$router.push(`/payer/${payerId}`);
		},
		goBack() {
			this.$router.push(`/all-data`);
		},
		getResourceFields(res) {
			return Object.values(Mappings[this.resourceType].convert(res));
		},
		getResourceTitle(res) {
			return this.getResourceFields(res)[0].value;
		}
	}
});
</script>

<template>
	<div class="all-of-resource">
		<Header
			:title="`${resourceType} (${resourceCount})`"
			@back="goBack"
		/>

		<InfiniteScrollArea
			class="scroll-area"
			@more="loadMore"
		>
			<div class="sub-header">
				Combined imported data from all payers.
			</div>

			<div
				v-for="payer in payersWithResources"
				:key="payer.id"
			>
				<h2 class="section-header">
					<span>{{ payer.name }}</span>
					<img
						src="~@/assets/images/arrow.svg"
						class="arrow"
						@click="goToPayer(payer.id)"
					>
				</h2>

				<CollapseGroup
					:items="resourcesByPayer[payer.id]"
				>
					<template #title="{ item }">
						<div
							:class="{ 'no-data': !getResourceTitle(item) }"
							class="resource-title"
						>
							{{ getResourceTitle(item) || "no data" }}
						</div>
					</template>

					<template #default="{ item }">
						<div
							v-for="field in getResourceFields(item)"
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
			</div>
		</InfiniteScrollArea>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts.scss";

.all-of-resource {
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
		flex: 1;
	}

	.section-header {
		height: 50px;
		background-color: $black-russian;
		border: 1px solid $platinum;
		font-size: 13px;
		font-weight: $global-font-weight-light;
		margin: 0;
		padding: 25px 0 0 $global-margin-large;
		text-transform: uppercase;
		position: sticky;
		z-index: 1;
		top: -1px;

		.arrow {
			transform: rotate(180deg);
			position: absolute;
			right: $global-margin-large;
			top: $global-margin-medium;
			cursor: pointer;
		}
	}

	.resource-title {
		font-size: $global-large-font-size;
		font-weight: $global-font-weight-normal;
		padding: $global-margin-medium 0;
	}

	.field {
		margin-left: 15px;

		&:not(:first-child) {
			margin-top: 13px;
		}
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
}
</style>
