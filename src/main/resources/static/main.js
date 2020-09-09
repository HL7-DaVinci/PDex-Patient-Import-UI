import App from "./components/App.js";

export const errorMessage = res => res && res.data && res.data.message ? `Error: ${res.data.message}` : 'Error happened.';

Vue.filter("dateTime", val => val ? moment(val).format("YYYY-MM-DD HH:ss") : "");
axios.defaults.headers.common["Cache-Control"] = "no-cache"; // to prevent caching of fhir resources
ELEMENT.locale(ELEMENT.lang.en);
Vue.use(VueRouter);
Vue.use(BootstrapVue);

const router = new VueRouter({
	routes: [{
		path: "/",
		component: () => import("./components/PayerList.js")
	},
	{
		path: "/details/:id",
		component: () => import("./components/PayerDetails.js"),
		props: true
	},
	{
		path: "/encounter/details/:id",
		component: () => import("./components/EncounterDetails.js"),
		props: true
	},
	{
		path: "*",
		component: () => import("./components/NotFound.js")
	}]
});

new Vue({
	render: h => h(App),
	router
}).$mount("#app");
