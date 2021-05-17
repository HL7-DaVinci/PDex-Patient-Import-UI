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
	ElDialog,
	ElIcon,
	ElTabs,
	ElTabPane,
	ElTooltip,
	ElRadioGroup,
	ElRadioButton,
	ElSwitch,
	ElAutocomplete,
	ElPopper,
	ElPopover
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
	app.use(ElIcon);
	app.use(ElTabs);
	app.use(ElTabPane);
	app.use(ElTooltip);
	app.use(ElRadioGroup);
	app.use(ElRadioButton);
	app.use(ElSwitch);
	app.use(ElAutocomplete);
	app.use(ElPopper);
	app.use(ElPopover);

	app.config.globalProperties.$notify = ElNotification;
};
