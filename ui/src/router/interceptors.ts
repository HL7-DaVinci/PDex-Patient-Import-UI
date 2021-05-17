import axios from "axios";
import router from "./index";
import { showDefaultErrorNotification } from "@/utils/utils";
import { AuthModule } from "@/store/modules/auth";
import { CallsModule } from "@/store/modules/calls";

axios.interceptors.response.use(undefined, async err => {
	if (err.response.status === 401) {
		await AuthModule.logout();
		await router.push("/login");
		CallsModule.clearList();
	} else {
		showDefaultErrorNotification();
	}

	throw err;
});
