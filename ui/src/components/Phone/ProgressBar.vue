<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	props: {
		percentage: {
			type: Number,
			required: true
		},
		thin: {
			type: Boolean,
			default: false
		}
	}
});
</script>

<template>
	<div
		:class="{ thin }"
		class="progress"
	>
		<span
			class="progress-line"
			:style="`width: ${percentage}%`"
		></span>
	</div>
</template>

<style lang="scss">
@import "~@/assets/scss/abstracts/variables.scss";

.progress {
	box-sizing: content-box;
	height: 22px;
	position: relative;
	border-radius: 10px;
	background: $white;
	overflow: hidden;
	width: 100%;
	box-shadow: inset 0 -1px 1px rgba(255, 255, 255, 0.3);

	.progress-line {
		display: block;
		height: 100%;
		border-radius: 10px;
		background: $picton-blue;
		position: relative;
		overflow: hidden;
		transition: width 0.5s;
		box-shadow:
			inset 0 2px 9px rgba(255, 255, 255, 0.3),
			inset 0 -2px 6px rgba(0, 0, 0, 0.4);

		&::after {
			content: "";
			position: absolute;
			top: 0;
			left: 0;
			bottom: 0;
			right: 0;
			background-image:
				linear-gradient(
					-45deg,
					rgba(255, 255, 255, 0.2) 25%,
					transparent 25%,
					transparent 50%,
					rgba(255, 255, 255, 0.2) 50%,
					rgba(255, 255, 255, 0.2) 75%,
					transparent 75%,
					transparent
				);
			z-index: 1;
			background-size: 50px 50px;
			animation: move 2s linear infinite;
			border-radius: 10px;
			overflow: hidden;
		}
	}

	&.thin {
		height: 2px;
		box-shadow: none;
		border-radius: 0;

		.progress-line {
			border-radius: 0;
			box-shadow: none;
			background-color: $active-color;

			&::after {
				content: none;
			}
		}
	}
}

@keyframes move {
	0% {
		background-position: 0 0;
	}

	100% {
		background-position: 50px 50px;
	}
}
</style>
