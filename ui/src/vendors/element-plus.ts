import "@/assets/scss/element-plus/index.scss";

import {
	ElButton,
	ElForm,
	ElFormItem,
	ElInput,
	ElCollapse,
	ElCollapseItem,
	ElLoading,
	ElNotification,
	ElDialog
} from "element-plus";

export default (app: any) => {
	app.use(ElButton);
	app.use(ElForm);
	app.use(ElFormItem);
	app.use(ElInput);
	app.use(ElCollapse);
	app.use(ElCollapseItem);
	app.use(ElLoading);
	app.use(ElNotification);
	app.use(ElDialog);

	app.config.globalProperties.$notify = ElNotification;
};
