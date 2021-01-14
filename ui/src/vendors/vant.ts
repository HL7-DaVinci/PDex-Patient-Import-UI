import {
	Button,
	Switch,
	Form,
	Field,
	Checkbox,
	RadioGroup,
	Radio,
	Notify,
	NavBar,
	Dialog,
	Progress,
	Cell,
	CellGroup,
	Popup,
	Collapse,
	CollapseItem,
	CheckboxGroup
} from "vant";

export default (app: any) => {
	app.use(Button);
	app.use(Switch);
	app.use(Form);
	app.use(Field);
	app.use(Checkbox);
	app.use(RadioGroup);
	app.use(Radio);
	app.use(Notify);
	app.use(NavBar);
	app.use(Dialog);
	app.use(Progress);
	app.use(CellGroup);
	app.use(Cell);
	app.use(Collapse);
	app.use(CollapseItem);
	app.use(Popup);
	app.use(CheckboxGroup);
};
