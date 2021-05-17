import Maybe from "./Maybe.js";
import _ from "@/vendors/lodash";

const Mapping = ({ fields, sortParams={} }) => ({
	fields,
	sortParams,
	convert: fhir =>
		_.mapValues(fields, field => ({
			label: field.label,
			value: field.extract(fhir)
		}))
});


export default {
	Patient: Mapping({
		fields: {
			name: {
				label: "Patient name",
				extract: fhir => Maybe(fhir.name).then(x => x[0]).then(showHumanName).value
			},
			id: {
				label: "Patient ID",
				extract: fhir => fhir.id.replace(/^\d*-/, "")  // trim payer id that we prepend to patient id at import time
			},
			dob: {
				label: "DOB",
				extract: fhir => fhir.birthDate
			},
			address: {
				label: "Address",
				extract: fhir => Maybe(fhir.address).then(x => x[0]).then(showAddress).value
			},
			phone: {
				label: "Phone",
				extract: fhir =>
					Maybe(fhir.telecom)
						.then(telecoms => telecoms.find(com => com.system === "phone"))
						.then(phone => phone.value)
						.value
			}
		},
		sortParams: {}
	}),


	Observation: Mapping({
		fields: {
			code: {
				label: "Observation Code Display",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			effective: {
				label: "Effective Date",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.effectiveDateTime),
						Maybe(fhir.effectivePeriod).then(showPeriod)
					).value
			},
			value: {
				label: "Value",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.valueQuantity).then(showQuantity),
						Maybe(fhir.valueCodeableConcept).then(showCodeableConcept),
						Maybe(fhir.valueString),
						Maybe(fhir.valueBoolean),
						Maybe(fhir.valueInteger),
						Maybe(fhir.valueRange).then(showRange),
						Maybe(fhir.valueRatio).then(showRatio),
						// maybe(fhir.valueSampleData).then(),
						Maybe(fhir.valueTime),
						Maybe(fhir.valueDateTime),
						Maybe(fhir.valuePeriod).then(showPeriod)
					).value
			},
			category: {
				label: "Category Code Display",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).then(showCodeableConcept).value
			},
			interpretation: {
				label: "Interpretation Code Display",
				extract: fhir => Maybe(fhir.interpretation).then(xs => xs[0]).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Code": "code",
			"Effective Date": "date",
			"Value": "value-concept,value-date,value-quantity,value-string",
			"Category": "category"
		}
	}),


	Condition: Mapping({
		fields: {
			code: {
				label: "Code",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			onset: {
				label: "Onset",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.onsetDateTime),
						Maybe(fhir.onsetAge).then(showQuantity),
						Maybe(fhir.onsetPeriod).then(showPeriod),
						Maybe(fhir.onsetRange).then(showRange),
						Maybe(fhir.onsetString)
					).value
			},
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(x => x[0]).then(showCodeableConcept).value
			},
			severity: {
				label: "Severity",
				extract: fhir => Maybe(fhir.severity).then(showCodeableConcept).value
			},
			note: {
				label: "Note time",
				extract: fhir => Maybe(fhir.note).then(x => x[0]).then(note => note.time).value
			},
			verificationStatus: {
				label: "Verification status",
				extract: fhir => Maybe(fhir.verificationStatus).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Code": "code",
			"Onset": "onset-age,onset-date,onset-info",
			"Category": "category",
			"Severity": "severity",
			"Verification status": "verification-status"
		}
	}),


	Encounter: Mapping({
		fields: {
			"class": {
				label: "Class",
				extract: fhir => Maybe(fhir.class).then(showCoding).value
			},
			period: {
				label: "Period",
				extract: fhir => Maybe(fhir.period).then(showPeriod).value
			},
			reasonCode: {
				label: "Reason Code",
				extract: fhir => Maybe(fhir.reasonCode).then(x => x[0]).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Class": "class",
			"Period": "date",
			"Reason code": "reason-code",
			"Status": "status"
		}
	}),


	MedicationRequest: Mapping({
		fields: {
			medication: {
				label: "Medication",
				extract: fhir => Maybe(fhir.medicationCodeableConcept).then(showCodeableConcept).value
			},
			authoredOn: {
				label: "Authored on",
				extract: fhir => fhir.authoredOn
			},
			reasonCode: {
				label: "Reason Code",
				extract: fhir => Maybe(fhir.reasonCode).then(x => x[0]).then(showCodeableConcept).value
			},
			note: {
				label: "Note time",
				extract: fhir => Maybe(fhir.note).then(x => x[0]).then(note => note.time).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Medication": "code",
			"Authored on": "authoredon",
			"Status": "status"
		}
	}),


	MedicationDispense: Mapping({
		fields: {
			medication: {
				label: "Medication",
				extract: fhir => Maybe(fhir.medicationCodeableConcept).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Medication": "code",
			"Status": "status"
		}
	}),


	MedicationStatement: Mapping({
		fields: {
			medication: {
				label: "Medication",
				extract: fhir => Maybe(fhir.medicationCodeableConcept).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Medication": "code",
			"Status": "status"
		}
	}),


	AllergyIntolerance: Mapping({
		fields: {
			code: {
				label: "Code",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			note: {
				label: "Note time",
				extract: fhir => Maybe(fhir.note).then(x => x[0]).then(note => note.time).value
			},
			onset: {
				label: "Onset",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.onsetDateTime),
						Maybe(fhir.onsetAge).then(showQuantity),
						Maybe(fhir.onsetPeriod).then(showPeriod),
						Maybe(fhir.onsetRange).then(showRange),
						Maybe(fhir.onsetString)
					).value
			},
			criticality: {
				label: "Criticality",
				extract: fhir => fhir.criticality
			},
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).value
			},
			clinicalStatus: {
				label: "Clinical status",
				extract: fhir => Maybe(fhir.clinicalStatus).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Code": "code", // this param considers .reaction.substance in absence of code
			"Criticality": "criticality",
			"Category": "category",
			"Clinical status": "clinical-status"
		}
	}),


	Procedure: Mapping({
		fields: {
			code: {
				label: "Code",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(showCodeableConcept).value
			},
			reasonCode: {
				label: "Reason code",
				extract: fhir => Maybe(fhir.reasonCode).then(xs => xs[0]).then(showCodeableConcept).value
			},
			note: {
				label: "Note time",
				extract: fhir => Maybe(fhir.note).then(x => x[0]).then(note => note.time).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Code": "code",
			"Category": "category",
			"Reason code": "reason-code",
			"Status": "status"
		}
	}),


	DiagnosticReport: Mapping({
		fields: {
			code: {
				label: "Code",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).then(showCodeableConcept).value
			},
			effective: {
				label: "Effective Date",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.effectiveDateTime),
						Maybe(fhir.effectivePeriod).then(showPeriod)
					).value
			},
			conclusionCode: {
				label: "Conclusion code",
				extract: fhir => Maybe(fhir.conclusionCode).then(xs => xs[0]).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Code": "code",
			"Category": "category",
			"Effective date": "date",
			"Conclusion code": "conclusion",
			"Status": "status"
		}
	}),


	Immunization: Mapping({
		fields: {
			vaccineCode: {
				label: "Vaccine code",
				extract: fhir => Maybe(fhir.vaccineCode).then(showCodeableConcept).value
			},
			occurrence: {
				label: "Occurrence",
				extract: fhir =>
					Maybe.first(
						fhir.occurrenceDateTime,
						fhir.occurrenceString
					).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			}
		},
		sortParams: {
			"Vaccine code": "vaccine-code",
			"Occurence": "date",
			"Status": "status"
		}
	}),


	CarePlan: Mapping({
		fields: {
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).then(showCodeableConcept).value
			},
			period: {
				label: "Period",
				extract: fhir => Maybe(fhir.period).then(showPeriod).value
			},
			description: {
				label: "Description",
				extract: fhir => fhir.description
			}
		},
		sortParams: {
			"Category": "category",
			"Period": "date"
		}
	}),


	CareTeam: Mapping({
		fields: {
			name: {
				label: "Name",
				extract: fhir => fhir.name
			},
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).then(showCodeableConcept).value
			},
			period: {
				label: "Period",
				extract: fhir => Maybe(fhir.period).then(showPeriod).value
			}
		},
		sortParams: {
			"Category": "category",
			"Period": "date"
		}
	}),


	Goal: Mapping({
		fields: {
			category: {
				label: "Category",
				extract: fhir => Maybe(fhir.category).then(xs => xs[0]).then(showCodeableConcept).value
			},
			statusDate: {
				label: "Status date",
				extract: fhir => fhir.statusDate
			},
			description: {
				label: "Description",
				extract: fhir => Maybe(fhir.description).then(showCodeableConcept).value
			},
			start: {
				label: "Start",
				extract: fhir =>
					Maybe.first(
						Maybe(fhir.startDate),
						Maybe(fhir.startCodeableConcept).then(showCodeableConcept)
					).value
			},
			targetDue: {
				label: "Target due",
				extract: fhir =>
					Maybe(fhir.target).then(x => x[0]).then(target =>
						Maybe.first(
							Maybe(target.dueDate),
							Maybe(target.dueDuration).then(showQuantity)
						)
					).value
			},
			note: {
				label: "Note time",
				extract: fhir => Maybe(fhir.note).then(x => x[0]).then(note => note.time).value
			}
		},
		sortParams: {
			"Category": "category",
			"Start": "start-date",
			"Target due": "target-date"
		}
	}),


	DetectedIssue: Mapping({
		fields: {
			code: {
				label: "Code",
				extract: fhir => Maybe(fhir.code).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			},
			severity: {
				label: "Severity",
				extract: fhir => fhir.severity
			},
			detail: {
				label: "Detail",
				extract: fhir => fhir.detail
			},
			mitigationAction: {
				label: "Mitigation Action",
				extract: fhir => Maybe(fhir.mitigation).then(x => x[0]).then(x => x.action).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Code": "code"
		}
	}),


	FamilyMemberHistory: Mapping({
		fields: {
			date: {
				label: "Date",
				extract: fhir => fhir.date
			},
			name: {
				label: "Name",
				extract: fhir => fhir.name
			},
			relationship: {
				label: "Relationship",
				extract: fhir => Maybe(fhir.relationship).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Date": "date",
			"Relationship": "relationship"
		}
	}),


	DocumentReference: Mapping({
		fields: {
			type: {
				label: "Type",
				extract: fhir => Maybe(fhir.type).then(showCodeableConcept).value
			},
			status: {
				label: "Status",
				extract: fhir => fhir.status
			},
			docStatus: {
				label: "Doc status",
				extract: fhir => fhir.docStatus
			},
			securityLabel: {
				label: "Security label",
				extract: fhir => Maybe(fhir.securityLabel).then(x => x[0]).then(showCodeableConcept).value
			}
		},
		sortParams: {
			"Type": "type",
			"Status": "status",
			"Security label": "security-label"
		}
	})
};




const showCodeableConcept = fhir => {
	if (fhir.text) {
		return fhir.text;
	}
	const coding = fhir.coding ? fhir.coding : [];
	const selected = coding.filter(code => code.userSelected);
	const notSelected = coding.filter(code => !code.userSelected);
	const priority = [...selected, ...notSelected];
	const withDisplay = priority.find(code => code.display);
	if (withDisplay) {
		return withDisplay.display;
	}
	return priority[0].code;
};


const showCoding = fhir => fhir.display ? fhir.display : fhir.code;


const showPeriod = fhir =>
	[
		Maybe(fhir.start).then(start => `Start: ${start}`).value,
		Maybe(fhir.end).then(end => `End: ${end}`).value
	]
		.filter(defined)
		.join(", ");


const showRatio = fhir =>
	Maybe(
		Maybe(fhir.numerator).then(showQuantity),
		Maybe(fhir.denominator).then(showQuantity)
	).then(
		(n, d) => `${n}/${d}`
	).value;


const showQuantity = fhir => [fhir.comparator, fhir.value, fhir.unit].filter(defined).join(" ");


const showRange = fhir =>
	Maybe(
		Maybe(fhir.low).then(showQuantity),
		Maybe(fhir.high).then(showQuantity)
	).then(
		(low, high) => `${low} - ${high}`
	).value;


const showHumanName = fhir => {
	const { text, family, given, prefix, suffix } = fhir;
	if (text) {
		return text;
	}
	const parts = [...(prefix || []), ...(given || []), family, ...(suffix || [])];
	return parts.join(" ");
};


const showAddress = fhir => {
	const { line, postalCode, city, state, country, text } = fhir;
	if (text) {
		return text;
	}
	const parts = [...(line || []), postalCode, city, state, country].filter(Boolean);
	return parts.join(", ");
};


const defined = x => x !== null && x !== undefined;
