package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.collections.AdviceCollection;
import com.dpdocter.collections.BabyNoteCollection;
import com.dpdocter.collections.CementCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorDrugCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.ECGDetailsCollection;
import com.dpdocter.collections.EarsExaminationCollection;
import com.dpdocter.collections.EchoCollection;
import com.dpdocter.collections.ExpenseTypeCollection;
import com.dpdocter.collections.GeneralExamCollection;
import com.dpdocter.collections.HolterCollection;
import com.dpdocter.collections.ImplantCollection;
import com.dpdocter.collections.IndicationOfUSGCollection;
import com.dpdocter.collections.IndirectLarygoscopyExaminationCollection;
import com.dpdocter.collections.IngredientCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LabourNoteCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MenstrualHistoryCollection;
import com.dpdocter.collections.NeckExaminationCollection;
import com.dpdocter.collections.NoseExaminationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.NutrientCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.ObstetricHistoryCollection;
import com.dpdocter.collections.OperationNoteCollection;
import com.dpdocter.collections.OralCavityAndThroatExaminationCollection;
import com.dpdocter.collections.PACollection;
import com.dpdocter.collections.PSCollection;
import com.dpdocter.collections.PVCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PresentComplaintCollection;
import com.dpdocter.collections.PresentComplaintHistoryCollection;
import com.dpdocter.collections.PresentingComplaintEarsCollection;
import com.dpdocter.collections.PresentingComplaintNoseCollection;
import com.dpdocter.collections.PresentingComplaintOralCavityCollection;
import com.dpdocter.collections.PresentingComplaintThroatCollection;
import com.dpdocter.collections.ProcedureNoteCollection;
import com.dpdocter.collections.ProvisionalDiagnosisCollection;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.SymptomDiseaseConditionCollection;
import com.dpdocter.collections.SystemExamCollection;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.TreatmentServicesCostCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.XRayDetailsCollection;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESAdvicesDocument;
import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESCementDocument;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESImplantDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.elasticsearch.services.ESDischargeSummaryService;
import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.OTPState;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdviceRepository;
import com.dpdocter.repository.AppLinkDetailsRepository;
import com.dpdocter.repository.BabyNoteRepository;
import com.dpdocter.repository.CementRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorDrugRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.ECGDetailsRepository;
import com.dpdocter.repository.EarsExaminationRepository;
import com.dpdocter.repository.EchoRepository;
import com.dpdocter.repository.ExpenseTypeRepository;
import com.dpdocter.repository.GeneralExamRepository;
import com.dpdocter.repository.HolterRepository;
import com.dpdocter.repository.ImplantRepository;
import com.dpdocter.repository.IndicationOfUSGRepository;
import com.dpdocter.repository.IndirectLarygoscopyExaminationRepository;
import com.dpdocter.repository.IngredientRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LabourNoteRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MenstrualHistoryRepository;
import com.dpdocter.repository.NeckExaminationRepository;
import com.dpdocter.repository.NoseExaminationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.NutrientRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.ObstetricHistoryRepository;
import com.dpdocter.repository.OperationNoteRepository;
import com.dpdocter.repository.OralCavityThroatExaminationRepository;
import com.dpdocter.repository.PARepository;
import com.dpdocter.repository.PSRepository;
import com.dpdocter.repository.PVRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PresentComplaintHistoryRepository;
import com.dpdocter.repository.PresentComplaintRepository;
import com.dpdocter.repository.PresentingComplaintEarsRepository;
import com.dpdocter.repository.PresentingComplaintNosesRepository;
import com.dpdocter.repository.PresentingComplaintOralCavityRepository;
import com.dpdocter.repository.PresentingComplaintThroatRepository;
import com.dpdocter.repository.ProcedureNoteRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.ProvisionalDiagnosisRepository;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SymptomDiseaseConditionRepository;
import com.dpdocter.repository.SystemExamRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.TreatmentServicesCostRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.XRayDetailsRepository;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class TransactionalManagementServiceImpl implements TransactionalManagementService {

	private static Logger logger = Logger.getLogger(TransactionalManagementServiceImpl.class.getName());

	@Autowired
	private TransnationalRepositiory transnationalRepositiory;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ESDischargeSummaryService esDischargeSummaryService;

	@Autowired
	private LabourNoteRepository labourNoteRepository;

	@Autowired
	private BabyNoteRepository babyNoteRepository;

	@Autowired
	private OperationNoteRepository operationNoteRepository;

	@Autowired
	private ImplantRepository implantRepository;

	@Autowired
	private CementRepository cementRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private DoctorDrugRepository doctorDrugRepository;

	@Autowired
	private LabTestRepository labTestRepository;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private PresentingComplaintEarsRepository presentingComplaintEarsRepository;

	@Autowired
	private InvestigationRepository investigationRepository;

	@Autowired
	private DiagnosisRepository diagnosisRepository;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private OTPService otpService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private ESMasterService esMasterService;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private ESTreatmentService esTreatmentService;

	@Autowired
	private TreatmentServicesRepository treatmentServicesRepository;

	@Autowired
	private TreatmentServicesCostRepository treatmentServicesCostRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private SMSTrackRepository smsTrackRepository;

	@Autowired
	private XRayDetailsRepository xRayDetailsRepository;

	@Autowired
	private HolterRepository holterRepository;

	@Autowired
	private EchoRepository echoRepository;

	@Autowired
	private ProcedureNoteRepository procedureNoteRepository;

	@Autowired
	private ECGDetailsRepository ecgDetailsRepository;

	@Autowired
	private PSRepository psRepository;

	@Autowired
	private PVRepository pvRepository;

	@Autowired
	private PARepository paRepository;

	@Autowired
	private IndicationOfUSGRepository indicationOfUSGRepository;

	@Autowired
	private ObstetricHistoryRepository obstetricHistoryRepository;

	@Autowired
	private MenstrualHistoryRepository menstrualHistoryRepository;

	@Autowired
	private PresentComplaintHistoryRepository presentComplaintHistoryRepository;

	@Autowired
	private SystemExamRepository systemExamRepository;

	@Autowired
	private GeneralExamRepository generalExamRepository;

	@Autowired
	private ProvisionalDiagnosisRepository provisionalDiagnosisRepository;

	@Autowired
	private PresentComplaintRepository presentComplaintRepository;

	@Autowired
	private AdviceRepository adviceRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private ESLocaleService esLocaleService;

	@Autowired
	private PresentingComplaintNosesRepository presentingComplaintNosesRepository;

	@Autowired
	private PresentingComplaintThroatRepository presentingComplaintThroatRepository;

	@Autowired
	private PresentingComplaintOralCavityRepository presentingComplaintOralCavityRepository;

	@Autowired
	private NoseExaminationRepository noseExaminationRepository;

	@Autowired
	private EarsExaminationRepository earsExaminationRepository;

	@Autowired
	private OralCavityThroatExaminationRepository oralCavityThroatExaminationRepository;

	@Autowired
	private IndirectLarygoscopyExaminationRepository indirectLarygoscopyExaminationRepository;

	@Autowired
	private NeckExaminationRepository neckExaminationRepository;

	@Autowired
	private AppLinkDetailsRepository appLinkDetailsRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ExpenseTypeRepository expenseTypeRepository;

	@Autowired
	private NutrientRepository nutrientRepository;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private ESExpenseTypeService exExpenseTypeService;

	@Autowired
	private ESRecipeService ESRecipeService;

	@Autowired
	private ServicesRepository servicesRepository;
	
	@Value(value = "${mail.appointment.details.subject}")
	private String appointmentDetailsSub;

	@Value(value = "${prescription.add.patient.download.app.message}")
	private String downloadAppMessageToPatient;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Value("${send.sms}")
	private Boolean sendSMS;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;
	
	@Autowired
	SpecialityRepository specialityRepository;
	
	@Autowired
	SymptomDiseaseConditionRepository symptomDiseaseConditionRepository;
	
	//@Scheduled(cron = "00 00 3 * * *", zone = "IST")
	//	@Scheduled(fixedDelay = 1800)
	@Override
	@Transactional
	public void checkResources() {
		System.out.println(">>> Scheduled test service <<<");
		List<TransactionalCollection> transactionalCollections = null;
		try {
			transactionalCollections = transnationalRepositiory.findByIsCached(false);
			if (transactionalCollections != null) {
				for (TransactionalCollection transactionalCollection : transactionalCollections) {
					if (transactionalCollection.getResourceId() != null)
						switch (transactionalCollection.getResource()) {

						case PATIENT:
							checkPatient(transactionalCollection.getResourceId());
							break;
						case DRUG:
							checkDrug(transactionalCollection.getResourceId());
							break;
						case DOCTORDRUG:
							checkDoctorDrug(transactionalCollection.getResourceId());
							break;
						case LABTEST:
							checkLabTest(transactionalCollection.getResourceId());
							break;
						case COMPLAINT:
							checkComplaint(transactionalCollection.getResourceId());
							break;
						case DIAGNOSIS:
							checkDiagnosis(transactionalCollection.getResourceId());
							break;
						case DIAGRAM:
							checkDiagrams(transactionalCollection.getResourceId());
							break;
						case INVESTIGATION:
							checkInvestigation(transactionalCollection.getResourceId());
							break;
						case NOTES:
							checkNotes(transactionalCollection.getResourceId());
							break;
						case OBSERVATION:
							checkObservation(transactionalCollection.getResourceId());
							break;
						case CITY:
							checkCity(transactionalCollection.getResourceId());
							break;
						case LANDMARKLOCALITY:
							checkLandmarkLocality(transactionalCollection.getResourceId());
							break;
						case DOCTOR:
							checkDoctor(transactionalCollection.getResourceId(), null);
							break;
						case LOCATION:
							checkLocation(transactionalCollection.getResourceId());
							break;
						case REFERENCE:
							checkReference(transactionalCollection.getResourceId());
							break;
						case DISEASE:
							checkDisease(transactionalCollection.getResourceId());
							break;
						case DIAGNOSTICTEST:
							checkDiagnosticTest(transactionalCollection.getResourceId());
							break;
						case TREATMENTSERVICE:
							checkTreatmentService(transactionalCollection.getResourceId());
							break;
						case TREATMENTSERVICECOST:
							checkTreatmentServiceCost(transactionalCollection.getResourceId());
							break;
						case XRAY:
							checkXray(transactionalCollection.getResourceId());
							break;
						case ADVICE:
							checkAdvice(transactionalCollection.getResourceId());
							break;
						case PRESENT_COMPLAINT:
							checkPresentComplaint(transactionalCollection.getResourceId());
							break;
						case GENERAL_EXAMINATION:
							checkGeneralExam(transactionalCollection.getResourceId());
							break;
						case PROVISIONAL_DIAGNOSIS:
							checkProvisionalDignosis(transactionalCollection.getResourceId());
							break;
						case SYSTEMIC_EXAMINATION:
							checkSystemExam(transactionalCollection.getResourceId());
							break;
						case HISTORY_OF_PRESENT_COMPLAINT:
							checkPresentComplaintHistory(transactionalCollection.getResourceId());
							break;
						case PS:
							checkPS(transactionalCollection.getResourceId());
							break;
						case PA:
							checkPA(transactionalCollection.getResourceId());
							break;
						case PV:
							checkPV(transactionalCollection.getResourceId());
							break;
						case ECHO:
							checkEcho(transactionalCollection.getResourceId());
							break;
						case INDICATION_OF_USG:
							checkIndicationOfUCG(transactionalCollection.getResourceId());
							break;

						case ECG:
							checkECG(transactionalCollection.getResourceId());
							break;

						case HOLTER:
							checkHolter(transactionalCollection.getResourceId());
							break;

						case PHARMACY:
							checkPharmacy(transactionalCollection.getResourceId());
							break;

						case PROCEDURE_NOTE:
							checkProcedureNote(transactionalCollection.getResourceId());
							break;
						case PC_NOSE:
							checkPCNoses(transactionalCollection.getResourceId());
							break;
						case PC_EARS:
							checkPCEars(transactionalCollection.getResourceId());
							break;
						case PC_THROAT:
							checkPCThroat(transactionalCollection.getResourceId());
							break;
						case PC_ORAL_CAVITY:
							checkPCOralCavity(transactionalCollection.getResourceId());
							break;
						case EARS_EXAM:
							checkEarsExam(transactionalCollection.getResourceId());
							break;
						case INDIRECT_LARYGOSCOPY_EXAM:
							checkINdirectExam(transactionalCollection.getResourceId());
							break;
						case ORAL_CAVITY_THROAT_EXAM:
							checkOralCavityAndThroatExam(transactionalCollection.getResourceId());
							break;
						case NOSE_EXAM:
							checkNoseExam(transactionalCollection.getResourceId());
							break;
						case NECK_EXAM:
							checkNeckExam(transactionalCollection.getResourceId());
							break;
						case LABOUR_NOTES:
							checkLabourNotes(transactionalCollection.getResourceId());
							break;

						case BABY_NOTES:
							checkBabyNote(transactionalCollection.getResourceId());
							break;

						case MENSTRUAL_HISTORY:
							checkmenstrualHistory(transactionalCollection.getResourceId());
							break;
						case OBSTETRIC_HISTORY:
							checkObstresrticHistory(transactionalCollection.getResourceId());
							break;

						case OPERATION_NOTES:
							checkOperationNote(transactionalCollection.getResourceId());
							break;
						case CEMENT:
							checkCement(transactionalCollection.getResourceId());
							break;
						case IMPLANT:
							checkImplant(transactionalCollection.getResourceId());
							break;
						case EXPENSE_TYPE:
							checkExpenseType(transactionalCollection.getResourceId());
							break;
						case RECIPE:
							checkRecipe(transactionalCollection.getResourceId());
							break;
						case INGREDIENT:
							checkIngredient(transactionalCollection.getResourceId());
							break;
						case NUTRIENT:
							checkNutrient(transactionalCollection.getResourceId());
							break;
						case STATE:
							break;
						case SERVICE:checkService(transactionalCollection.getResourceId());
							break;
						case SPECIALITY:checkSpeciality(transactionalCollection.getResourceId());
						break;
						case SYMPTOM_DISEASE_CONDITION:checkSymptomsDiseasesCondition(transactionalCollection.getResourceId());
						break;
						default:
							break;
						}
				}
			}
			// Expire invalid otp
			checkOTP();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkOTP() {
		try {
			List<OTPCollection> otpCollections = otpRepository.findNonExpiredOtp(OTPState.EXPIRED.getState());
			if (otpCollections != null) {
				for (OTPCollection otpCollection : otpCollections) {
					if (otpCollection.getState().equals(OTPState.VERIFIED)) {
						if (!otpService.isOTPValid(otpCollection.getCreatedTime())) {
							otpCollection.setState(OTPState.EXPIRED);
						}
					} else if (otpCollection.getState().equals(OTPState.NOTVERIFIED)) {
						if (!otpService.isNonVerifiedOTPValid(otpCollection.getCreatedTime())) {
							otpCollection.setState(OTPState.EXPIRED);
						}
					}
					otpRepository.save(otpCollection);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void addResource(ObjectId resourceId, Resource resource, boolean isCached) {
		TransactionalCollection transactionalCollection = null;
		try {
			transactionalCollection = transnationalRepositiory.findByResourceIdAndResource(resourceId,
					resource.getType());
			if (transactionalCollection == null) {
				transactionalCollection = new TransactionalCollection();
				transactionalCollection.setResourceId(resourceId);
				transactionalCollection.setResource(resource);
				transactionalCollection.setIsCached(isCached);
				transnationalRepositiory.save(transactionalCollection);
			} else {
				transactionalCollection.setIsCached(isCached);
				transnationalRepositiory.save(transactionalCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public void checkPatient(ObjectId id) {
		try {
			UserCollection userCollection = userRepository.findOne(id);
			List<PatientCollection> patientCollections = patientRepository.findByUserId(id);
			if (userCollection != null && patientCollections != null) {
				for (PatientCollection patientCollection : patientCollections) {
					ESPatientDocument patientDocument = new ESPatientDocument();

					BeanUtil.map(userCollection, patientDocument);
					if (patientCollection != null)
						BeanUtil.map(patientCollection, patientDocument);

					if (patientCollection != null)
						patientDocument.setId(patientCollection.getId().toString());

					if (patientCollection != null)
						esRegistrationService.addPatient(patientDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDrug(ObjectId id) {
		try {
			DrugCollection drugCollection = drugRepository.findOne(id);
			if (drugCollection != null) {
				ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if (drugCollection.getDrugType() != null) {
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esPrescriptionService.addDrug(esDrugDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDoctorDrug(ObjectId resourceId) {
		try {
			DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findOne(resourceId);
			if (doctorDrugCollection != null) {
				DrugCollection drugCollection = drugRepository.findOne(doctorDrugCollection.getDrugId());
				if (drugCollection != null) {
					ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
					BeanUtil.map(drugCollection, esDoctorDrugDocument);
					BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
					esDoctorDrugDocument.setId(drugCollection.getId().toString());
					esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, resourceId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkLabTest(ObjectId id) {
		try {
			LabTestCollection labTestCollection = labTestRepository.findOne(id);
			if (labTestCollection != null) {
				ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
				BeanUtil.map(labTestCollection, esLabTestDocument);
				esPrescriptionService.addLabTest(esLabTestDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkComplaint(ObjectId id) {
		try {
			ComplaintCollection complaintCollection = complaintRepository.findOne(id);
			if (complaintCollection != null) {
				ESComplaintsDocument esComplaintsDocument = new ESComplaintsDocument();
				BeanUtil.map(complaintCollection, esComplaintsDocument);
				esClinicalNotesService.addComplaints(esComplaintsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkObservation(ObjectId id) {
		try {
			ObservationCollection observationCollection = observationRepository.findOne(id);
			if (observationCollection != null) {
				ESObservationsDocument esObservationsDocument = new ESObservationsDocument();
				BeanUtil.map(observationCollection, esObservationsDocument);
				esClinicalNotesService.addObservations(esObservationsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkInvestigation(ObjectId id) {
		try {
			InvestigationCollection investigationCollection = investigationRepository.findOne(id);
			if (investigationCollection != null) {
				ESInvestigationsDocument esInvestigationsDocument = new ESInvestigationsDocument();
				BeanUtil.map(investigationCollection, esInvestigationsDocument);
				esClinicalNotesService.addInvestigations(esInvestigationsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDiagnosis(ObjectId id) {
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
			if (diagnosisCollection != null) {
				ESDiagnosesDocument esDiagnosesDocument = new ESDiagnosesDocument();
				BeanUtil.map(diagnosisCollection, esDiagnosesDocument);
				esClinicalNotesService.addDiagnoses(esDiagnosesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkNotes(ObjectId id) {
		try {
			NotesCollection notesCollection = notesRepository.findOne(id);
			if (notesCollection != null) {
				ESNotesDocument esNotesDocument = new ESNotesDocument();
				BeanUtil.map(notesCollection, esNotesDocument);
				esClinicalNotesService.addNotes(esNotesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDiagrams(ObjectId id) {
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
			if (diagramsCollection != null) {
				ESDiagramsDocument esDiagramsDocument = new ESDiagramsDocument();
				BeanUtil.map(diagramsCollection, esDiagramsDocument);
				esClinicalNotesService.addDiagrams(esDiagramsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkLocation(ObjectId resourceId) {
		try {
			LocationCollection locationCollection = locationRepository.findOne(resourceId);
			if (locationCollection != null) {
				DoctorLocation doctorLocation = new DoctorLocation();
				BeanUtil.map(locationCollection, doctorLocation);
				doctorLocation.setLocationId(locationCollection.getId().toString());
				if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
					List<String> images = new ArrayList<String>();
					for (ClinicImage clinicImage : locationCollection.getImages()) {
						images.add(clinicImage.getImageUrl());
					}
					doctorLocation.setImages(null);
					doctorLocation.setImages(images);
				}

				esRegistrationService.editLocation(doctorLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void checkDoctor(ObjectId resourceId, ObjectId locationId) {
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
			UserCollection userCollection = userRepository.findOne(resourceId);
			if (doctorCollection != null && userCollection != null) {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
				if (locationId == null)
					doctorClinicProfileCollections = doctorClinicProfileRepository.findByDoctorId(resourceId);
				else {
					DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdLocationId(resourceId, locationId);
					doctorClinicProfileCollections = new ArrayList<DoctorClinicProfileCollection>();
					doctorClinicProfileCollections.add(doctorClinicProfileCollection);
				}
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					LocationCollection locationCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(doctorClinicProfileCollection.getLocationId())) {
						locationCollection = locationRepository.findOne(doctorClinicProfileCollection.getLocationId());
					}
					GeoPoint geoPoint = null;

					ESDoctorDocument doctorDocument = new ESDoctorDocument();
					if (locationCollection != null) {
						if (locationCollection.getLatitude() != null && locationCollection.getLongitude() != null)
							geoPoint = new GeoPoint(locationCollection.getLatitude(),
									locationCollection.getLongitude());

						BeanUtil.map(locationCollection, doctorDocument);

						ESLocationDocument esLocationDocument = new ESLocationDocument();
						BeanUtil.map(doctorClinicProfileCollection, esLocationDocument);
						BeanUtil.map(locationCollection, esLocationDocument);

						if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
							List<String> images = new ArrayList<String>();
							for (ClinicImage clinicImage : locationCollection.getImages()) {
								images.add(clinicImage.getImageUrl());
							}
							doctorDocument.setImages(null);
							doctorDocument.setImages(images);

							esLocationDocument.setImages(null);
							esLocationDocument.setImages(images);

							doctorDocument.setClinicWorkingSchedules(null);
							doctorDocument.setClinicWorkingSchedules(locationCollection.getClinicWorkingSchedules());

							doctorDocument.setAlternateClinicNumbers(null);
							doctorDocument.setAlternateClinicNumbers(locationCollection.getAlternateClinicNumbers());

							esLocationDocument.setClinicWorkingSchedules(null);
							esLocationDocument
									.setClinicWorkingSchedules(locationCollection.getClinicWorkingSchedules());

							esLocationDocument.setAlternateClinicNumbers(null);
							esLocationDocument
									.setAlternateClinicNumbers(locationCollection.getAlternateClinicNumbers());

						}
						esLocationDocument.setGeoPoint(geoPoint);
						esLocationDocument.setId(locationCollection.getId().toString());
						esLocationRepository.save(esLocationDocument);

					}
					if (userCollection != null)
						BeanUtil.map(userCollection, doctorDocument);
					if (doctorCollection != null)
						BeanUtil.map(doctorCollection, doctorDocument);
					if (doctorClinicProfileCollection != null) {
						BeanUtil.map(doctorClinicProfileCollection, doctorDocument);
						doctorDocument.setAppointmentBookingNumber(null);
						doctorDocument.setAppointmentBookingNumber(
								doctorClinicProfileCollection.getAppointmentBookingNumber());
						doctorDocument.setWorkingSchedules(null);
						doctorDocument.setWorkingSchedules(doctorClinicProfileCollection.getWorkingSchedules());
					}
					if (locationCollection != null)
						doctorDocument.setLocationId(locationCollection.getId().toString());

					if (doctorCollection.getProfessionalMemberships() != null
							&& !doctorCollection.getProfessionalMemberships().isEmpty()) {
						List<String> professionalMemberships = (List<String>) CollectionUtils.collect(
								(Collection<?>) professionalMembershipRepository
										.findAll(doctorCollection.getProfessionalMemberships()),
								new BeanToPropertyValueTransformer("membership"));
						doctorDocument.setProfessionalMemberships(professionalMemberships);
					}

					
					esRegistrationService.addDoctor(doctorDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkLandmarkLocality(ObjectId resourceId) {
		try {
			LandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository.findOne(resourceId);
			if (landmarkLocalityCollection != null) {
				ESLandmarkLocalityDocument esLocalityLandmarkDocument = new ESLandmarkLocalityDocument();
				BeanUtil.map(landmarkLocalityCollection, esLocalityLandmarkDocument);
				esCityService.addLocalityLandmark(esLocalityLandmarkDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkCity(ObjectId resourceId) {
		try {
			CityCollection cityCollection = cityRepository.findOne(resourceId);
			if (cityCollection != null) {
				ESCityDocument esCityDocument = new ESCityDocument();
				BeanUtil.map(cityCollection, esCityDocument);

				esCityService.addCities(esCityDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkReference(ObjectId resourceId) {
		try {
			ReferencesCollection referenceCollection = referenceRepository.findOne(resourceId);
			if (referenceCollection != null) {
				ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
				BeanUtil.map(referenceCollection, esReferenceDocument);
				esRegistrationService.addEditReference(esReferenceDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkDisease(ObjectId resourceId) {
		try {
			DiseasesCollection diseasesCollection = diseasesRepository.findOne(resourceId);
			if (diseasesCollection != null) {
				ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
				BeanUtil.map(diseasesCollection, esDiseasesDocument);
				esMasterService.addEditDisease(esDiseasesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkDiagnosticTest(ObjectId resourceId) {
		try {
			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(resourceId);
			if (diagnosticTestCollection != null) {
				ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, esDiagnosticTestDocument);
				esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkTreatmentService(ObjectId resourceId) {
		try {
			TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository.findOne(resourceId);
			if (treatmentServicesCollection != null) {
				ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
				BeanUtil.map(treatmentServicesCollection, esTreatmentServiceDocument);
				esTreatmentService.addEditService(esTreatmentServiceDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkTreatmentServiceCost(ObjectId resourceId) {
		try {
			TreatmentServicesCostCollection treatmentServicesCostCollection = treatmentServicesCostRepository
					.findOne(resourceId);
			if (treatmentServicesCostCollection != null) {
				ESTreatmentServiceCostDocument esTreatmentServiceCostDocument = new ESTreatmentServiceCostDocument();
				BeanUtil.map(treatmentServicesCostCollection, esTreatmentServiceCostDocument);
				esTreatmentService.addEditServiceCost(esTreatmentServiceCostDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkXray(ObjectId resourceId) {
		try {
			XRayDetailsCollection xRayDetailsCollection = xRayDetailsRepository.findOne(resourceId);
			if (xRayDetailsCollection != null) {
				ESXRayDetailsDocument esxRayDetailsDocument = new ESXRayDetailsDocument();
				BeanUtil.map(xRayDetailsCollection, esxRayDetailsDocument);
				esClinicalNotesService.addXRayDetails(esxRayDetailsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkHolter(ObjectId resourceId) {
		try {
			HolterCollection holterCollection = holterRepository.findOne(resourceId);
			if (holterCollection != null) {
				ESHolterDocument esHolterDocument = new ESHolterDocument();
				BeanUtil.map(holterCollection, esHolterDocument);
				esClinicalNotesService.addHolter(esHolterDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkPharmacy(ObjectId resourceId) {
		try {
			LocaleCollection localeCollection = localeRepository.findOne(resourceId);
			UserCollection userCollection = null;
			if (localeCollection != null) {
				userCollection = userRepository.findAdminByMobileNumber(localeCollection.getContactNumber(),
						"PHARMACY");
			}

			if (localeCollection != null && userCollection != null) {
				ESUserLocaleDocument esUserLocaleDocument = new ESUserLocaleDocument();
				BeanUtil.map(userCollection, esUserLocaleDocument);
				BeanUtil.map(localeCollection, esUserLocaleDocument);
				esUserLocaleDocument.setLocaleId(localeCollection.getId().toString());
				esLocaleService.addLocale(esUserLocaleDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkEcho(ObjectId resourceId) {
		try {

			EchoCollection echoCollection = echoRepository.findOne(resourceId);
			if (echoCollection != null) {
				ESEchoDocument esEchoDocument = new ESEchoDocument();
				BeanUtil.map(echoCollection, esEchoDocument);
				esClinicalNotesService.addEcho(esEchoDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkProcedureNote(ObjectId resourceId) {
		try {

			ProcedureNoteCollection procedureNoteCollection = procedureNoteRepository.findOne(resourceId);
			if (procedureNoteCollection != null) {
				ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
				BeanUtil.map(procedureNoteCollection, esProcedureNoteDocument);
				esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkECG(ObjectId resourceId) {
		try {

			ECGDetailsCollection ecgDetailsCollection = ecgDetailsRepository.findOne(resourceId);
			if (ecgDetailsCollection != null) {
				ESECGDetailsDocument esECGDetailsDocument = new ESECGDetailsDocument();
				BeanUtil.map(ecgDetailsCollection, esECGDetailsDocument);
				esClinicalNotesService.addECGDetails(esECGDetailsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPS(ObjectId resourceId) {
		try {

			PSCollection psCollection = psRepository.findOne(resourceId);
			if (psCollection != null) {
				ESPSDocument espsDocument = new ESPSDocument();
				BeanUtil.map(psCollection, espsDocument);
				esClinicalNotesService.addPS(espsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPA(ObjectId resourceId) {
		try {

			PACollection paCollection = paRepository.findOne(resourceId);
			if (paCollection != null) {
				ESPADocument espaDocument = new ESPADocument();
				BeanUtil.map(paCollection, espaDocument);
				esClinicalNotesService.addPA(espaDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPV(ObjectId resourceId) {
		try {

			PVCollection pvCollection = pvRepository.findOne(resourceId);
			if (pvCollection != null) {
				ESPVDocument espvDocument = new ESPVDocument();
				BeanUtil.map(pvCollection, espvDocument);
				esClinicalNotesService.addPV(espvDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkIndicationOfUCG(ObjectId resourceId) {
		try {

			IndicationOfUSGCollection indicationOfUCGCollection = indicationOfUSGRepository.findOne(resourceId);
			if (indicationOfUCGCollection != null) {
				ESIndicationOfUSGDocument esIndicationOfUSGDocument = new ESIndicationOfUSGDocument();
				BeanUtil.map(indicationOfUCGCollection, esIndicationOfUSGDocument);
				esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSGDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkSystemExam(ObjectId resourceId) {
		try {

			SystemExamCollection systemExamCollection = systemExamRepository.findOne(resourceId);
			if (systemExamCollection != null) {
				ESSystemExamDocument esSystemExamDocument = new ESSystemExamDocument();
				BeanUtil.map(systemExamCollection, esSystemExamDocument);
				esClinicalNotesService.addSystemExam(esSystemExamDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkProvisionalDignosis(ObjectId resourceId) {
		try {

			ProvisionalDiagnosisCollection provitionalDiagnosisCollection = provisionalDiagnosisRepository
					.findOne(resourceId);
			if (provitionalDiagnosisCollection != null) {
				ESPresentComplaintHistoryDocument esPresentComplaintHistoryDocument = new ESPresentComplaintHistoryDocument();
				BeanUtil.map(provitionalDiagnosisCollection, esPresentComplaintHistoryDocument);
				esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistoryDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPresentComplaintHistory(ObjectId resourceId) {
		try {

			PresentComplaintHistoryCollection presentComplaintHistoryCollection = presentComplaintHistoryRepository
					.findOne(resourceId);
			if (presentComplaintHistoryCollection != null) {
				ESPresentComplaintHistoryDocument esPresentComplaintHistoryDocument = new ESPresentComplaintHistoryDocument();
				BeanUtil.map(presentComplaintHistoryCollection, esPresentComplaintHistoryDocument);
				esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistoryDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkGeneralExam(ObjectId resourceId) {
		try {

			GeneralExamCollection generalExamCollection = generalExamRepository.findOne(resourceId);
			if (generalExamCollection != null) {
				ESGeneralExamDocument esGeneralExamDocument = new ESGeneralExamDocument();
				BeanUtil.map(generalExamCollection, esGeneralExamDocument);
				esClinicalNotesService.addGeneralExam(esGeneralExamDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPresentComplaint(ObjectId resourceId) {
		try {

			PresentComplaintCollection presentComplaintCollection = presentComplaintRepository.findOne(resourceId);
			if (presentComplaintCollection != null) {
				ESPresentComplaintDocument esPresentComplaintDocument = new ESPresentComplaintDocument();
				BeanUtil.map(presentComplaintCollection, esPresentComplaintDocument);
				esClinicalNotesService.addPresentComplaint(esPresentComplaintDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkAdvice(ObjectId resourceId) {
		try {

			AdviceCollection adviceCollection = adviceRepository.findOne(resourceId);
			if (adviceCollection != null) {
				ESAdvicesDocument esAdvicesDocument = new ESAdvicesDocument();
				BeanUtil.map(adviceCollection, esAdvicesDocument);
				esPrescriptionService.addAdvices(esAdvicesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPCNoses(ObjectId resourceId) {
		try {

			PresentingComplaintNoseCollection presentingComplaintNoseCollection = presentingComplaintNosesRepository
					.findOne(resourceId);
			if (presentingComplaintNoseCollection != null) {
				ESPresentingComplaintNoseDocument esComplaintNoseDocument = new ESPresentingComplaintNoseDocument();
				BeanUtil.map(presentingComplaintNoseCollection, esComplaintNoseDocument);
				esClinicalNotesService.addPCNose(esComplaintNoseDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPCThroat(ObjectId resourceId) {
		try {

			PresentingComplaintThroatCollection presentingComplaintThroatCollection = presentingComplaintThroatRepository
					.findOne(resourceId);
			if (presentingComplaintThroatCollection != null) {
				ESPresentingComplaintThroatDocument esComplaintThroatDocument = new ESPresentingComplaintThroatDocument();
				BeanUtil.map(presentingComplaintThroatCollection, esComplaintThroatDocument);
				esClinicalNotesService.addPCThroat(esComplaintThroatDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPCOralCavity(ObjectId resourceId) {
		try {

			PresentingComplaintOralCavityCollection presentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
					.findOne(resourceId);
			if (presentingComplaintOralCavityCollection != null) {
				ESPresentingComplaintOralCavityDocument esComplaintOralCavityDocument = new ESPresentingComplaintOralCavityDocument();
				BeanUtil.map(presentingComplaintOralCavityCollection, esComplaintOralCavityDocument);
				esClinicalNotesService.addPCOralCavity(esComplaintOralCavityDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkPCEars(ObjectId resourceId) {
		try {

			PresentingComplaintEarsCollection earsCollection = presentingComplaintEarsRepository.findOne(resourceId);
			if (earsCollection != null) {
				ESPresentingComplaintEarsDocument earsDocument = new ESPresentingComplaintEarsDocument();
				BeanUtil.map(earsCollection, earsDocument);
				esClinicalNotesService.addPCEars(earsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkNoseExam(ObjectId resourceId) {
		try {

			NoseExaminationCollection noseExaminationCollection = noseExaminationRepository.findOne(resourceId);
			if (noseExaminationCollection != null) {
				ESNoseExaminationDocument noseExaminationDocument = new ESNoseExaminationDocument();
				BeanUtil.map(noseExaminationCollection, noseExaminationDocument);
				esClinicalNotesService.addNoseExam(noseExaminationDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkOralCavityAndThroatExam(ObjectId resourceId) {
		try {

			OralCavityAndThroatExaminationCollection cavityAndThroatExamination = oralCavityThroatExaminationRepository
					.findOne(resourceId);
			if (cavityAndThroatExamination != null) {
				ESOralCavityAndThroatExaminationDocument examinationDocument = new ESOralCavityAndThroatExaminationDocument();
				BeanUtil.map(cavityAndThroatExamination, examinationDocument);
				esClinicalNotesService.addOralCavityThroatExam(examinationDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkINdirectExam(ObjectId resourceId) {
		try {

			IndirectLarygoscopyExaminationCollection indirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
					.findOne(resourceId);
			if (indirectLarygoscopyExaminationCollection != null) {
				ESIndirectLarygoscopyExaminationDocument examinationDocument = new ESIndirectLarygoscopyExaminationDocument();
				BeanUtil.map(indirectLarygoscopyExaminationCollection, examinationDocument);
				esClinicalNotesService.addIndirectLarygoscopyExam(examinationDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkNeckExam(ObjectId resourceId) {
		try {

			NeckExaminationCollection neckExaminationCollection = neckExaminationRepository.findOne(resourceId);
			if (neckExaminationCollection != null) {
				ESNeckExaminationDocument examinationDocument = new ESNeckExaminationDocument();
				BeanUtil.map(neckExaminationCollection, examinationDocument);
				esClinicalNotesService.addNeckExam(examinationDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkEarsExam(ObjectId resourceId) {
		try {

			EarsExaminationCollection earsExaminationCollection = earsExaminationRepository.findOne(resourceId);
			if (earsExaminationCollection != null) {
				ESEarsExaminationDocument examinationDocument = new ESEarsExaminationDocument();
				BeanUtil.map(earsExaminationCollection, examinationDocument);
				esClinicalNotesService.addEarsExam(examinationDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkmenstrualHistory(ObjectId resourceId) {
		try {

			MenstrualHistoryCollection historyCollection = menstrualHistoryRepository.findOne(resourceId);
			if (historyCollection != null) {
				ESMenstrualHistoryDocument historyDocument = new ESMenstrualHistoryDocument();
				BeanUtil.map(historyCollection, historyDocument);
				esClinicalNotesService.addMenstrualHistory(historyDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkObstresrticHistory(ObjectId resourceId) {
		try {

			ObstetricHistoryCollection historyCollection = obstetricHistoryRepository.findOne(resourceId);
			if (historyCollection != null) {
				ESObstetricHistoryDocument historyDocument = new ESObstetricHistoryDocument();
				BeanUtil.map(historyCollection, historyDocument);
				esClinicalNotesService.addObstetricsHistory(historyDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkLabourNotes(ObjectId resourceId) {
		try {

			LabourNoteCollection noteCollection = labourNoteRepository.findOne(resourceId);
			if (noteCollection != null) {
				EsLabourNoteDocument noteDocument = new EsLabourNoteDocument();
				BeanUtil.map(noteCollection, noteDocument);
				esDischargeSummaryService.addLabourNotes(noteDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkBabyNote(ObjectId resourceId) {
		try {

			BabyNoteCollection noteCollection = babyNoteRepository.findOne(resourceId);
			if (noteCollection != null) {
				ESBabyNoteDocument noteDocument = new ESBabyNoteDocument();
				BeanUtil.map(noteCollection, noteDocument);
				esDischargeSummaryService.addBabyNote(noteDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkOperationNote(ObjectId resourceId) {
		try {

			OperationNoteCollection noteCollection = operationNoteRepository.findOne(resourceId);
			if (noteCollection != null) {
				ESOperationNoteDocument noteDocument = new ESOperationNoteDocument();
				BeanUtil.map(noteCollection, noteDocument);
				esDischargeSummaryService.addOperationNote(noteDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkImplant(ObjectId resourceId) {
		try {

			ImplantCollection implantCollection = implantRepository.findOne(resourceId);
			if (implantCollection != null) {
				ESImplantDocument esImplantDocument = new ESImplantDocument();
				BeanUtil.map(implantCollection, esImplantDocument);
				esDischargeSummaryService.addImplant(esImplantDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkCement(ObjectId resourceId) {
		try {

			CementCollection cementCollection = cementRepository.findOne(resourceId);
			if (cementCollection != null) {
				ESCementDocument cementDocument = new ESCementDocument();
				BeanUtil.map(cementCollection, cementDocument);
				esDischargeSummaryService.addCement(cementDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkExpenseType(ObjectId resourceId) {
		try {

			ExpenseTypeCollection typeCollection = expenseTypeRepository.findOne(resourceId);
			if (typeCollection != null) {
				ESExpenseTypeDocument expenseDocument = new ESExpenseTypeDocument();
				BeanUtil.map(typeCollection, expenseDocument);
				exExpenseTypeService.addEditExpenseType(expenseDocument);
	
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkRecipe(ObjectId resourceId) {
		try {

			RecipeCollection recipeCollection = recipeRepository.findOne(resourceId);
			if (recipeCollection != null) {
				ESRecipeDocument esRecipeDocument = new ESRecipeDocument();
				BeanUtil.map(recipeCollection, esRecipeDocument);
				ESRecipeService.addRecipe(esRecipeDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkIngredient(ObjectId resourceId) {
		try {

			IngredientCollection ingredientCollection = ingredientRepository.findOne(resourceId);
			if (ingredientCollection != null) {
				ESIngredientDocument esIngredientDocument = new ESIngredientDocument();
				BeanUtil.map(ingredientCollection, esIngredientDocument);
				ESRecipeService.addIngredient(esIngredientDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public void checkNutrient(ObjectId resourceId) {
		try {

			NutrientCollection nutrientCollection = nutrientRepository.findOne(resourceId);
			if (nutrientCollection != null) {
				ESNutrientDocument esNutrientDocument = new ESNutrientDocument();
				BeanUtil.map(nutrientCollection, esNutrientDocument);
				ESRecipeService.addNutrient(esNutrientDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void checkService(ObjectId resourceId) {
		try {
			ServicesCollection services = servicesRepository.findOne(resourceId);
			if (services != null) {
				ESServicesDocument esServicesDocument = new ESServicesDocument();
				BeanUtil.map(services, esServicesDocument);
				esMasterService.addEditServices(esServicesDocument);
			}
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
	}
	
	private void checkSpeciality(ObjectId resourceId) {
		try {
			SpecialityCollection specialityCollection = specialityRepository.findOne(resourceId);
			if (specialityCollection != null) {
				ESSpecialityDocument esSpecialityDocument = new ESSpecialityDocument();
				BeanUtil.map(specialityCollection, esSpecialityDocument);
				esMasterService.addEditSpecialities(esSpecialityDocument);
			}
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
	}
	
	private void checkSymptomsDiseasesCondition(ObjectId resourceId) {
		try {
			SymptomDiseaseConditionCollection symptomDiseaseConditionCollection = symptomDiseaseConditionRepository.findOne(resourceId);
			if (symptomDiseaseConditionCollection != null) {
				ESSymptomDiseaseConditionDocument esSymptomDiseaseConditionDocument = new ESSymptomDiseaseConditionDocument();
				BeanUtil.map(symptomDiseaseConditionCollection, esSymptomDiseaseConditionDocument);
				esMasterService.addEditSymptomDiseaseConditionDocument(esSymptomDiseaseConditionDocument);
			}
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}
	}
}
