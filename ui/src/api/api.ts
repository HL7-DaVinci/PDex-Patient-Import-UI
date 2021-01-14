import axios from "axios";

export const login = (data: any) => axios.post("/api/auth/login", data);

export const getAllServers = () => axios.get("/api/payers");

export const addServer = (data: Object) => axios.post("/api/payers", data);

export const deleteServer = (id: String) => axios.delete(`/api/payers/${id}`);

export const changeServer = (data: any) => axios.put(`/api/payers/${data.id}`, data);

export const getServerToken = (payerId: string, authCode: string) => axios.get(`/api/payers/${payerId}/token`, {
	params: {
		authCode,
		redirectUri: "http://localhost:8080"
	}
});

export const importPayerData = (payerId: any, patientId: any, accessToken: any) => axios.post(`/api/payers/${payerId}/import`, {
	patientId,
	accessToken
});

export const getPatientInfo = (id: string) => axios.get(`/fhir/Patient?_source=${id}`);

// TODO: there could theoretically be more patients than max number of resources fhir returns in a single response (50 when i tested)
export const getAllPatients = () => axios.get("/fhir/Patient?_count=9000");

export const getResourceOverview = (payerId: string) => axios.get(`/api/payers/${payerId}/resources`);

export const getTotalResourceOverview = () => axios.get("/api/payers/resources");

export const removePayerData = (id: string) => axios.post(`/api/payers/${id}/clear`);

export const removeAllData = (payers: any) => payers.reduce((previousPromise, payer) => previousPromise.then(() => removePayerData(payer.id)), Promise.resolve());
