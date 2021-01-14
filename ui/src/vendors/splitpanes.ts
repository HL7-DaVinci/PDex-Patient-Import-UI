import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

export default (app: any) => {
	app.component("Splitpanes", Splitpanes);
	app.component("Pane", Pane);
};
