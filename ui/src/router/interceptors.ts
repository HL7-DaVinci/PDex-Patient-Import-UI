import axios from "axios";
import store from "../store";
import router from "./index";
import { showDefaultErrorNotification } from "../utils/utils";

axios.interceptors.response.use(undefined, err => {
	if (err.response.status === 401) {
		store.dispatch("authLogout");
		router.push("/login");
	} else {
		showDefaultErrorNotification();
	}

	throw err;
});
