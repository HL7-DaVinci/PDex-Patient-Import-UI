package org.hl7.davinci.refimpl.patientui.config.fhir;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoCodeSystem;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoComposition;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoConceptMap;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoEncounter;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoMessageHeader;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoObservation;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoPatient;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoSearchParameter;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoStructureDefinition;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoSubscription;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDaoValueSet;
import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.dao.JpaResourceDao;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoCodeSystemR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoCompositionR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoConceptMapR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoEncounterR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoMessageHeaderR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoObservationR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoPatientR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoSearchParameterR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoStructureDefinitionR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoSubscriptionR4;
import ca.uhn.fhir.jpa.dao.r4.FhirResourceDaoValueSetR4;
import org.hl7.davinci.refimpl.patientui.config.fhir.dao.ResourcePreStorageValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Account;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.AdverseEvent;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.AppointmentResponse;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Basic;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.BiologicallyDerivedProduct;
import org.hl7.fhir.r4.model.BodyStructure;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.CatalogEntry;
import org.hl7.fhir.r4.model.ChargeItem;
import org.hl7.fhir.r4.model.ChargeItemDefinition;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.CommunicationRequest;
import org.hl7.fhir.r4.model.CompartmentDefinition;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Contract;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest;
import org.hl7.fhir.r4.model.CoverageEligibilityResponse;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DeviceDefinition;
import org.hl7.fhir.r4.model.DeviceMetric;
import org.hl7.fhir.r4.model.DeviceRequest;
import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentManifest;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.EffectEvidenceSynthesis;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.EnrollmentRequest;
import org.hl7.fhir.r4.model.EnrollmentResponse;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.EventDefinition;
import org.hl7.fhir.r4.model.Evidence;
import org.hl7.fhir.r4.model.EvidenceVariable;
import org.hl7.fhir.r4.model.ExampleScenario;
import org.hl7.fhir.r4.model.ExplanationOfBenefit;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.GraphDefinition;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.GuidanceResponse;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.ImmunizationEvaluation;
import org.hl7.fhir.r4.model.ImmunizationRecommendation;
import org.hl7.fhir.r4.model.ImplementationGuide;
import org.hl7.fhir.r4.model.InsurancePlan;
import org.hl7.fhir.r4.model.Invoice;
import org.hl7.fhir.r4.model.Library;
import org.hl7.fhir.r4.model.Linkage;
import org.hl7.fhir.r4.model.ListResource;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationKnowledge;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.MedicinalProduct;
import org.hl7.fhir.r4.model.MedicinalProductAuthorization;
import org.hl7.fhir.r4.model.MedicinalProductContraindication;
import org.hl7.fhir.r4.model.MedicinalProductIndication;
import org.hl7.fhir.r4.model.MedicinalProductIngredient;
import org.hl7.fhir.r4.model.MedicinalProductInteraction;
import org.hl7.fhir.r4.model.MedicinalProductManufactured;
import org.hl7.fhir.r4.model.MedicinalProductPackaged;
import org.hl7.fhir.r4.model.MedicinalProductPharmaceutical;
import org.hl7.fhir.r4.model.MedicinalProductUndesirableEffect;
import org.hl7.fhir.r4.model.MessageDefinition;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.NamingSystem;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ObservationDefinition;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.OrganizationAffiliation;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PaymentNotice;
import org.hl7.fhir.r4.model.PaymentReconciliation;
import org.hl7.fhir.r4.model.Person;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.RequestGroup;
import org.hl7.fhir.r4.model.ResearchDefinition;
import org.hl7.fhir.r4.model.ResearchElementDefinition;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.hl7.fhir.r4.model.ResearchSubject;
import org.hl7.fhir.r4.model.RiskAssessment;
import org.hl7.fhir.r4.model.RiskEvidenceSynthesis;
import org.hl7.fhir.r4.model.Schedule;
import org.hl7.fhir.r4.model.SearchParameter;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Slot;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.SpecimenDefinition;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r4.model.Subscription;
import org.hl7.fhir.r4.model.Substance;
import org.hl7.fhir.r4.model.SubstanceNucleicAcid;
import org.hl7.fhir.r4.model.SubstancePolymer;
import org.hl7.fhir.r4.model.SubstanceProtein;
import org.hl7.fhir.r4.model.SubstanceReferenceInformation;
import org.hl7.fhir.r4.model.SubstanceSourceMaterial;
import org.hl7.fhir.r4.model.SubstanceSpecification;
import org.hl7.fhir.r4.model.SupplyDelivery;
import org.hl7.fhir.r4.model.SupplyRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.TerminologyCapabilities;
import org.hl7.fhir.r4.model.TestReport;
import org.hl7.fhir.r4.model.TestScript;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.VerificationResult;
import org.hl7.fhir.r4.model.VisionPrescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Provides a customized DAO for every resource of the FHIR server.
 *
 * @author Taras Vuyiv
 */
@Configuration
public class FhirJpaConfig extends BaseJavaConfigR4 {

  private final ResourcePreStorageValidator resourcePreStorageValidator = new ResourcePreStorageValidator();

  @Override
  @Lazy
  @Bean(name = "myAccountDaoR4")
  public IFhirResourceDao<Account> daoAccountR4() {
    return jpaResourceDao(Account.class);
  }

  @Override
  @Lazy
  @Bean(name = "myActivityDefinitionDaoR4")
  public IFhirResourceDao<ActivityDefinition> daoActivityDefinitionR4() {
    return jpaResourceDao(ActivityDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myAdverseEventDaoR4")
  public IFhirResourceDao<AdverseEvent> daoAdverseEventR4() {
    return jpaResourceDao(AdverseEvent.class);
  }

  @Override
  @Lazy
  @Bean(name = "myAllergyIntoleranceDaoR4")
  public IFhirResourceDao<AllergyIntolerance> daoAllergyIntoleranceR4() {
    return jpaResourceDao(AllergyIntolerance.class);
  }

  @Override
  @Lazy
  @Bean(name = "myAppointmentDaoR4")
  public IFhirResourceDao<Appointment> daoAppointmentR4() {
    return jpaResourceDao(Appointment.class);
  }

  @Override
  @Lazy
  @Bean(name = "myAppointmentResponseDaoR4")
  public IFhirResourceDao<AppointmentResponse> daoAppointmentResponseR4() {
    return jpaResourceDao(AppointmentResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myAuditEventDaoR4")
  public IFhirResourceDao<AuditEvent> daoAuditEventR4() {
    return jpaResourceDao(AuditEvent.class);
  }

  @Override
  @Lazy
  @Bean(name = "myBasicDaoR4")
  public IFhirResourceDao<Basic> daoBasicR4() {
    return jpaResourceDao(Basic.class);
  }

  @Override
  @Lazy
  @Bean(name = "myBinaryDaoR4")
  public IFhirResourceDao<Binary> daoBinaryR4() {
    return jpaResourceDao(Binary.class);
  }

  @Override
  @Lazy
  @Bean(name = "myBiologicallyDerivedProductDaoR4")
  public IFhirResourceDao<BiologicallyDerivedProduct> daoBiologicallyDerivedProductR4() {
    return jpaResourceDao(BiologicallyDerivedProduct.class);
  }

  @Override
  @Lazy
  @Bean(name = "myBodyStructureDaoR4")
  public IFhirResourceDao<BodyStructure> daoBodyStructureR4() {
    return jpaResourceDao(BodyStructure.class);
  }

  @Override
  @Lazy
  @Bean(name = "myBundleDaoR4")
  public IFhirResourceDao<Bundle> daoBundleR4() {
    return jpaResourceDao(Bundle.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCapabilityStatementDaoR4")
  public IFhirResourceDao<CapabilityStatement> daoCapabilityStatementR4() {
    return jpaResourceDao(CapabilityStatement.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCarePlanDaoR4")
  public IFhirResourceDao<CarePlan> daoCarePlanR4() {
    return jpaResourceDao(CarePlan.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCareTeamDaoR4")
  public IFhirResourceDao<CareTeam> daoCareTeamR4() {
    return jpaResourceDao(CareTeam.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCatalogEntryDaoR4")
  public IFhirResourceDao<CatalogEntry> daoCatalogEntryR4() {
    return jpaResourceDao(CatalogEntry.class);
  }

  @Override
  @Lazy
  @Bean(name = "myChargeItemDaoR4")
  public IFhirResourceDao<ChargeItem> daoChargeItemR4() {
    return jpaResourceDao(ChargeItem.class);
  }

  @Override
  @Lazy
  @Bean(name = "myChargeItemDefinitionDaoR4")
  public IFhirResourceDao<ChargeItemDefinition> daoChargeItemDefinitionR4() {
    return jpaResourceDao(ChargeItemDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myClaimDaoR4")
  public IFhirResourceDao<Claim> daoClaimR4() {
    return jpaResourceDao(Claim.class);
  }

  @Override
  @Lazy
  @Bean(name = "myClaimResponseDaoR4")
  public IFhirResourceDao<ClaimResponse> daoClaimResponseR4() {
    return jpaResourceDao(ClaimResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myClinicalImpressionDaoR4")
  public IFhirResourceDao<ClinicalImpression> daoClinicalImpressionR4() {
    return jpaResourceDao(ClinicalImpression.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCodeSystemDaoR4")
  public IFhirResourceDaoCodeSystem<CodeSystem, Coding, CodeableConcept> daoCodeSystemR4() {
    FhirResourceDaoCodeSystemR4 dao = new FhirResourceDaoCodeSystemR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((CodeSystem) theResource, this);
      }
    };
    dao.setResourceType(CodeSystem.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myCommunicationDaoR4")
  public IFhirResourceDao<Communication> daoCommunicationR4() {
    return jpaResourceDao(Communication.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCommunicationRequestDaoR4")
  public IFhirResourceDao<CommunicationRequest> daoCommunicationRequestR4() {
    return jpaResourceDao(CommunicationRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCompartmentDefinitionDaoR4")
  public IFhirResourceDao<CompartmentDefinition> daoCompartmentDefinitionR4() {
    return jpaResourceDao(CompartmentDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCompositionDaoR4")
  public IFhirResourceDaoComposition<Composition> daoCompositionR4() {
    FhirResourceDaoCompositionR4 dao = new FhirResourceDaoCompositionR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((Composition) theResource, this);
      }
    };
    dao.setResourceType(Composition.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myConceptMapDaoR4")
  public IFhirResourceDaoConceptMap<ConceptMap> daoConceptMapR4() {
    FhirResourceDaoConceptMapR4 dao = new FhirResourceDaoConceptMapR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((ConceptMap) theResource, this);
      }
    };
    dao.setResourceType(ConceptMap.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myConditionDaoR4")
  public IFhirResourceDao<Condition> daoConditionR4() {
    return jpaResourceDao(Condition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myConsentDaoR4")
  public IFhirResourceDao<Consent> daoConsentR4() {
    return jpaResourceDao(Consent.class);
  }

  @Override
  @Lazy
  @Bean(name = "myContractDaoR4")
  public IFhirResourceDao<Contract> daoContractR4() {
    return jpaResourceDao(Contract.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCoverageDaoR4")
  public IFhirResourceDao<Coverage> daoCoverageR4() {
    return jpaResourceDao(Coverage.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCoverageEligibilityRequestDaoR4")
  public IFhirResourceDao<CoverageEligibilityRequest> daoCoverageEligibilityRequestR4() {
    return jpaResourceDao(CoverageEligibilityRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myCoverageEligibilityResponseDaoR4")
  public IFhirResourceDao<CoverageEligibilityResponse> daoCoverageEligibilityResponseR4() {
    return jpaResourceDao(CoverageEligibilityResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDetectedIssueDaoR4")
  public IFhirResourceDao<DetectedIssue> daoDetectedIssueR4() {
    return jpaResourceDao(DetectedIssue.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDeviceDaoR4")
  public IFhirResourceDao<Device> daoDeviceR4() {
    return jpaResourceDao(Device.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDeviceDefinitionDaoR4")
  public IFhirResourceDao<DeviceDefinition> daoDeviceDefinitionR4() {
    return jpaResourceDao(DeviceDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDeviceMetricDaoR4")
  public IFhirResourceDao<DeviceMetric> daoDeviceMetricR4() {
    return jpaResourceDao(DeviceMetric.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDeviceRequestDaoR4")
  public IFhirResourceDao<DeviceRequest> daoDeviceRequestR4() {
    return jpaResourceDao(DeviceRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDeviceUseStatementDaoR4")
  public IFhirResourceDao<DeviceUseStatement> daoDeviceUseStatementR4() {
    return jpaResourceDao(DeviceUseStatement.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDiagnosticReportDaoR4")
  public IFhirResourceDao<DiagnosticReport> daoDiagnosticReportR4() {
    return jpaResourceDao(DiagnosticReport.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDocumentManifestDaoR4")
  public IFhirResourceDao<DocumentManifest> daoDocumentManifestR4() {
    return jpaResourceDao(DocumentManifest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myDocumentReferenceDaoR4")
  public IFhirResourceDao<DocumentReference> daoDocumentReferenceR4() {
    return jpaResourceDao(DocumentReference.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEffectEvidenceSynthesisDaoR4")
  public IFhirResourceDao<EffectEvidenceSynthesis> daoEffectEvidenceSynthesisR4() {
    return jpaResourceDao(EffectEvidenceSynthesis.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEncounterDaoR4")
  public IFhirResourceDaoEncounter<Encounter> daoEncounterR4() {
    FhirResourceDaoEncounterR4 encounterDao = new FhirResourceDaoEncounterR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((Encounter) theResource, this);
      }
    };
    encounterDao.setResourceType(Encounter.class);
    encounterDao.setContext(fhirContextR4());
    return encounterDao;
  }

  @Override
  @Lazy
  @Bean(name = "myEndpointDaoR4")
  public IFhirResourceDao<Endpoint> daoEndpointR4() {
    return jpaResourceDao(Endpoint.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEnrollmentRequestDaoR4")
  public IFhirResourceDao<EnrollmentRequest> daoEnrollmentRequestR4() {
    return jpaResourceDao(EnrollmentRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEnrollmentResponseDaoR4")
  public IFhirResourceDao<EnrollmentResponse> daoEnrollmentResponseR4() {
    return jpaResourceDao(EnrollmentResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEpisodeOfCareDaoR4")
  public IFhirResourceDao<EpisodeOfCare> daoEpisodeOfCareR4() {
    return jpaResourceDao(EpisodeOfCare.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEventDefinitionDaoR4")
  public IFhirResourceDao<EventDefinition> daoEventDefinitionR4() {
    return jpaResourceDao(EventDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEvidenceDaoR4")
  public IFhirResourceDao<Evidence> daoEvidenceR4() {
    return jpaResourceDao(Evidence.class);
  }

  @Override
  @Lazy
  @Bean(name = "myEvidenceVariableDaoR4")
  public IFhirResourceDao<EvidenceVariable> daoEvidenceVariableR4() {
    return jpaResourceDao(EvidenceVariable.class);
  }

  @Override
  @Lazy
  @Bean(name = "myExampleScenarioDaoR4")
  public IFhirResourceDao<ExampleScenario> daoExampleScenarioR4() {
    return jpaResourceDao(ExampleScenario.class);
  }

  @Override
  @Lazy
  @Bean(name = "myExplanationOfBenefitDaoR4")
  public IFhirResourceDao<ExplanationOfBenefit> daoExplanationOfBenefitR4() {
    return jpaResourceDao(ExplanationOfBenefit.class);
  }

  @Override
  @Lazy
  @Bean(name = "myFamilyMemberHistoryDaoR4")
  public IFhirResourceDao<FamilyMemberHistory> daoFamilyMemberHistoryR4() {
    return jpaResourceDao(FamilyMemberHistory.class);
  }

  @Override
  @Lazy
  @Bean(name = "myFlagDaoR4")
  public IFhirResourceDao<Flag> daoFlagR4() {
    return jpaResourceDao(Flag.class);
  }

  @Override
  @Lazy
  @Bean(name = "myGoalDaoR4")
  public IFhirResourceDao<Goal> daoGoalR4() {
    return jpaResourceDao(Goal.class);
  }

  @Override
  @Lazy
  @Bean(name = "myGraphDefinitionDaoR4")
  public IFhirResourceDao<GraphDefinition> daoGraphDefinitionR4() {
    return jpaResourceDao(GraphDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myGroupDaoR4")
  public IFhirResourceDao<Group> daoGroupR4() {
    return jpaResourceDao(Group.class);
  }

  @Override
  @Lazy
  @Bean(name = "myGuidanceResponseDaoR4")
  public IFhirResourceDao<GuidanceResponse> daoGuidanceResponseR4() {
    return jpaResourceDao(GuidanceResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myHealthcareServiceDaoR4")
  public IFhirResourceDao<HealthcareService> daoHealthcareServiceR4() {
    return jpaResourceDao(HealthcareService.class);
  }

  @Override
  @Lazy
  @Bean(name = "myImagingStudyDaoR4")
  public IFhirResourceDao<ImagingStudy> daoImagingStudyR4() {
    return jpaResourceDao(ImagingStudy.class);
  }

  @Override
  @Lazy
  @Bean(name = "myImmunizationDaoR4")
  public IFhirResourceDao<Immunization> daoImmunizationR4() {
    return jpaResourceDao(Immunization.class);
  }

  @Override
  @Lazy
  @Bean(name = "myImmunizationEvaluationDaoR4")
  public IFhirResourceDao<ImmunizationEvaluation> daoImmunizationEvaluationR4() {
    return jpaResourceDao(ImmunizationEvaluation.class);
  }

  @Override
  @Lazy
  @Bean(name = "myImmunizationRecommendationDaoR4")
  public IFhirResourceDao<ImmunizationRecommendation> daoImmunizationRecommendationR4() {
    return jpaResourceDao(ImmunizationRecommendation.class);
  }

  @Override
  @Lazy
  @Bean(name = "myImplementationGuideDaoR4")
  public IFhirResourceDao<ImplementationGuide> daoImplementationGuideR4() {
    return jpaResourceDao(ImplementationGuide.class);
  }

  @Override
  @Lazy
  @Bean(name = "myInsurancePlanDaoR4")
  public IFhirResourceDao<InsurancePlan> daoInsurancePlanR4() {
    return jpaResourceDao(InsurancePlan.class);
  }

  @Override
  @Lazy
  @Bean(name = "myInvoiceDaoR4")
  public IFhirResourceDao<Invoice> daoInvoiceR4() {
    return jpaResourceDao(Invoice.class);
  }

  @Override
  @Lazy
  @Bean(name = "myLibraryDaoR4")
  public IFhirResourceDao<Library> daoLibraryR4() {
    return jpaResourceDao(Library.class);
  }

  @Override
  @Lazy
  @Bean(name = "myLinkageDaoR4")
  public IFhirResourceDao<Linkage> daoLinkageR4() {
    return jpaResourceDao(Linkage.class);
  }

  @Override
  @Lazy
  @Bean(name = "myListDaoR4")
  public IFhirResourceDao<ListResource> daoListResourceR4() {
    return jpaResourceDao(ListResource.class);
  }

  @Override
  @Lazy
  @Bean(name = "myLocationDaoR4")
  public IFhirResourceDao<Location> daoLocationR4() {
    return jpaResourceDao(Location.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMeasureDaoR4")
  public IFhirResourceDao<Measure> daoMeasureR4() {
    return jpaResourceDao(Measure.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMeasureReportDaoR4")
  public IFhirResourceDao<MeasureReport> daoMeasureReportR4() {
    return jpaResourceDao(MeasureReport.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMediaDaoR4")
  public IFhirResourceDao<Media> daoMediaR4() {
    return jpaResourceDao(Media.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationDaoR4")
  public IFhirResourceDao<Medication> daoMedicationR4() {
    return jpaResourceDao(Medication.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationAdministrationDaoR4")
  public IFhirResourceDao<MedicationAdministration> daoMedicationAdministrationR4() {
    return jpaResourceDao(MedicationAdministration.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationDispenseDaoR4")
  public IFhirResourceDao<MedicationDispense> daoMedicationDispenseR4() {
    return jpaResourceDao(MedicationDispense.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationKnowledgeDaoR4")
  public IFhirResourceDao<MedicationKnowledge> daoMedicationKnowledgeR4() {
    return jpaResourceDao(MedicationKnowledge.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationRequestDaoR4")
  public IFhirResourceDao<MedicationRequest> daoMedicationRequestR4() {
    return jpaResourceDao(MedicationRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicationStatementDaoR4")
  public IFhirResourceDao<MedicationStatement> daoMedicationStatementR4() {
    return jpaResourceDao(MedicationStatement.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductDaoR4")
  public IFhirResourceDao<MedicinalProduct> daoMedicinalProductR4() {
    return jpaResourceDao(MedicinalProduct.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductAuthorizationDaoR4")
  public IFhirResourceDao<MedicinalProductAuthorization> daoMedicinalProductAuthorizationR4() {
    return jpaResourceDao(MedicinalProductAuthorization.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductContraindicationDaoR4")
  public IFhirResourceDao<MedicinalProductContraindication> daoMedicinalProductContraindicationR4() {
    return jpaResourceDao(MedicinalProductContraindication.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductIndicationDaoR4")
  public IFhirResourceDao<MedicinalProductIndication> daoMedicinalProductIndicationR4() {
    return jpaResourceDao(MedicinalProductIndication.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductIngredientDaoR4")
  public IFhirResourceDao<MedicinalProductIngredient> daoMedicinalProductIngredientR4() {
    return jpaResourceDao(MedicinalProductIngredient.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductInteractionDaoR4")
  public IFhirResourceDao<MedicinalProductInteraction> daoMedicinalProductInteractionR4() {
    return jpaResourceDao(MedicinalProductInteraction.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductManufacturedDaoR4")
  public IFhirResourceDao<MedicinalProductManufactured> daoMedicinalProductManufacturedR4() {
    return jpaResourceDao(MedicinalProductManufactured.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductPackagedDaoR4")
  public IFhirResourceDao<MedicinalProductPackaged> daoMedicinalProductPackagedR4() {
    return jpaResourceDao(MedicinalProductPackaged.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductPharmaceuticalDaoR4")
  public IFhirResourceDao<MedicinalProductPharmaceutical> daoMedicinalProductPharmaceuticalR4() {
    return jpaResourceDao(MedicinalProductPharmaceutical.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMedicinalProductUndesirableEffectDaoR4")
  public IFhirResourceDao<MedicinalProductUndesirableEffect> daoMedicinalProductUndesirableEffectR4() {
    return jpaResourceDao(MedicinalProductUndesirableEffect.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMessageDefinitionDaoR4")
  public IFhirResourceDao<MessageDefinition> daoMessageDefinitionR4() {
    return jpaResourceDao(MessageDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myMessageHeaderDaoR4")
  public IFhirResourceDaoMessageHeader<MessageHeader> daoMessageHeaderR4() {
    FhirResourceDaoMessageHeaderR4 dao = new FhirResourceDaoMessageHeaderR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((MessageHeader) theResource, this);
      }
    };
    dao.setResourceType(MessageHeader.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myMolecularSequenceDaoR4")
  public IFhirResourceDao<MolecularSequence> daoMolecularSequenceR4() {
    return jpaResourceDao(MolecularSequence.class);
  }

  @Override
  @Lazy
  @Bean(name = "myNamingSystemDaoR4")
  public IFhirResourceDao<NamingSystem> daoNamingSystemR4() {
    return jpaResourceDao(NamingSystem.class);
  }

  @Override
  @Lazy
  @Bean(name = "myNutritionOrderDaoR4")
  public IFhirResourceDao<NutritionOrder> daoNutritionOrderR4() {
    return jpaResourceDao(NutritionOrder.class);
  }

  @Override
  @Lazy
  @Bean(name = "myObservationDaoR4")
  public IFhirResourceDaoObservation<Observation> daoObservationR4() {
    FhirResourceDaoObservationR4 dao = new FhirResourceDaoObservationR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((Observation) theResource, this);
      }
    };
    dao.setResourceType(Observation.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myObservationDefinitionDaoR4")
  public IFhirResourceDao<ObservationDefinition> daoObservationDefinitionR4() {
    return jpaResourceDao(ObservationDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myOperationDefinitionDaoR4")
  public IFhirResourceDao<OperationDefinition> daoOperationDefinitionR4() {
    return jpaResourceDao(OperationDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myOperationOutcomeDaoR4")
  public IFhirResourceDao<OperationOutcome> daoOperationOutcomeR4() {
    return jpaResourceDao(OperationOutcome.class);
  }

  @Override
  @Lazy
  @Bean(name = "myOrganizationDaoR4")
  public IFhirResourceDao<Organization> daoOrganizationR4() {
    return jpaResourceDao(Organization.class);
  }

  @Override
  @Lazy
  @Bean(name = "myOrganizationAffiliationDaoR4")
  public IFhirResourceDao<OrganizationAffiliation> daoOrganizationAffiliationR4() {
    return jpaResourceDao(OrganizationAffiliation.class);
  }

  @Override
  @Lazy
  @Bean(name = "myParametersDaoR4")
  public IFhirResourceDao<Parameters> daoParametersR4() {
    return jpaResourceDao(Parameters.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPatientDaoR4")
  public IFhirResourceDaoPatient<Patient> daoPatientR4() {
    FhirResourceDaoPatientR4 dao = new FhirResourceDaoPatientR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((Patient) theResource, this);
      }
    };
    dao.setResourceType(Patient.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myPaymentNoticeDaoR4")
  public IFhirResourceDao<PaymentNotice> daoPaymentNoticeR4() {
    return jpaResourceDao(PaymentNotice.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPaymentReconciliationDaoR4")
  public IFhirResourceDao<PaymentReconciliation> daoPaymentReconciliationR4() {
    return jpaResourceDao(PaymentReconciliation.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPersonDaoR4")
  public IFhirResourceDao<Person> daoPersonR4() {
    return jpaResourceDao(Person.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPlanDefinitionDaoR4")
  public IFhirResourceDao<PlanDefinition> daoPlanDefinitionR4() {
    return jpaResourceDao(PlanDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPractitionerDaoR4")
  public IFhirResourceDao<Practitioner> daoPractitionerR4() {
    return jpaResourceDao(Practitioner.class);
  }

  @Override
  @Lazy
  @Bean(name = "myPractitionerRoleDaoR4")
  public IFhirResourceDao<PractitionerRole> daoPractitionerRoleR4() {
    return jpaResourceDao(PractitionerRole.class);
  }

  @Override
  @Lazy
  @Bean(name = "myProcedureDaoR4")
  public IFhirResourceDao<Procedure> daoProcedureR4() {
    return jpaResourceDao(Procedure.class);
  }

  @Override
  @Lazy
  @Bean(name = "myProvenanceDaoR4")
  public IFhirResourceDao<Provenance> daoProvenanceR4() {
    return jpaResourceDao(Provenance.class);
  }

  @Override
  @Lazy
  @Bean(name = "myQuestionnaireDaoR4")
  public IFhirResourceDao<Questionnaire> daoQuestionnaireR4() {
    return jpaResourceDao(Questionnaire.class);
  }

  @Override
  @Lazy
  @Bean(name = "myQuestionnaireResponseDaoR4")
  public IFhirResourceDao<QuestionnaireResponse> daoQuestionnaireResponseR4() {
    return jpaResourceDao(QuestionnaireResponse.class);
  }

  @Override
  @Lazy
  @Bean(name = "myRelatedPersonDaoR4")
  public IFhirResourceDao<RelatedPerson> daoRelatedPersonR4() {
    return jpaResourceDao(RelatedPerson.class);
  }

  @Override
  @Lazy
  @Bean(name = "myRequestGroupDaoR4")
  public IFhirResourceDao<RequestGroup> daoRequestGroupR4() {
    return jpaResourceDao(RequestGroup.class);
  }

  @Override
  @Lazy
  @Bean(name = "myResearchDefinitionDaoR4")
  public IFhirResourceDao<ResearchDefinition> daoResearchDefinitionR4() {
    return jpaResourceDao(ResearchDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myResearchElementDefinitionDaoR4")
  public IFhirResourceDao<ResearchElementDefinition> daoResearchElementDefinitionR4() {
    return jpaResourceDao(ResearchElementDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myResearchStudyDaoR4")
  public IFhirResourceDao<ResearchStudy> daoResearchStudyR4() {
    return jpaResourceDao(ResearchStudy.class);
  }

  @Override
  @Lazy
  @Bean(name = "myResearchSubjectDaoR4")
  public IFhirResourceDao<ResearchSubject> daoResearchSubjectR4() {
    return jpaResourceDao(ResearchSubject.class);
  }

  @Override
  @Lazy
  @Bean(name = "myRiskAssessmentDaoR4")
  public IFhirResourceDao<RiskAssessment> daoRiskAssessmentR4() {
    return jpaResourceDao(RiskAssessment.class);
  }

  @Override
  @Lazy
  @Bean(name = "myRiskEvidenceSynthesisDaoR4")
  public IFhirResourceDao<RiskEvidenceSynthesis> daoRiskEvidenceSynthesisR4() {
    return jpaResourceDao(RiskEvidenceSynthesis.class);
  }

  @Override
  @Lazy
  @Bean(name = "myScheduleDaoR4")
  public IFhirResourceDao<Schedule> daoScheduleR4() {
    return jpaResourceDao(Schedule.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySearchParameterDaoR4")
  public IFhirResourceDaoSearchParameter<SearchParameter> daoSearchParameterR4() {
    FhirResourceDaoSearchParameterR4 dao = new FhirResourceDaoSearchParameterR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((SearchParameter) theResource, this);
      }
    };
    dao.setResourceType(SearchParameter.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myServiceRequestDaoR4")
  public IFhirResourceDao<ServiceRequest> daoServiceRequestR4() {
    return jpaResourceDao(ServiceRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySlotDaoR4")
  public IFhirResourceDao<Slot> daoSlotR4() {
    return jpaResourceDao(Slot.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySpecimenDaoR4")
  public IFhirResourceDao<Specimen> daoSpecimenR4() {
    return jpaResourceDao(Specimen.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySpecimenDefinitionDaoR4")
  public IFhirResourceDao<SpecimenDefinition> daoSpecimenDefinitionR4() {
    return jpaResourceDao(SpecimenDefinition.class);
  }

  @Override
  @Lazy
  @Bean(name = "myStructureDefinitionDaoR4")
  public IFhirResourceDaoStructureDefinition<StructureDefinition> daoStructureDefinitionR4() {
    FhirResourceDaoStructureDefinitionR4 dao = new FhirResourceDaoStructureDefinitionR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((StructureDefinition) theResource, this);
      }
    };
    dao.setResourceType(StructureDefinition.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myStructureMapDaoR4")
  public IFhirResourceDao<StructureMap> daoStructureMapR4() {
    return jpaResourceDao(StructureMap.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubscriptionDaoR4")
  public IFhirResourceDaoSubscription<Subscription> daoSubscriptionR4() {
    FhirResourceDaoSubscriptionR4 dao = new FhirResourceDaoSubscriptionR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((Subscription) theResource, this);
      }
    };
    dao.setResourceType(Subscription.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceDaoR4")
  public IFhirResourceDao<Substance> daoSubstanceR4() {
    return jpaResourceDao(Substance.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceNucleicAcidDaoR4")
  public IFhirResourceDao<SubstanceNucleicAcid> daoSubstanceNucleicAcidR4() {
    return jpaResourceDao(SubstanceNucleicAcid.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstancePolymerDaoR4")
  public IFhirResourceDao<SubstancePolymer> daoSubstancePolymerR4() {
    return jpaResourceDao(SubstancePolymer.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceProteinDaoR4")
  public IFhirResourceDao<SubstanceProtein> daoSubstanceProteinR4() {
    return jpaResourceDao(SubstanceProtein.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceReferenceInformationDaoR4")
  public IFhirResourceDao<SubstanceReferenceInformation> daoSubstanceReferenceInformationR4() {
    return jpaResourceDao(SubstanceReferenceInformation.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceSourceMaterialDaoR4")
  public IFhirResourceDao<SubstanceSourceMaterial> daoSubstanceSourceMaterialR4() {
    return jpaResourceDao(SubstanceSourceMaterial.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySubstanceSpecificationDaoR4")
  public IFhirResourceDao<SubstanceSpecification> daoSubstanceSpecificationR4() {
    return jpaResourceDao(SubstanceSpecification.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySupplyDeliveryDaoR4")
  public IFhirResourceDao<SupplyDelivery> daoSupplyDeliveryR4() {
    return jpaResourceDao(SupplyDelivery.class);
  }

  @Override
  @Lazy
  @Bean(name = "mySupplyRequestDaoR4")
  public IFhirResourceDao<SupplyRequest> daoSupplyRequestR4() {
    return jpaResourceDao(SupplyRequest.class);
  }

  @Override
  @Lazy
  @Bean(name = "myTaskDaoR4")
  public IFhirResourceDao<Task> daoTaskR4() {
    return jpaResourceDao(Task.class);
  }

  @Override
  @Lazy
  @Bean(name = "myTerminologyCapabilitiesDaoR4")
  public IFhirResourceDao<TerminologyCapabilities> daoTerminologyCapabilitiesR4() {
    return jpaResourceDao(TerminologyCapabilities.class);
  }

  @Override
  @Lazy
  @Bean(name = "myTestReportDaoR4")
  public IFhirResourceDao<TestReport> daoTestReportR4() {
    return jpaResourceDao(TestReport.class);
  }

  @Override
  @Lazy
  @Bean(name = "myTestScriptDaoR4")
  public IFhirResourceDao<TestScript> daoTestScriptR4() {
    return jpaResourceDao(TestScript.class);
  }

  @Override
  @Lazy
  @Bean(name = "myValueSetDaoR4")
  public IFhirResourceDaoValueSet<ValueSet, Coding, CodeableConcept> daoValueSetR4() {
    FhirResourceDaoValueSetR4 dao = new FhirResourceDaoValueSetR4() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((ValueSet) theResource, this);
      }
    };
    dao.setResourceType(ValueSet.class);
    dao.setContext(fhirContextR4());
    return dao;
  }

  @Override
  @Lazy
  @Bean(name = "myVerificationResultDaoR4")
  public IFhirResourceDao<VerificationResult> daoVerificationResultR4() {
    return jpaResourceDao(VerificationResult.class);
  }

  @Override
  @Lazy
  @Bean(name = "myVisionPrescriptionDaoR4")
  public IFhirResourceDao<VisionPrescription> daoVisionPrescriptionR4() {
    return jpaResourceDao(VisionPrescription.class);
  }

  private <T extends IBaseResource> IFhirResourceDao<T> jpaResourceDao(Class<T> resourceType) {
    JpaResourceDao<T> dao = new JpaResourceDao<T>() {
      @Override
      protected void preProcessResourceForStorage(IBaseResource theResource) {
        resourcePreStorageValidator.validate((T) theResource, this);
      }
    };
    dao.setResourceType(resourceType);
    dao.setContext(fhirContextR4());
    return dao;
  }
}
