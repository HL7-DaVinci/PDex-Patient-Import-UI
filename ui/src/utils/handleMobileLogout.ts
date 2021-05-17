import { computed, ref } from "vue";
import { Call } from "@/types";
import { CallsModule } from "@/store/modules/calls";
import { AuthModule } from "@/store/modules/auth";
import router from "@/router";

export const handleMobileLogout = () => {
	const showDialog = ref<boolean>(false);
	const confirmOptions = {
		primaryText: "If you log out of the phone the list of FHIR and Authorization Requests will be cleared.",
		secondaryText: "",
		title: "Log out",
		primaryButton: "Log out",
		secondaryButton: "Cancel"
	};

	const fhirList = computed<Call[]>(() => CallsModule.fhirList);

	const logout = async () => {
		await AuthModule.logout();
		await router.push("/login");
	};

	const handleLogout = (): void => {
		if (fhirList.value.length) {
			showDialog.value = true;
		} else {
			logout();
		}
	};

	const clearAllRequests = (): void => {
		CallsModule.clearPreviousCallList();
		showDialog.value = false;
		logout();
	};

	return {
		confirmOptions,
		showDialog,
		handleLogout,
		clearAllRequests
	};
};
