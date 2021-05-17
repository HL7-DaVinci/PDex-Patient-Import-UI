import { getAllPayers, addPayer, deletePayer, updatePayer } from "@/api/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Payer, NewPayerPayload } from "@/types";
import { CallsModule } from "@/store/modules/calls";

export interface IPayers {
	payers: Payer[],
	isPayerDeleting: boolean,
	activePayerId: number | null
}

@Module({ dynamic: true, store, name: "payers" })
class Payers extends VuexModule implements IPayers {
	payers: Payer[] = [];
	isPayerDeleting: boolean = false;
	activePayerId: number | null = null;

	get importedPayers(): Payer[] {
		return this.payers.filter((payer: Payer) => payer.lastImported !== null);
	}

	get notImportedPayers(): Payer[] {
		return this.payers.filter((payer: Payer) => payer.lastImported === null);
	}

	get activePayer(): Payer | undefined {
		return this.importedPayers.find((payer: Payer) => payer.id === this.activePayerId);
	}

	@Mutation
	setPayers(data: Payer[]): void {
		this.payers = data;
	}

	@Mutation
	addNewPayer(data: Payer): void {
		this.payers = [...this.payers, data];
	}

	@Mutation
	deleteOldPayer(id: number): void {
		this.payers = this.payers.filter((item: Payer) => item.id !== id);
	}

	@Mutation
	toggleIsPayerDeleting(): void {
		this.isPayerDeleting = !this.isPayerDeleting;
	}

	@Mutation
	changePayer(data: Payer): void {
		this.payers = this.payers.map((payer: Payer) => {
			if (payer.id === data.id) {
				return { ...payer, ...data };
			}

			return payer;
		});
	}

	@Mutation
	setActivePayerId(id: number | null): void {
		this.activePayerId = id;
	}

	@Action
	async loadPayers(): Promise<void> {
		const { data } = await getAllPayers();
		this.setPayers(data);
	}

	@Action
	async addPayer(payload: NewPayerPayload): Promise<void> {
		const { data } = await addPayer(payload);
		this.addNewPayer(data);
	}

	@Action
	async deletePayer(id: number): Promise<void> {
		this.toggleIsPayerDeleting();

		try {
			await deletePayer(id);
			this.deleteOldPayer(id);
			if (id === CallsModule.lastImportedPayerId) {
				CallsModule.clearPreviousCallList();
			}
		} finally {
			this.toggleIsPayerDeleting();
		}
	}

	@Action
	async updatePayer(payload: { id: string, data: NewPayerPayload }): Promise<void> {
		const { data } = await updatePayer(payload);
		this.changePayer(data);
	}

	@Action
	updatePayerLastImported(payload: Payer): void {
		this.changePayer(payload);
	}

	@Action
	updateActivePayerId(id: number | null): void {
		this.setActivePayerId(id);
	}
}

export const PayersModule = getModule(Payers);
