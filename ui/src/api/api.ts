import axios from "axios";
import { NewPayerPayload,
	AuthLoginPayload,
	AuthVerifyPayload,
	AuthLoginResponse,
	AuthVerifyResponse,
	ValidatePayerResponse,
	ProgressStatus,
	ImportPayerPayload,
	ImportOverview
} from "@/types";

export const login = async (data: AuthLoginPayload): Promise<AuthLoginResponse> => {
	const res = await axios.post<AuthLoginResponse>("/api/auth/login", data);

	return res.data;
};

export const authVerify = async (data: AuthVerifyPayload): Promise<AuthVerifyResponse> => {
	const res = await axios.post<AuthVerifyResponse>("/api/auth/verify", data);

	return res.data;
};

export const getAllPayers = () => axios.get("/api/payers");

export const addPayer = (data: NewPayerPayload) => axios.post("/api/payers", data);

export const deletePayer = (id: number) => axios.delete(`/api/payers/${id}`);

export const updatePayer = (data: { id: string, data: NewPayerPayload }) => axios.put(`/api/payers/${data.id}`, data.data);

export const importPayerData = ({ payerId, authCode, authState }: ImportPayerPayload) => axios.post(`/api/payers/${payerId}/import`, null, {
	params: {
		authCode,
		authState
	}
});

export const refreshPayerData = (payerId: number, authCode: string, authState: string, resourceType?: string) =>
	axios.post<void>(`/api/payers/${payerId}/refresh`, null, {
		params: {
			authCode,
			authState,
			resourceType // BE allows repeat resourceType to refresh multiple resources but we don't use this
		}
	});

export const getPatient = ({ payerId, patientId }) => axios.get(`/fhir/Patient/${payerId}-${patientId}`);

// TODO: there could theoretically be more patients than max number of resources fhir returns in a single response (50 when i tested)
export const getAllPatients = () => axios.get("/fhir/Patient?_count=9000");

export const getResourcesOverview = (payerId: number) => axios.get(`/api/payers/${payerId}/resources`);

export const getTotalResourcesOverview = () => axios.get("/api/payers/resources");

export const removePayerData = (id: number) => axios.post(`/api/payers/${id}/clear`);

export const removeAllData = () => axios.post("/api/payers/clear");

export const validatePayer = async (uri: string): Promise<ValidatePayerResponse> => {
	const res = await axios.get<ValidatePayerResponse>("/api/fhir/oauth-uris", { params: { serverUri: uri } });

	return res.data;
};

export const getResponseBody = async (payerId: number, requestId: string): Promise<string> => {
	const { data } = await axios.get(`/api/payers/${payerId}/import-requests/${requestId}/content`);
	return data.responseBody;
};

export const getProgress = async (id: number): Promise<ProgressStatus> => {
	const res = await axios.get<ProgressStatus>(`/api/progress/${id}`);

	return res.data;
};

export const getAllProgress = async (): Promise<ProgressStatus[]> => {
	const res = await axios.get<ProgressStatus[]>("/api/progress");

	return res.data;
};

export const getImportOverview = async (payerId: number, date: string): Promise<ImportOverview[]> => {
	const { data } = await axios.get(`/api/payers/${payerId}/import-info`, { params: { date } });
	return data;
};
