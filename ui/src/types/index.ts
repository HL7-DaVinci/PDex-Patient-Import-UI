export type NewPayerPayload = {
	name: string,
	fhirServerUri: string,
	clientId: string,
	scope: string,
	authorizeUri?: string,
	tokenUri?: string
}

export type AuthLoginPayload = {
	username: string,
	password: string
}

export type AuthLoginResponse = {
	token: string,
	type: string,
	username: string
}

export type AuthVerifyPayload = {
	token: string
}

export type AuthVerifyResponse = {
	active: boolean
}

export enum RequestStatus {
	Pending = "PENDING",
	Completed = "COMPLETED",
	Undefined = "UNDEFINED"
}

export type Call = {
	requestUri: string,
	requestStatus: "PENDING" | "COMPLETED" | "UNDEFINED",
	requestId: string,
	requestMethod: "GET" | "POST",
	requestType: "FHIR" | "AUTH",
	requestBody?: string,
	requestHeaders?: {
		[key: string]: string[]
	},
	requestDuration?: number,
	responseStatus?: number,
	responseStatusInfo?: string,
	responseHeaders?: {
		[key: string]: string[]
	},
	contentType?: string
};

export type SectionField = {
	key: string,
	value: string,
	keyInfo?: string,
	valueIcon?: string,
	searchMatches?: number
}

export type Section = {
	key: string,
	title: string,
	disabled: boolean,
	content: SectionField[],
	searchMatches: number
}

export type CallWithExtendedData = Call & {
	uriWithHighlights: string,
	sections: Section[],
	searchMatches: {
		total: number,
		totalUrl: number,
		totalDetails: number
	}
}

export type ValidatePayerResponse = {
	authorize: string | null;
	token: string | null;
};

export type ImportPayerPayload = {
	payerId: number,
	authCode: string,
	authState: string
}

export type ProgressStatus = {
	id: number,
	type: "IMPORT" | "REFRESH" | "CLEAR" | "DELETE",
	current: number,
	max: number,
	status: "NEW" | "STARTED" | "COMPLETED" | "FAILED",
	errorMessage: null | string
}

export type ImportOverview = {
	resourceType: string,
	createdCount: number,
	updatedCount: number
}

export type Payer = {
	id: number,
	authorizeUri?: string,
	clientId?: string,
	fhirServerUri?: string,
	lastImported?: string | null,
	name?: string,
	scope?: string,
	sourcePatientId?: string,
	tokenUri?: string
}

export type SearchOption = { value: string }
