package org.hl7.davinci.refimpl.patientui.fhir.importing;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides a list of FHIR resources that can have a reference to Patient together with the search parameters that can
 * be used to search for these resources by Patient ID.
 *
 * @author Taras Vuyiv
 */
@Component
public class PatientReferenceProvider {

  private final FhirContext fhirContext;
  private final List<PatientReference> references;

  @Autowired
  public PatientReferenceProvider(FhirContext fhirContext) {
    this.fhirContext = fhirContext;
    this.references = Collections.unmodifiableList(initialize());
  }

  /**
   * @return all the initialized {@link PatientReference}s
   */
  public List<PatientReference> getAll() {
    return this.references;
  }

  /**
   * Returns a single {@link PatientReference} object for the given resource type.
   *
   * @param resourceName the name of the resource
   * @return a {@link PatientReference}
   *
   * @throws IllegalArgumentException in case no references for the given resource initialized
   */
  public PatientReference getByResource(String resourceName) {
    Class<? extends IBaseResource> resourceType = fhirContext.getResourceDefinition(resourceName)
        .getImplementingClass();
    return references.stream()
        .filter(r -> Objects.equals(r.getResource(), resourceType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("A %s for resource name '%s' does not exist!", PatientReference.class.getSimpleName(),
                resourceName)));
  }

  private List<PatientReference> initialize() {
    // For now we support only some of the resources to shorten the import time:
    return ReferenceBuilder.start()
        .add(AllergyIntolerance.class, AllergyIntolerance.PATIENT, AllergyIntolerance.ASSERTER,
            AllergyIntolerance.RECORDER)
        .add(CarePlan.class, Arrays.asList(CarePlan.PATIENT, CarePlan.SUBJECT), CarePlan.PERFORMER,
            AllergyIntolerance.RECORDER)
        .add(CareTeam.class, Arrays.asList(CareTeam.PATIENT, CareTeam.SUBJECT), CareTeam.PARTICIPANT)
        .add(Condition.class, Arrays.asList(Condition.PATIENT, Condition.SUBJECT), Condition.ASSERTER,
            Condition.EVIDENCE_DETAIL)
        .add(DetectedIssue.class, DetectedIssue.PATIENT, DetectedIssue.IMPLICATED)
        .add(DiagnosticReport.class, Arrays.asList(DiagnosticReport.PATIENT, DiagnosticReport.SUBJECT))
        .add(DocumentReference.class, Arrays.asList(DocumentReference.PATIENT, DocumentReference.SUBJECT),
            DocumentReference.AUTHOR, DocumentReference.RELATED)
        .add(Encounter.class, Arrays.asList(Encounter.PATIENT, Encounter.SUBJECT))
        .add(FamilyMemberHistory.class, FamilyMemberHistory.PATIENT)
        .add(Goal.class, Arrays.asList(Goal.PATIENT, Goal.SUBJECT))
        .add(Immunization.class, Immunization.PATIENT)
        .add(MedicationDispense.class, Arrays.asList(MedicationDispense.PATIENT, MedicationDispense.SUBJECT),
            MedicationDispense.RECEIVER)
        .add(MedicationRequest.class, Arrays.asList(MedicationRequest.PATIENT, MedicationRequest.SUBJECT),
            MedicationRequest.INTENDED_PERFORMER, MedicationRequest.REQUESTER)
        .add(MedicationStatement.class, Arrays.asList(MedicationStatement.PATIENT, MedicationStatement.SUBJECT),
            MedicationStatement.SOURCE)
        .add(Observation.class, Arrays.asList(Observation.PATIENT, Observation.SUBJECT), Observation.PERFORMER,
            Observation.FOCUS)
        .add(Procedure.class, Arrays.asList(Procedure.PATIENT, Procedure.SUBJECT), Procedure.PERFORMER)
        .build();
  }

  /**
   * A simple builder for {@link PatientReference} which helps to organize the initialization code.
   */
  private static class ReferenceBuilder {

    private List<PatientReference> references = new ArrayList<>();

    private static ReferenceBuilder start() {
      return new ReferenceBuilder();
    }

    private ReferenceBuilder add(Class<? extends Resource> resource, ReferenceClientParam... params) {
      return add(resource, Collections.emptyList(), params);
    }

    private ReferenceBuilder add(Class<? extends Resource> resource, List<ReferenceClientParam> siblingParams,
        ReferenceClientParam... params) {
      references.add(new PatientReference(resource, siblingParams, params));
      return this;
    }

    private List<PatientReference> build() {
      return references;
    }
  }
}
