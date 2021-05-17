<script lang="ts">
import { defineComponent, ref, computed } from "vue";
import { AuthModule } from "@/store/modules/auth";
import { goToImportOrHome } from "@/utils/utils";

export default defineComponent({
	name: "LoginForm",
	setup () {
		const username = ref<string>("");
		const password = ref<string>("");
		const checked = ref<boolean>(false);
		const loginError = ref<boolean>(false);

		const loginButtonEnabled = computed<boolean>(() => checked.value && username.value !== "" && password.value !== "");

		const handleLogin = async (): Promise<void> => {
			const payload = {
				username: username.value,
				password: password.value
			};

			try {
				await AuthModule.login(payload);
				await goToImportOrHome();
			} catch (err) {
				if (err.response.status === 401) {
					loginError.value = true;
				}
			}
		};
		const hideValidation = (): void => {
			loginError.value = false;
		};

		return {
			username,
			password,
			checked,
			loginError,
			loginButtonEnabled,
			handleLogin,
			hideValidation
		};
	}
});

</script>

<template>
	<div class="login-page">
		<img
			class="logo"
			src="~@/assets/images/davinci-logo.svg"
			alt="logo"
		>
		<div class="header">
			<div class="header-main-text">
				Welcome to Patient API Emulator
			</div>
			<span class="header-secondary-text">Log into the app to simulate the data import process.</span>
		</div>
		<van-form
			@submit="handleLogin"
		>
			<span class="label">Username</span>
			<van-field
				v-model.trim="username"
				name="Username"
				:class="{ error: loginError }"
				@update:model-value="hideValidation"
			/>
			<span class="label">Password</span>
			<van-field
				v-model.trim="password"
				type="password"
				name="Password"
				:class="{ error: loginError }"
				@update:model-value="hideValidation"
			/>
			<van-checkbox
				v-model="checked"
				shape="square"
			>
				I agree with terms and conditions for using this app.
			</van-checkbox>
			<van-button
				size="large"
				:disabled="!loginButtonEnabled"
				:class="{ 'button-active': loginButtonEnabled }"
				type="primary"
				native-type="submit"
			>
				Login
			</van-button>
		</van-form>
		<van-notify :show="loginError">
			<span class="content">
				We don’t recognize this username or password. Double-check your information and try again.
			</span>
		</van-notify>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.login-page {
	display: flex;
	flex-direction: column;
	position: relative;
	padding: 15px 30px;
	background-color: $mirage;
	height: 100%;
	overflow-y: overlay;

	.logo {
		margin: 50px 0;
		align-self: center;
		width: 130px;
	}

	.header {
		margin-bottom: 40px;
	}

	.header-main-text {
		font-size: $global-xxlarge-font-size;
		font-weight: $global-font-weight-medium;
		margin-bottom: 20px;
		width: 80%;
	}

	.header-secondary-text {
		width: 80%;
		display: block;
		font-weight: $global-font-weight-normal;
		font-size: $global-medium-font-size;
	}

	.van-form {
		.label {
			font-weight: $global-font-weight-normal;
			font-size: $global-large-font-size;
			color: $alto;
		}

		.van-field {
			margin: 5px 0 20px 0;
		}

		.van-checkbox {
			margin: 20px 0 50px 0;
		}
	}
}
</style>
