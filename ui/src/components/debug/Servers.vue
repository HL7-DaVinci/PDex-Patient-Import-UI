<script lang="ts">
import { computed, defineComponent, nextTick, ref, watch } from "vue";
import ServerForm from "./ServerForm.vue";
import NoServers from "./NoServers.vue";
import { Payer } from "@/types";
import { PayersModule } from "@/store/modules/payers";

export default defineComponent({
	components: { NoServers, ServerForm },
	emits: ["back-to-calls"],
	setup(_, { emit }) {
		const showAddServerForm = ref<boolean>(false);
		const activeServers = ref<number | null>(null);
		const hasUnsavedChanges = ref<boolean>(false);

		const servers = computed<Payer[]>(() => PayersModule.payers);

		watch(servers, (val, oldVal) => {
			if (val.length > oldVal.length) {
				nextTick(() => activeServers.value = val[val.length - 1].id);
			}
		});
		watch(showAddServerForm, val => {
			if (val) {
				collapseSection();
			}
		});

		const collapseSection = (): void => {
			activeServers.value = null;
		};
		const backToCalls = (): void => emit("back-to-calls");

		return {
			showAddServerForm,
			activeServers,
			hasUnsavedChanges,
			servers,
			collapseSection,
			backToCalls
		};
	}
});
</script>

<template>
	<div class="servers-configuration">
		<div class="header">
			<h2 class="title">
				Manage Servers
			</h2>
			<div>
				<el-button
					v-if="servers.length || showAddServerForm"
					size="small"
					:disabled="showAddServerForm || hasUnsavedChanges"
					@click="showAddServerForm = true"
				>
					Add New Server
				</el-button>
				<el-button
					v-if="servers.length"
					size="small"
					:disabled="showAddServerForm || hasUnsavedChanges"
					@click="backToCalls"
				>
					Back to FHIR Requests
				</el-button>
			</div>
		</div>
		<div class="content">
			<el-collapse
				v-if="servers.length"
				v-model="activeServers"
				accordion
			>
				<el-collapse-item
					v-for="item in servers"
					:key="item.id"
					:title="item.name"
					:name="item.id"
					:disabled="hasUnsavedChanges"
				>
					<ServerForm
						:id="item.id"
						:data="{
							name: item.name,
							fhirServerUri: item.fhirServerUri,
							clientId: item.clientId,
							scope: item.scope,
							authorizeUri: item.authorizeUri,
							tokenUri: item.tokenUri
						}"
						:last-imported="item.lastImported"
						:edit-mode="true"
						@hide-form="collapseSection"
						@changed="hasUnsavedChanges = $event"
					/>
				</el-collapse-item>
			</el-collapse>
			<NoServers
				v-else-if="!showAddServerForm"
				@add-new-server="showAddServerForm = true"
			/>
			<ServerForm
				v-if="showAddServerForm"
				@hide-form="showAddServerForm = false"
				@changed="hasUnsavedChanges = $event"
			/>
		</div>
	</div>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
$form-left-right-padding: $global-margin-medium + 24px; // 24 px - size of el-collapse-item__arrow

.header {
	background: $global-background-gray;
	border-bottom: $global-base-border;
	padding: $global-margin-small $global-margin-medium;
	display: flex;
	justify-content: space-between;
	align-items: center;

	.title {
		font-size: $global-font-size;
		font-weight: $global-font-weight-normal;
		margin: $global-margin-small 0;
	}
}

.servers-configuration {
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: stretch;

	.content {
		height: 100%;
		overflow-y: auto;
	}
}
</style>
