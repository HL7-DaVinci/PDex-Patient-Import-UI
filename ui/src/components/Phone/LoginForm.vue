<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
	name: "LoginForm",
	data() {
		return {
			username: "",
			password: "",
			checked: false,
			loginError : false
		};
	},
	computed: {
		loginButtonEnabled(): boolean {
			return this.checked && this.username !== "" && this.password !== "";
		}
	},
	methods: {
		//
		// Form submit handler
		//
		handleLogin(): void {
			const payload = {
				username: this.username,
				password: this.password
			};

			this.$store.dispatch("authRequest", payload).then(() => this.$router.push("/")).catch((res: any) => {
				if (res.response.status === 401) {
					this.loginError = true;
				}
			});
		},
		hideValidation() {
			this.loginError = false;
		}
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
