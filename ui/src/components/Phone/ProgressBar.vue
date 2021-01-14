<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "ProgressBar"
});
</script>

<template>
	<div class="progress-line"></div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.progress-line, .progress-line:before {
	width: 100%;
	margin: 0;
	height: 22px;
	border-radius: 10px;
}
.progress-line {
	background-color: $white;
	display: -webkit-flex;
	display: flex;
	height: 22px;
	border-radius: 10px;
}

.progress-line:before {
	background-color: $picton-blue;
	content: '';
	-webkit-animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
	animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
}

@-webkit-keyframes running-progress {
	0% { margin-left: 0; margin-right: 95%; }
	50% { margin-left: 25%; margin-right: 0; }
	100% { margin-left: 100%; margin-right: 0; }
}

@keyframes running-progress {
	0% { margin-left: 0; margin-right: 95%; }
	50% { margin-left: 25%; margin-right: 0; }
	100% { margin-left: 95%; margin-right: 0; }
}
</style>
