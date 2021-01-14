import axios from "axios";
import store from "../store";
import router from "./index";
import { ElNotification } from "element-plus";

axios.interceptors.response.use(undefined, err => {
	if (err.response.status === 401) {
		store.dispatch("authLogout");
		router.push("/login");
	} else {
		ElNotification({
			title: "Error",
			type: "error",
			message: "Oops! Something went wrong."
		});
	}

	throw err;
});
