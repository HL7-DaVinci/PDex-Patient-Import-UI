import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import Login from "../views/Login.vue";
import PayerData from "../components/Phone/Payer/PayerData.vue";
import AllData from "../components/Phone/AllData.vue";
import AllOfSomeResource from "../components/Phone/AllOfSomeResource.vue";
import ResourceData from "../components/Phone/Resource/ResourceData.vue";
import { AuthModule } from "@/store/modules/auth";
import { PayersModule } from "@/store/modules/payers";
import Import from "@/components/Phone/Import/Import.vue";
import AuthRedirectPage from "../components/Phone/AuthRedirectPage.vue";

const isAuthenticated = (to: any, from: any, next: any) => {
	if (AuthModule.isAuthenticated) {
		next();
		return;
	}
	next("/login");
};

const shouldBeAuthenticated = (to: any, from: any, next: any) => {
	if (!AuthModule.isAuthenticated) {
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
		beforeEnter: isAuthenticated,
		meta: { depth: 1 }
	},
	{
		path: "/import",
		name: "Import",
		component: Import,
		meta: { depth: 2 }
	},
	{
		path: "/auth-redirect",
		name: "AuthRedirect",
		component: AuthRedirectPage
	},
	{
		path: "/login",
		name: "Login",
		component: Login,
		beforeEnter: shouldBeAuthenticated,
		meta: { depth: 0 }
	},
	{
		path: "/payer/:id",
		name: "Payer",
		component: PayerData,
		props: true,
		beforeEnter: isAuthenticated,
		meta: { depth: 3 }
	} ,
	{
		path: "/all-data",
		component: AllData,
		beforeEnter: isAuthenticated,
		meta: { depth: 2 }
	},
	{
		path: "/all-data/:resourceType",
		component: AllOfSomeResource,
		beforeEnter: isAuthenticated,
		props: true,
		meta: { depth: 3 }
	},
	{
		path: "/payer/:id/:resourceType",
		name: "Resource",
		component: ResourceData,
		props: true,
		beforeEnter: isAuthenticated,
		meta: { transition: "slide", depth: 4 }
	}
];

const router = createRouter({
	history: createWebHistory(process.env.BASE_URL),
	routes
});

// if router include payer id it will set it as active if not then reset activePayerId
router.beforeEach((to, from, next) => {
	const payerId = to.params.id ? +to.params.id : null;
	PayersModule.updateActivePayerId(payerId);

	next();
});

export default router;
