import {
	removePayerData,
	importPayerData,
	getResourcesOverview,
	getTotalResourcesOverview,
	removeAllData,
	getProgress,
	refreshPayerData,
	getImportOverview
} from "@/api/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { CallsModule } from "@/store/modules/calls";
import { ProgressStatus, Payer as PayerType, ImportOverview } from "@/types";
import { poll, authorizePayer } from "@/utils/utils";
import Mappings from "@/utils/resourceMappings";

export interface IResourceOverview {
	resourceType: string,
	count: number
}

export interface IPayer {
	resourcesOverview: IResourceOverview[],
	totalResourcesOverview: IResourceOverview[],
	progressStatus: ProgressStatus | null,
	importOverview: ImportOverview[] | null
}

@Module({ dynamic: true, store, name: "payer" })
class Payer extends VuexModule implements IPayer {
	resourcesOverview: IResourceOverview[] = [];
	totalResourcesOverview: IResourceOverview[] = [];
	progressStatus: ProgressStatus | null = null;
	importOverview: ImportOverview[] | null = null;
	payerAuthInProgress: boolean = false;

	get supportedResourcesOverview(): IResourceOverview[] {
		return this.resourcesOverview.filter(({ resourceType }) => Boolean(Mappings[resourceType]));
	}

	get totalSupportedResourcesOverview(): IResourceOverview[] {
		return this.totalResourcesOverview.filter(({ resourceType }) => Boolean(Mappings[resourceType]));
	}

	get importProgress(): ProgressStatus | null {
		return this.progressStatus && this.progressStatus.status !== "COMPLETED" ? this.progressStatus : null;
	}

	get isActiveProgress(): boolean | null {
		return this.progressStatus && (this.progressStatus.status === "NEW" || this.progressStatus.status === "STARTED");
	}

	get progressPercentage(): number {
		if (!this.importProgress) {
			return 0;
		}
		const { max, current } = this.importProgress;
		return max === 0 ? 0 : (current / max * 100);
	}

	@Mutation
	setResourcesOverview(data: IResourceOverview[]): void {
		this.resourcesOverview = data;
	}

	@Mutation
	setTotalResourcesOverview(data: IResourceOverview[]): void {
		this.totalResourcesOverview = data;
	}

	@Mutation
	setProgress(data: ProgressStatus | null): void {
		this.progressStatus = data;
	}

	@Mutation
	setImportOverview(data: ImportOverview[]): void {
		this.importOverview = data;
	}

	@Mutation
	setPayerAuthInProgress(payload: boolean): void {
		this.payerAuthInProgress = payload;
	}

	@Action
	async startProgress(id: number): Promise<ProgressStatus> {
		this.setProgress(null);
		return new Promise((resolve, reject) => {
			poll(
				async (): Promise <ProgressStatus> => getProgress(id),
				async (progress: ProgressStatus) => {
					const { type, status } = progress;

					if (!progress) {
						reject();
						return false;
					}

					this.setProgress(progress);

					if (status === "FAILED") {
						reject();
						return false;
					}

					if (status === "NEW" || status === "STARTED") {
						return true;
					}

					if (status === "COMPLETED") {

						resolve(progress);
						return false;
					}

					return false;
				},
				500
			);
		});
	}

	@Action
	async importPayerData(payer: PayerType): Promise<ProgressStatus> {
		CallsModule.clearPreviousCallList();
		CallsModule.setLastImportedPayerId(payer.id);
		this.setPayerAuthInProgress(true);
		const { authCode, authState } = await authorizePayer(payer);
		this.setPayerAuthInProgress(false);
		await importPayerData({ authCode, authState, payerId: payer.id });
		return this.startProgress(payer.id);
	}

	@Action
	async refreshPayerData(payer: PayerType, resourceType?: string): Promise<void> {
		CallsModule.clearPreviousCallList();
		CallsModule.setLastImportedPayerId(payer.id);
		this.setPayerAuthInProgress(true);
		const { authCode, authState } = await authorizePayer(payer);
		this.setPayerAuthInProgress(false);
		await refreshPayerData(payer.id, authCode, authState, resourceType);
		await this.startProgress(payer.id);
	}

	@Action
	async getResourcesOverview(payerId: number): Promise<void> {
		const { data } = await getResourcesOverview(payerId);
		this.setResourcesOverview(data);
	}

	@Action
	async getTotalResourcesOverview(): Promise<void> {
		const { data } = await getTotalResourcesOverview();
		this.setTotalResourcesOverview(data);
	}

	@Action
	async removePayerData(payerId: number): Promise<ProgressStatus> {
		await removePayerData(payerId);
		return this.startProgress(payerId);
	}

	@Action
	async removeAllData(): Promise<ProgressStatus> {
		await removeAllData();
		return this.startProgress(-1);
	}

	@Action
	async getImportOverview(payer: PayerType): Promise<void> {
		if (!payer.lastImported) {
			return;
		}
		this.setImportOverview(await getImportOverview(payer.id, payer.lastImported));
	}
}

export const PayerModule = getModule(Payer);
