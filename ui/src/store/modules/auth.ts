import { login, authVerify } from "@/api/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import { getToken, removeToken, setToken, setRequestAuthHeader, removeRequestAuthHeader } from "@/utils/utils";
import store from "@/store";
import { AuthLoginPayload, AuthVerifyPayload } from "@/types";

export interface IAuth {
	token: string,
	status: string
}

@Module({ dynamic: true, store, name: "auth" })
class Auth extends VuexModule implements IAuth {
	token: string = getToken() || ""
	status: string = ""

	get isAuthenticated(): boolean {
		return !!this.token;
	}

	@Mutation
	setStatus(status: string): void {
		this.status = status;
	}

	@Mutation
	setToken(token: string): void {
		this.token = token;
	}

	@Action
	async login(payload: AuthLoginPayload): Promise<void> {
		this.setStatus("loading");

		try {
			const data = await login(payload);
			setToken(data.token);
			setRequestAuthHeader(`Bearer ${data.token}`);
			this.setStatus("success");
			this.setToken(data.token);
		} catch (err) {
			this.setStatus("error");
			removeToken();
			throw err;
		}
	}

	@Action
	async logout(): Promise<void> {
		return new Promise(resolve => {
			this.setStatus("");
			this.setToken("");
			removeToken();
			removeRequestAuthHeader();
			resolve();
		});
	}

	@Action
	async authVerify(payload: AuthVerifyPayload): Promise<void> {
		const data = await authVerify(payload);

		if (!data.active) {
			this.setStatus("");
			this.setToken("");
			removeToken();
			removeRequestAuthHeader();
			throw new Error("inactive token");
		}
	}
}

export const AuthModule = getModule(Auth);
