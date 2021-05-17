import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import store from "@/store";
import { Call, CallWithExtendedData, SearchOption, Payer } from "@/types";
import { prepareCallsData } from "@/utils/prepareCallsData";
import _ from "@/vendors/lodash";
import { PayersModule } from "@/store/modules/payers";

const SEARCH_OPTIONS_LIST_MAX_LENGTH = 30;

export interface ICalls {
	callList: Call[];
	activeCallId: string | null;
	searchValue: string,
	showMatches: boolean
}

// get search options from localStorage
const getSearchOptions = ():SearchOption[] => {
	const options = localStorage.getItem("autocompleteOptions");

	return options ? JSON.parse(options) : [];
};

@Module({ dynamic: true, store, name: "calls" })
class Calls extends VuexModule implements ICalls {
	lastImportedPayerId: number | null = null;
	callList: Call[] = [];
	activeCallId: string | null = null;
	activeCall: CallWithExtendedData | null = null;
	searchValue: string = "";
	showMatches: boolean = true;
	searchOptions: SearchOption[] = getSearchOptions()

	get lastImportedPayer(): Payer | null {
		return PayersModule.payers.find(p => p.id === this.lastImportedPayerId) || null;
	}

	get fhirList(): Call[] {
		return this.callList.filter((call: Call) => call.requestType === "FHIR");
	}

	get authList(): Call[] {
		return this.callList.filter((call: Call) => call.requestType === "AUTH");
	}

	get preparedList(): CallWithExtendedData[] {
		return prepareCallsData(this.callList, this.searchValue, this.showMatches);
	}

	get preparedFhirList(): CallWithExtendedData[] {
		return this.preparedList.filter((call: Call) => call.requestType === "FHIR");
	}

	get preparedAuthList(): CallWithExtendedData[] {
		return this.preparedList.filter((call: Call) => call.requestType === "AUTH");
	}

	@Mutation
	setLastImportedPayerId(id: number | null) {
		this.lastImportedPayerId = id;
	}

	@Mutation
	updateSearchedValue(payload: string): void {
		this.searchValue = payload;
	}

	@Mutation
	updateShowMatches(payload: boolean): void {
		this.showMatches = payload;
	}

	@Mutation
	addNewCall(payload: Call): void {
		this.callList = [...this.callList, payload];
	}

	@Mutation
	updateCall(payload: Call): void {
		this.callList = this.callList.map(call => call.requestId === payload.requestId ? payload : call);
		this.updateActiveCall();
	}

	@Mutation
	updateActiveCallId(id: string | null): void {
		this.activeCallId = id;
	}

	@Mutation
	updateActiveCall(): void {
		if (this.activeCallId === null) {
			this.activeCall = null;
		} else {
			this.activeCall = CallsModule.preparedList.find((call: Call) => call.requestId === this.activeCallId) || null;
		}
	}

	@Mutation
	clearList(): void {
		this.callList = [];
	}

	@Mutation
	addSearchOption(newOption: string): void {
		if (this.searchOptions.find(item => item.value === newOption)) {
			return;
		}

		if (this.searchOptions.length === SEARCH_OPTIONS_LIST_MAX_LENGTH) {
			this.searchOptions = _.tail(this.searchOptions);
		}

		if (newOption.length > 1) {
			this.searchOptions = [...this.searchOptions, { value: newOption }];
			localStorage.setItem("autocompleteOptions", JSON.stringify(this.searchOptions));
		}
	}

	@Action
	addCall(payload: Call): void {
		const call = this.callList.find(c => c.requestId === payload.requestId);

		call ? this.updateCall(payload) : this.addNewCall(payload);
	}

	@Action
	setActiveCallId(id: string | null): void {
		this.updateActiveCallId(id);
		this.updateActiveCall();
	}

	@Action
	setSearchedValue(payload: string): void {
		this.updateSearchedValue(payload);
		this.updateActiveCall();
	}

	@Action
	setShowMatches(payload: boolean): void {
		this.updateShowMatches(payload);
		this.updateActiveCall();
	}

	@Action
	clearPreviousCallList(): void {
		this.clearList();
		this.setLastImportedPayerId(null);
		this.updateActiveCallId(null);
		this.updateActiveCall();
	}
}
export const CallsModule = getModule(Calls);
