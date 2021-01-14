<script lang="ts">
import LoginForm from "../components/Phone/LoginForm.vue";
import { defineComponent } from "vue";

export default defineComponent({
	name: "Login",
	components: {
		LoginForm
	}
});
</script>

<template>
	<LoginForm />
</template>
