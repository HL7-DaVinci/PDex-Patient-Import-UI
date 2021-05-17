<script lang="ts">
import { defineComponent } from "vue";
import Header from "@/components/Phone/Header.vue";
import CollapseGroup from "@/components/Phone/CollapseGroup.vue";
import ResourceLoader from "../../utils/ResourceLoader";
import Mappings from "../../utils/resourceMappings.js";
import InfiniteScrollArea from "./InfiniteScrollArea.vue";
import { PayerModule } from "@/store/modules/payer";
import { PayersModule } from "@/store/modules/payers";

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
			resourceLoader: new ResourceLoader([]),
			openedPayers: []
		};
	},
	computed: {
		resourceCount() {
			const item = PayerModule.totalSupportedResourcesOverview.find(x => x.resourceType === this.resourceType);
			return item ? item.count : "";
		},
		payers() {
			return PayersModule.importedPayers;
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
			return this.payers.filter(payer => this.resourcesByPayer[payer.id] && this.resourcesByPayer[payer.id].length > 0);
		},
		isResourceLoading() {
			return this.resourceLoader.loading;
		}
	},
	watch: {
		searchByPayer(searches) {
			this.resourceLoader = new ResourceLoader(searches, 20);
			this.resourceLoader.load();
		},
		payersWithResources(val) {
			this.openedPayers = [...val.map(p => p.id)];
		}
	},
	created() {
		this.resourceLoader = new ResourceLoader(this.searchByPayer, 20);
		this.resourceLoader.load();
		PayerModule.getTotalResourcesOverview();
	},
	methods: {
		loadMore() {
			this.resourceLoader.load();
		},
		goBack() {
			this.$router.push("/all-data");
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
			:loading="isResourceLoading"
			@more="loadMore"
		>
			<div class="sub-header">
				Combined imported data from all payers.
			</div>

			<van-collapse v-model="openedPayers">
				<van-collapse-item
					v-for="payer in payersWithResources"
					:key="payer.id"
					:title="payer.name"
					:name="payer.id"
					class="section-header"
				>
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
								v-for="(field, index) in getResourceFields(item)"
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
				</van-collapse-item>
			</van-collapse>
		</InfiniteScrollArea>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/abstracts";
@import "~@/assets/scss/abstracts/mixins";

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

		@include dont-break-out();
	}

	.no-data {
		color: $pinkish-grey;
	}

	.section-header {
		> ::v-deep(.van-cell) {
			height: 50px;
			background-color: $black-russian;
			padding: 25px $global-margin-large $global-margin-small;
			border: 1px solid $platinum;
			position: sticky;
			z-index: 1;
			top: -1px;

			.van-cell__title {
				font-size: $global-intermediate-font-size;
				font-weight: $global-font-weight-light;
				text-transform: uppercase;

				@include text-ellipsis();
			}
		}

		> ::v-deep(.van-collapse-item__wrapper) {
			.van-collapse-item__content {
				padding: 0;
			}
		}
	}
}
</style>
