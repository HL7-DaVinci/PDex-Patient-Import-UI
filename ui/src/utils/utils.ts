import { ElNotification } from "element-plus";

export const showDefaultErrorNotification = (): void => {
	ElNotification({
		title: "Error",
		type: "error",
		message: "Oops! Something went wrong."
	});
};
