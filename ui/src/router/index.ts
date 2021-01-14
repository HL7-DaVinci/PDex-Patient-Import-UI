import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import Import from "../views/Import.vue";
import Login from "../views/Login.vue";
import store from "../store";
import PayerData from "../components/Phone/Payer/PayerData.vue";
import AllData from "../components/Phone/AllData.vue";
import AllOfSomeResource from "../components/Phone/AllOfSomeResource.vue";
import ResourceData from "../components/Phone/Resource/ResourceData.vue";

const isAuthenticated = (to: any, from: any, next: any) => {
	if (store.getters.isAuthenticated) {
		// if there is some code in path that mean te import data for new payer started
		if(to.query.code) {
			next("/import");
			return;
		}
		next();
		return;
	}
	next("/login");
};

const shouldBeAuthenticated = (to: any, from: any, next: any) => {
	if (!store.getters.isAuthenticated) {
		next();
		return;
	}
	next("/");
};

const routes: Array<RouteRecordRaw> = [
	{
		path: "/",
		name: "Home",
		component: Home,
		beforeEnter: isAuthenticated
	},
	{
		path: "/import",
		name: "Import",
		component: Import
	},
	{
		path: "/login",
		name: "Login",
		component: Login,
		beforeEnter: shouldBeAuthenticated
	},
	{
		path: "/payer/:id",
		name: "Payer",
		component: PayerData,
		props: true,
		beforeEnter: isAuthenticated
	} ,
	{
		path: "/all-data",
		component: AllData,
		beforeEnter: isAuthenticated
	},
	{
		path: "/all-data/:resourceType",
		component: AllOfSomeResource,
		beforeEnter: isAuthenticated,
		props: true
	},
	{
		path: "/payer/:id/:resourceType",
		name: "Resource",
		component: ResourceData,
		props: true,
		beforeEnter: isAuthenticated
	}
];

const router = createRouter({
	history: createWebHistory(process.env.BASE_URL),
	routes
});

// if router include payer id it will set it as active if not then reset activePayerId
router.beforeEach((to, from, next) => {
	const payerId = to.params.id ? +to.params.id : "";
	store.dispatch("setActivePayerId", payerId);
	next();
});

export default router;
