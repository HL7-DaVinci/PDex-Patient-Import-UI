import PatientInfo from "./PatientInfo.js";
import { errorMessage } from "../main.js";

export default {
	name: "PayerDetails",
	components: { PatientInfo },
	template: `
		<b-row>
			<patient-info :data="patient.resource || {}"></patient-info>
			<b-col
				v-if="patientReady"
				cols="12"
			>
				<b-tabs
					content-class="mt-3"
					v-model="tabIndex"
					pills
					fill
				>
					<b-tab
						title="Observation"
						name="observation"
						@click="handleResourceChange('observation', 0)"
						:lazy="true"
						:title-link-class="linkClass(0)"
					>
						<el-table
							style="width: 100%"
							:data="observations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="240" label="Observation Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.effectiveDateTime" min-width="180" label="Effective Date(s)" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.valueQuantity" min-width="180" label="Value" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.value }} {{ scope.row.resource.valueQuantity && scope.row.resource.valueQuantity.unit }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="220" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.interpretation" min-width="260" label="Interpretation Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.interpretation }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Condition"
						name="condition"
						@click="handleResourceChange('condition', 1)"
						:lazy="true"
						:title-link-class="linkClass(1)"
					>
						<el-table
							style="width: 100%"
							:data="conditions"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="220" label="Condition Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.onsetDateTime" min-width="180" label="Onset" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.onsetDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.recordedDate" min-width="220" label="Asserted Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.recordedDate | dateTime}}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="260" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.severity" min-width="240" label="Severity Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.severity && scope.row.resource.severity.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.note" min-width="200" label="Note Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.verificationStatus" min-width="220" label="Verification Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.verificationStatus && scope.row.resource.verificationStatus.coding && scope.row.resource.verificationStatus.coding[0] && scope.row.resource.verificationStatus.coding[0].code }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Encounter"
						name="encounter"
						@click="handleResourceChange('encounter', 2)"
						:lazy="true"
						:title-link-class="linkClass(2)"
					>
						<el-table
							style="width: 100%"
							:data="encounters"
							:stripe="true"
							v-loading="loading"
							@row-click="handleEncounterRowClick"
							class="encounters-table"
						>
							<el-table-column prop="resource.class" min-width="180" label="Class Code" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.class && scope.row.resource.class.code }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period.start" min-width="220" label="Period Start DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.start | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period.end" min-width="220" label="Period End DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.end | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.reasonCode" min-width="220" label="Reason Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Medication Request"
						name="medicationRequest"
						@click="handleResourceChange('medicationRequest', 3)"
						:lazy="true"
						:title-link-class="linkClass(3)"
					>
						<el-table
							style="width: 100%"
							:data="medicationRequests"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.medicationCodeableConcept" min-width="240" label="Medication Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.medicationCodeableConcept && scope.row.resource.medicationCodeableConcept.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.authoredOn" min-width="180" label="Authored On" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.authoredOn | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.reasonCode" min-width="220" label="Reason Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode && scope.row.resource.reasonCode[0] && scope.row.resource.reasonCode[0].coding[0] && scope.row.resource.reasonCode[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.note" min-width="200" label="Note Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Medication Dispense"
						name="medicationDispense"
						@click="handleResourceChange('medicationDispense', 4)"
						:lazy="true"
						:title-link-class="linkClass(4)"
					>
						<el-table
							style="width: 100%"
							:data="medicationDispenses"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.medicationCodeableConcept" min-width="240" label="Medication Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.medicationCodeableConcept && scope.row.resource.medicationCodeableConcept.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Medication Statement"
						name="medicationStatement"
						@click="handleResourceChange('medicationStatement', 5)"
						:lazy="true"
						:title-link-class="linkClass(5)"
					>
						<el-table
							style="width: 100%"
							:data="medicationStatements"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.medicationCodeableConcept" min-width="240" label="Medication Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.medicationCodeableConcept && scope.row.resource.medicationCodeableConcept.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.effectiveDateTime" min-width="180" label="Effective" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.taken" min-width="180" label="Taken" sortable>
								<template slot-scope="scope">
								{{ scope.row.resource.taken }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Allergy Intolerance"
						name="allergyIntolerance"
						@click="handleResourceChange('allergyIntolerance', 6)"
						:lazy="true"
						:title-link-class="linkClass(6)"
					>
						<el-table
							style="width: 100%"
							:data="allergyIntolerances"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="260" label="Allergy/Intolerance Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.note" min-width="240" label="Note Time DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.onsetDateTime" min-width="180" label="Asserted Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.onsetDateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.criticality" min-width="180" label="Criticality" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.criticality }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="180" label="Category" sortable>
								<template slot-scope="scope">
								{{ scope.row.resource.category && scope.row.resource.category[0] }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.clinicalStatus" min-width="200" label="Clinical Status" sortable>
								<template slot-scope="scope">
								{{ scope.row.resource.clinicalStatus && scope.row.resource.clinicalStatus.coding && scope.row.resource.clinicalStatus.coding[0] && scope.row.resource.clinicalStatus.coding[0].code }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Procedure"
						name="procedure"
						@click="handleResourceChange('procedure', 7)"
						:lazy="true"
						:title-link-class="linkClass(7)"
					>
						<el-table
							style="width: 100%"
							:data="procedures"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="260" label="Procedure Code Display" sortable>
								<template slot-scope="scope">
								{{ scope.row.resource.code && scope.row.resource.code.coding && scope.row.resource.code.coding[0] && scope.row.resource.code.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category.coding && scope.row.resource.category.coding[0] && scope.row.resource.category.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.reasonCode" min-width="180" label="Reason Code Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.reasonCode && scope.row.resource.reasonCode[0] && scope.row.resource.reasonCode[0].coding && scope.row.resource.reasonCode[0].coding[0] && scope.row.resource.reasonCode[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.notes" min-width="180" label="Notes Time DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.notes && scope.row.resource.notes[0] && scope.row.resource.notes[0].time }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Diagnostic Report"
						name="diagnosticReport"
						@click="handleResourceChange('diagnosticReport', 8)"
						:lazy="true"
						:title-link-class="linkClass(8)"
					>
						<el-table
							style="width: 100%"
							:data="diagnosticReports"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.code" min-width="260" label="Diagnostic Report Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.code && scope.row.resource.code.coding && scope.row.resource.code.coding[0] && scope.row.resource.code.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category.coding && scope.row.resource.category.coding[0] && scope.row.resource.category.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.effectiveDateTime" min-width="180" label="Effective Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.effectiveDateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.diagnosis" min-width="180" label="Diagnosis Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.diagnosis && scope.row.resource.diagnosis.coding && scope.row.resource.diagnosis.coding[0] && scope.row.resource.diagnosis.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Immunization"
						name="immunization"
						@click="handleResourceChange('immunization', 9)"
						:lazy="true"
						:title-link-class="linkClass(9)"
					>
						<el-table
							style="width: 100%"
							:data="immunizations"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.vaccineCode" min-width="240" label="Vaccine Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.vaccineCode && scope.row.resource.vaccineCode.text }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.occurrenceDateTime" min-width="180" label="Date Time" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.occurrenceDateTime | dateTime }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="CarePlan"
						name="carePlan"
						@click="handleResourceChange('CarePlan', 10)"
						:lazy="true"
						:title-link-class="linkClass(10)"
					>
						<el-table
							style="width: 100%"
							:data="carePlans"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].coding && scope.row.resource.category[0].coding[0] && scope.row.resource.category[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period" min-width="220" label="Period Start DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.start }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period" min-width="220" label="Period End DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.end }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.description" min-width="220" label="Desc" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.description }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Care"
						name="careTeam"
						@click="handleResourceChange('CareTeam', 11)"
						:lazy="true"
						:title-link-class="linkClass(11)"
					>
						<el-table
							style="width: 100%"
							:data="careTeams"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.name" min-width="220" label="Name" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.name }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].coding && scope.row.resource.category[0].coding[0] && scope.row.resource.category[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period" min-width="220" label="Period Start DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.start }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.period" min-width="220" label="Period End DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.period && scope.row.resource.period.end }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Goal"
						name="goal"
						@click="handleResourceChange('goal', 12)"
						:lazy="true"
						:title-link-class="linkClass(12)"
					>
						<el-table
							style="width: 100%"
							:data="goals"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].coding && scope.row.resource.category[0].coding[0] && scope.row.resource.category[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="220" label="Status Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.description" min-width="240" label="Description Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.description }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.start" min-width="180" label="Start" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.start }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.target" min-width="240" label="Target Due Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.target }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.note" min-width="240" label="Note Time DateTime" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.note && scope.row.resource.note[0] }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Detected Issue"
						name="detectedIssue"
						@click="handleResourceChange('detectedIssue', 13)"
						:lazy="true"
						:title-link-class="linkClass(13)"
					>
						<el-table
							style="width: 100%"
							:data="detectedIssues"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.status" min-width="220" label="Status Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
								<el-table-column prop="resource.status" min-width="220" label="Status" sortable>
									<template slot-scope="scope">
										{{ scope.row.resource.status }}
									</template>
								</el-table-column>
							<el-table-column prop="resource.severity" min-width="180" label="Severity" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.severity }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.detail" min-width="180" label="Details" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.detail }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.category" min-width="240" label="Category Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.category && scope.row.resource.category[0] && scope.row.resource.category[0].coding && scope.row.resource.category[0].coding[0] && scope.row.resource.category[0].coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.mitigation" min-width="240" label="Mitigation Action Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.mitigation && scope.row.resource.mitigation[0] && scope.row.resource.mitigation[0].action && scope.row.resource.mitigation[0].action.coding && scope.row.resource.mitigation[0].action.coding[0] && scope.row.resource.mitigation[0].action.coding[0].display }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Family Member History"
						name="familyMemberHistory"
						@click="handleResourceChange('familyMemberHistory', 14)"
						:lazy="true"
						:title-link-class="linkClass(14)"
					>
						<el-table
							style="width: 100%"
							:data="familyMemberHistories"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.date" min-width="180" label="Date" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.date }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.name" min-width="180" label="Name" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.name }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.relationship" min-width="180" label="Relationship Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.relationship && scope.row.resource.relationship.coding && scope.row.resource.relationship.coding[0] && scope.row.resource.relationship.coding[0].display }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>

					<b-tab
						title="Document Reference"
						name="documentReference"
						@click="handleResourceChange('documentReference', 15)"
						:lazy="true"
						:title-link-class="linkClass(15)"
					>
						<el-table
							style="width: 100%"
							:data="documentReferences"
							:stripe="true"
							v-loading="loading"
						>
							<el-table-column prop="resource.type" min-width="240" label="Type Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.type && scope.row.resource.type.coding && scope.row.resource.type.coding[0] && scope.row.resource.type.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.class" min-width="240" label="Class Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.class && scope.row.resource.class.coding && scope.row.resource.class.coding[0] && scope.row.resource.class.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.status" min-width="180" label="Status" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.status }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.documentStatus" min-width="240" label="DocStatus Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.documentStatus && scope.row.resource.documentStatus.coding && scope.row.resource.documentStatus.coding[0] && scope.row.resource.documentStatus.coding[0].display }}
								</template>
							</el-table-column>
							<el-table-column prop="resource.securityLabel" min-width="240" label="Security Label Code Display" sortable>
								<template slot-scope="scope">
									{{ scope.row.resource.securityLabel && scope.row.resource.securityLabel[0] && scope.row.resource.securityLabel[0].coding && scope.row.resource.securityLabel[0].coding[0] && scope.row.resource.securityLabel[0].coding[0].display }}
								</template>
							</el-table-column>
						</el-table>
					</b-tab>
				</b-tabs>
			</b-col>
		</b-row>
	`,
	props: ["id"], // passed from router
	data() {
		return {
			loading: false,
			patient: {},
			encounters: [],
			observations: [],
			conditions: [],
			medicationRequests: [],
			medicationDispenses: [],
			medicationStatements: [],
			immunizations: [],
			allergyIntolerances: [],
			procedures: [],
			diagnosticReports: [],
			carePlans: [],
			careTeams: [],
			goals: [],
			detectedIssues: [],
			familyMemberHistories: [],
			documentReferences: [],
			tabIndex: 0
		};
	},
	mounted() {
		this.loadPatient().then(this.loadObservation);
		this.handleResourceChange('observation', 0);
	},
	computed: {
		patientReady() {
			return Object.keys(this.patient).length > 0;
		}
	},
	methods: {
		linkClass(idx) {
			if (this.tabIndex === idx) {
				return ['bg-primary', 'text-light']
			} else {
				return ['bg-light', 'text-info']
			}
		},
		loadPatient() {
			this.loading = true;
			return axios.get("/fhir/Patient", { params: { identifier: `${this.id}|` } })
				.then(({ data }) => this.patient = data.entry[0])
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadEncounter() {
			this.loading = true;
			axios.get("/fhir/Encounter", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.encounters = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadObservation() {
			this.loading = true;
			axios.get("/fhir/Observation", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.observations = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadCondition() {
			this.loading = true;
			axios.get("/fhir/Condition", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.conditions = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadMedicationRequest() {
			this.loading = true;
			axios.get("/fhir/MedicationRequest", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.medicationRequests = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadMedicationDispense() {
			this.loading = true;
			axios.get("/fhir/MedicationDispense", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.medicationDispenses = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadMedicationStatement() {
			this.loading = true;
			axios.get("/fhir/MedicationStatement", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.medicationStatements = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadImmunization() {
			this.loading = true;
			axios.get("/fhir/Immunization", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.immunizations = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadAllergyIntolerance() {
			this.loading = true;
			axios.get("/fhir/AllergyIntolerance", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.allergyIntolerances = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadProcedure() {
			this.loading = true;
			axios.get("/fhir/Procedure", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.procedures = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadDiagnosticReport() {
			this.loading = true;
			axios.get("/fhir/DiagnosticReport", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.diagnosticReports = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadCarePlan() {
			this.loading = true;
			axios.get("/fhir/CarePlan", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.carePlans = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadCareTeam() {
			this.loading = true;
			axios.get("/fhir/CareTeam", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.careTeams = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadGoal() {
			this.loading = true;
			axios.get("/fhir/Goal", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.goals = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadDetectedIssue() {
			this.loading = true;
			axios.get("/fhir/DetectedIssue", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.detectedIssues = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadFamilyMemberHistory() {
			this.loading = true;
			axios.get("/fhir/FamilyMemberHistory", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.familyMemberHistories = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		loadDocumentReference() {
			this.loading = true;
			axios.get("/fhir/DocumentReference", { params: { patient: this.patient.resource.id, _count: 100 } })
				.then(({ data }) => this.documentReferences = data.entry)
				.catch(error => this.$message.error(errorMessage(error.response)))
				.finally(() => this.loading = false);
		},
		handleResourceChange( name, tabId ) {
			const API_MAP = {
				encounter: this.loadEncounter,
				observation: this.loadObservation,
				condition: this.loadCondition,
				medicationRequest: this.loadMedicationRequest,
				medicationDispense: this.loadMedicationDispense,
				medicationStatement: this.loadMedicationStatement,
				immunization: this.loadImmunization,
				allergyIntolerance: this.loadAllergyIntolerance,
				procedure: this.loadProcedure,
				diagnosticReport: this.loadDiagnosticReport,
				carePlan: this.loadCarePlan,
				careTeam: this.loadCareTeam,
				goal: this.loadGoal,
				detectedIssue: this.loadDetectedIssue,
				familyMemberHistory: this.loadfamilyMemberHistory,
				documentReference: this.loaddocumentReference,
			};

			this.tabIndex = tabId

			API_MAP[name]();
		},
		goBack() {
			this.$router.push({ path: "/" });
		},
		handleEncounterRowClick(row) {
			this.$router.push({ path: `/encounter/details/${encodeURIComponent(row.resource.id)}` });
		}
	}
};
