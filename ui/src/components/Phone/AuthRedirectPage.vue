<script lang="ts">
import { defineComponent } from "vue";
import { AuthModule } from "@/store/modules/auth";
import router from "@/router";

export default defineComponent({
	setup() {
		const handleLogout = async () => {
			await AuthModule.logout();
			await router.push("/login");
		};
		return {
			handleLogout
		};
	}
});
</script>

<template>
	<div class="auth-redirect-page">
		<div class="header">
			<van-nav-bar>
				<template #left>
					<van-button
						:icon="require('@/assets/images/arrow-left.svg')"
						size="mini"
						@click="$router.push('/')"
					/>
				</template>
				<template #right>
					<van-button
						class="logout"
						@click="handleLogout"
					>
						Log Out
					</van-button>
				</template>
			</van-nav-bar>
		</div>

		<div class="content">
			<h3>
				Redirected from server authorization page.
			</h3>
		</div>
	</div>
</template>


<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables.scss";

.auth-redirect-page {
	height: 100%;
	display: flex;
	flex-direction: column;

	.header {
		height: 80px;
		background-color: $mirage;
		display: flex;
		align-items: flex-end;

		.logout {
			font-size: $global-large-font-size;
			font-weight: $global-font-weight-normal;
			color: $active-color;
			padding: 0;
			border: none;
			background: none;
		}
	}

	.content {
		padding: $global-margin-small $global-margin-large;
	}
}
</style>
