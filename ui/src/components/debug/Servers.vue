<script lang="ts">
import { defineComponent } from "vue";
import ServerForm from "./ServerForm.vue";
import NoServers from "./NoServers.vue";

export default defineComponent({
	name: "Servers",
	components: { NoServers, ServerForm },
	data() {
		return {
			showAddServerForm: false,
			activeServers: []
		};
	},
	computed: {
		servers() {
			return this.$store.getters.servers;
		}
	},
	methods: {
		collapseSection(el: string) {
			this.activeServers = this.activeServers.filter(item => item !== el);
		}
	}
});
</script>

<template>
	<div class="servers-configuration">
		<div class="header">
			<h2 class="title">
				Manage Servers
			</h2>
			<el-button
				v-if="servers.length || showAddServerForm"
				:disabled="showAddServerForm"
				@click="showAddServerForm = true"
			>
				Add New Server
			</el-button>
		</div>
		<div class="content">
			<el-collapse
				v-if="servers.length"
				v-model="activeServers"
			>
				<el-collapse-item
					v-for="item in servers"
					:key="item.id"
					:title="item.name"
					:name="item.id"
				>
					<ServerForm
						:data="item"
						:edit-mode="true"
						@hide-form="collapseSection(item.id)"
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
	padding: $global-margin-small $form-left-right-padding $global-margin-small $global-margin-medium;
	display: flex;
	justify-content: space-between;

	.title {
		font-size: $global-medium-font-size;
		font-weight: $global-font-weight-medium;
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
