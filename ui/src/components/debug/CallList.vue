<script lang="ts">
import { defineComponent, PropType } from "vue";
import CallListItem from "./CallListItem.vue";
import { Call } from "@/types";

export default defineComponent({
	components: {
		CallListItem
	},
	props: {
		data: {
			type: Array as PropType<Call[]>,
			required: true
		},
		activeId: {
			type: String,
			default: null
		},
		searchMode: {
			type: Boolean,
			default: false
		},
		type: {
			type: String,
			required: true
		}
	},
	emits: ["select"],
	setup(_, { emit }) {
		const handleClick = (call: Call) => emit("select", call.requestId);

		return {
			handleClick
		};
	}
});
</script>

<template>
	<ul
		v-if="data.length"
		class="call-list"
	>
		<CallListItem
			v-for="(call, index) in data"
			:key="index"
			:call="call"
			:selected="activeId === call.requestId"
			@click="handleClick(call)"
		/>
	</ul>
	<div
		v-else-if="searchMode"
		class="no-results"
	>
		<img
			class="no-results-image"
			:src="require(`@/assets/images/icon-search.svg`)"
			alt="No search results image"
		>
		<span class="no-results-text">No search results</span>
	</div>
	<div
		v-else
		class="no-data"
	>
		<img
			class="no-data-image"
			:src="require(`@/assets/images/no-fhir-request-img.svg`)"
			alt="No data image"
		>
		<span class="no-data-header">
			No {{ type }} Requests yet
		</span>
		<span class="no-data-text">Details of {{ type }} server request will appear here.</span>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins.scss";

.call-list {
	padding: 10px 0;
}

.no-data,
.no-results {
	display: flex;
	align-items: center;
	flex-direction: column;
	margin-top: 70px;
	color: $grey;
}

.no-data {
	&-image {
		width: 165px;
	}

	&-header {
		font-size: $global-xxlarge-font-size;
		line-height: 28px;
		margin: 30px 0 15px 0;
	}

	&-text {
		font-size: $global-large-font-size;
		line-height: 21px;
	}
}

.no-results {
	&-image {
		width: 65px;
	}

	&-text {
		margin-top: $global-margin-large;
		font-size: $global-large-font-size;
		line-height: 21px;
	}
}
</style>
