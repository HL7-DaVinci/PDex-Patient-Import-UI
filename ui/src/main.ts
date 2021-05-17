import { createApp } from "vue";
import router from "./router";
import store from "./store";
import App from "./App.vue";
import vant from "./vendors/vant";
import element from "./vendors/element-plus";
import "./vendors/normalize";
import "./vendors/lodash";
import splitpanes from "./vendors/splitpanes";
import "./router/interceptors";
import { formatDate } from "./filters/filters";

const app = createApp(App);
app.use(store);
app.use(router);
vant(app);
element(app);
splitpanes(app);
app.config.globalProperties.$filters = {
	formatDate
};
app.mount("#app");
