import { Call, CallWithExtendedData, Section, SectionField } from "@/types";
import sanitizeHtml from "sanitize-html";

// count how many times string occurs in another string
const countStringMatches = (str: string, searchValue: string): number => !searchValue ? 0 : str.toLowerCase().split(searchValue).length - 1;

// count matches in request/response section of request details
const countTwoDimensionalArrayMatches = (data: [string, string][], searchValue: string): number => !searchValue ? 0 : data.reduce((result, item) => result + countStringMatches(item[0], searchValue) + countStringMatches(item[1], searchValue), 0);

// eslint-disable-next-line
const makeRegExp = (queryStr: string, caseInsensitive=true) => new RegExp(queryStr.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&"), caseInsensitive ? "gi" : "g")

// replace all occurrences of searched text with the same text but wrapped with a <mark>
export const highlightMatches = (value: string, searchValue: string): string => sanitizeHtml(!searchValue ? value : value.replaceAll(makeRegExp(searchValue), match => `<mark class="highlight-text">${match}</mark>`), {
	allowedTags: ["mark"],
	allowedClasses: {
		"mark": ["highlight-text"]
	}
});

// prepare all data for details section
const prepareDetailsSections = (call: Call, searchValue: string): Section[] =>
	call.requestMethod === "POST" ? [
		prepareGeneralSection(call, searchValue),
		prepareParamsSection(call, searchValue),
		prepareRequestHeadersSection(call, searchValue),
		prepareRequestContentSection(call, searchValue),
		prepareResponseHeadersSection(call, searchValue)
	] : [
		prepareGeneralSection(call, searchValue),
		prepareParamsSection(call, searchValue),
		prepareRequestHeadersSection(call, searchValue),
		prepareResponseHeadersSection(call, searchValue)
	];

const prepareGeneralSection = (call: Call, searchValue: string): Section => {
	const duration = `${call.requestDuration ? call.requestDuration/1000 : 0} sec`;
	const status = `${call.responseStatus} ${call.responseStatusInfo}`;

	const completeRequestFields = (call: Call, searchValue) => [{
		key: "Response status",
		value:  highlightMatches(status, searchValue),
		valueIcon: call.responseStatus && call.responseStatus < 400 ? "success" : "warning",
		searchMatches: countStringMatches(status, searchValue)
	}, {
		key: "Request duration",
		value: highlightMatches(duration, searchValue),
		searchMatches: countStringMatches(duration, searchValue)
	}
	];

	const method: SectionField = {
		key: "Request method",
		value:  highlightMatches(call.requestMethod, searchValue),
		searchMatches: countStringMatches(call.requestMethod, searchValue)
	};

	const fields: SectionField[] = [method].concat(call.requestStatus === "COMPLETED" ? completeRequestFields(call, searchValue) : []);

	return {
		key: "general",
		title: "General",
		disabled: false,
		content: fields,
		searchMatches: fields.reduce((result, field) => result + (field.searchMatches || 0), 0)
	};
};

const prepareParamsSection = (call: Call, searchValue: string): Section => {
	const params: [string, string][] = Array.from(new URL(call.requestUri).searchParams.entries());
	return {
		key: "query-params",
		title: `URL Parameters (${params.length})`,
		disabled: params.length === 0,
		content: params.map(([key, value]) => ({ key: highlightMatches(key, searchValue), value: highlightMatches(value, searchValue) })),
		searchMatches: countTwoDimensionalArrayMatches(params, searchValue)
	};
};

const prepareRequestHeadersSection = (call: Call, searchValue: string): Section => {
	const headers: [string, string][] =
		Object.entries(call.requestHeaders || {})
			.flatMap(([headerName, headerValues]) => headerValues.map(val => [headerName, val] as [string, string]));

	//TODO: change it when header hints functionality will be clear
	const keyInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

	return {
		key: "request-headers",
		title: `Request Headers (${headers.length})`,
		disabled: headers.length === 0,
		content: headers.map(([key, value]) => ({ key: highlightMatches(key, searchValue), value: highlightMatches(value, searchValue), keyInfo, searchMatches: countStringMatches(value, searchValue) })),
		searchMatches: countTwoDimensionalArrayMatches(headers, searchValue)
	};
};

const prepareResponseHeadersSection = (call: Call, searchValue: string): Section => {
	const headers: [string, string][] =
		call.requestStatus === "PENDING" ? [] :
			Object.entries(call.responseHeaders || {})
				.flatMap(([headerName, headerValues]) => headerValues.map(val => [headerName, val] as [string, string]));

	//TODO: change it when header hints functionality will be clear
	const keyInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

	return {
		key: "response-headers",
		title: `Response Headers (${headers.length})`,
		disabled: headers.length === 0,
		content: headers.map(([key, value]) => ({ key: highlightMatches(key, searchValue), value: highlightMatches(value, searchValue), keyInfo, searchMatches: countStringMatches(value, searchValue) })),
		searchMatches: countTwoDimensionalArrayMatches(headers, searchValue)
	};
};

const prepareRequestContentSection = (call: Call, searchValue: string): Section => {
	const bodyPairs: [string, [string]][] = call.requestBody ? Object.entries(JSON.parse(call.requestBody)) : [];
	const data: [string, string][] = bodyPairs.flatMap(([name, values]) => values.map(val => [name, val] as [string, string]));

	return {
		key: "request-content",
		title: `Request Content (${data.length})`,
		disabled: data.length === 0,
		content: data.map(([key, value]) => ({ key: highlightMatches(key, searchValue), value: highlightMatches(value, searchValue) })),
		searchMatches: countTwoDimensionalArrayMatches(data, searchValue)
	};
};


// return list of calls with call data and sections for details and counters of matches
// all calls if no search value or matched calls otherwise
export const prepareCallsData = (calls: Call[], searchValue: string, showMatches: boolean): CallWithExtendedData[] => calls.reduce((result: CallWithExtendedData[], call: Call) => {
	const sections = prepareDetailsSections(call, searchValue);
	const totalURLMatches: number = countStringMatches(call.requestUri, searchValue);
	const totalDetailsMatches: number = sections.reduce((result, sec) => result + (sec.searchMatches || 0), 0);
	const totalQueryMatches: number = sections.find(sec => sec.key === "query-params")?.searchMatches || 0;
	const totalMatches: number = totalURLMatches + totalDetailsMatches - totalQueryMatches;

	// if no searchValue return all calls if searchValue exist return only that calls that have matches
	if (!searchValue || !showMatches || totalMatches > 0) {
		result.push({
			...call,
			uriWithHighlights: highlightMatches(call.requestUri, searchValue),
			sections,
			searchMatches: {
				total: totalMatches,
				totalUrl: totalURLMatches,
				totalDetails: totalDetailsMatches
			}
		});
	}

	return result;
}, []);

// get total matches in all data
export const getTotalMatches = (calls: CallWithExtendedData[]): number => calls.reduce((total: number, call) => total + (call.searchMatches.total || 0), 0);

// get total matches in all url
export const getTotalURLMatches = (calls: CallWithExtendedData[]): number => calls.reduce((total: number, call) => total + (call.searchMatches.totalUrl || 0), 0);
