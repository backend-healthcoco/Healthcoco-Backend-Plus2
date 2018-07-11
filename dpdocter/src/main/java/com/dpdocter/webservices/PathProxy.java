package com.dpdocter.webservices;

/**
 * @author veeraj
 */
public interface PathProxy {

	public static final String HOME_URL = "/";

	public static final String BASE_URL = "/v1";

	public static final String SIGNUP_BASE_URL = BASE_URL + "/signup";

	public interface SignUpUrls {

		public static final String PATIENT_SIGNUP = "/patient";

		public static final String PATIENT_SIGNUP_MOBILE = "/patient/mobile";

		public static final String VERIFY_USER = "/verify/{tokenId}";

		public static final String CHECK_IF_USERNAME_EXIST = "/check-username-exists/{username}";

		public static final String CHECK_IF_MOBNUM_EXIST = "/check-mobnum-exists/{mobileNumber}";

		public static final String CHECK_MOBNUM_SIGNEDUP = "/{mobileNumber}/signedUp";

		public static final String CHECK_IF_EMAIL_ADDR_EXIST = "/check-email-exists/{emailaddress}";

		public static final String PATIENT_PROFILE_PIC_CHANGE = "/patientProfilePicChange";

		public static final String VERIFY_UNLOCK_PATIENT = "/patient/verifyorunlock";

		public static final String VERIFY_LOCALE = "/locale/verify/{tokenId}";

		public static final String SUBMIT_DOCTOR_CONTACT = "/submitDoctorContact";

		public static final String SUBMIT_CLINIC_CONTACT = "/submitClinicContact";

		public static final String SIGNUP_COLLECTION_BOY = "/collectionBoy";

	}

	public static final String LOGIN_BASE_URL = BASE_URL + "/login";

	public interface LoginUrls {

		public static final String LOGIN_USER = "/user";

		public static final String LOGIN_PATIENT = "/patient";

		public static final String IS_LOCATION_ADMIN = "/isLocationAdmin";

		public static final String GET_DOCTOR_LOGIN_PIN = "/pin/{doctorId}/get";

		public static final String ADD_EDIT_DOCTOR_LOGIN_PIN = "/pin/addEdit";

		public static final String CHECK_DOCTOR_LOGIN_PIN = "/pin/check";

	}

	public static final String CONTACTS_BASE_URL = BASE_URL + "/contacts";

	public interface ContactsUrls {

		public static final String DOCTOR_CONTACTS_DOCTOR_SPECIFIC = "/{type}";

		public static final String DOCTOR_CONTACTS_HANDHELD = "/handheld";

		public static final String BLOCK_CONTACT = "/{doctorId}/{patientId}/block";

		public static final String ADD_GROUP = "/group/add";

		public static final String EDIT_GROUP = "/group/{groupId}/update";

		public static final String GET_ALL_GROUPS = "/groups";

		public static final String DELETE_GROUP = "/group/{groupId}/delete";

		public static final String TOTAL_COUNT = "/totalcount";

		public static final String IMPORT_CONTACTS = "/importContacts";

		public static final String EXPORT_CONTACTS = "/exportContacts";

		public static final String ADD_GROUP_TO_PATIENT = "/patient/addgroup";

		public static final String SEND_SMS_TO_GROUP = "/group/sms";

	}

	public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";

	public interface RegistrationUrls {
		public static final String PATIENT_REGISTER = "/patient";

		public static final String USER_REGISTER_IN_CLINIC = "/user";

		public static final String EDIT_USER_IN_CLINIC = "/user/{userId}/{locationId}/edit";

		public static final String EDIT_PATIENT_PROFILE = "/patient";

		public static final String EXISTING_PATIENTS_BY_PHONE_NUM = "/existing_patients/{mobileNumber}/{doctorId}/{locationId}/{hospitalId}";

		public static final String PATIENTS_BY_PHONE_NUM = "/patients/{mobileNumber}";

		public static final String EXISTING_PATIENTS_BY_PHONE_NUM_COUNT = "/existing_patients_count/{mobileNumber}";

		public static final String GET_PATIENT_PROFILE = "/getpatientprofile/{userId}";

		public static final String ADD_REFERRENCE = "/referrence/add";

		public static final String DELETE_REFERRENCE = "/referrence/{referrenceId}/delete";

		public static final String GET_REFERRENCES = "/reference/{range}";

		public static final String UPDATE_PATIENT_INITIAL_AND_COUNTER = "/updatePatientIdGeneratorLogic/{locationId}/{patientInitial}/{patientCounter}";

		public static final String GET_PATIENT_INITIAL_COUNTER = "/getPatientInitialAndCounter/{locationId}";

		public static final String GET_CLINIC_DETAILS = "/settings/getClinicDetails/{clinicId}";

		public static final String UPDATE_CLINIC_PROFILE = "/settings/updateClinicProfile";

		public static final String UPDATE_CLINIC_PROFILE_HANDHELD = "/settings/updateClinicProfileHandheld";

		public static final String UPDATE_CLINIC_SPECIALIZATION = "/settings/updateClinicSpecialization";

		public static final String UPDATE_CLINIC_ADDRESS = "/settings/updateClinicAddress";

		public static final String UPDATE_CLINIC_TIMING = "/settings/updateClinicTiming";

		public static final String UPDATE_CLINIC_LAB_PROPERTIES = "/settings/updateLabProperties";

		public static final String CHANGE_CLINIC_LOGO = "/settings/changeClinicLogo";

		public static final String ADD_CLINIC_IMAGE = "/settings/clinicImage/add";

		public static final String DELETE_CLINIC_IMAGE = "/settings/clinicImage/{locationId}/{counter}/delete";

		public static final String GET_BLOOD_GROUP = "/settings/bloodGroup";

		public static final String ADD_PROFESSION = "/settings/profession/add";

		public static final String GET_PROFESSION = "/settings/profession";

		public static final String ADD_EDIT_ROLE = "/role/addEdit";

		public static final String GET_ROLE = "/role/{range}/{locationId}/{hospitalId}";

		public static final String DELETE_ROLE = "/role/{roleId}/delete";

		public static final String GET_USERS = "/users/{locationId}/{hospitalId}";

		public static final String ACTIVATE_DEACTIVATE_USER = "/user/{userId}/{locationId}/activate";

		public static final String ADD_FEEDBACK = "/feedback/add";

		public static final String VISIBLE_FEEDBACK = "/feedback/visible/{feedbackId}";

		public static final String GET_DOCTOR_FEEDBACK = "/feedback";

		public static final String GET_PATIENT_STATUS = "/patientStatus/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String CHANGE_PATIENT_NUMBER = "/patient/changeNumber/{oldMobileNumber}/{newMobileNumber}/{otpNumber}";

		public static final String CHECK_PATIENT_NUMBER = "/patient/checkNumber/{oldMobileNumber}/{newMobileNumber}";

		public static final String REGISTER_PATIENTS_IN_BULK = "/registerPatients/{doctorId}/{locationId}/{hospitalId}";

		public static final String UPDATE_PATIENT_INITIAL_COUNTER_ON_CLINIC_LEVEL = "/updatePIDOnClinicLevel";

		public static final String UPDATE_DOCTOR_CLINIC_PROFILE = "/updateDoctorClinicProfile";

		public static final String ADD_SUGGESTION = "/suggestion/add";

		public static final String GET_SUGGESTION = "/getSuggestion/{userId}";

		public static final String UPDATE_ROLE_COLLECTION_DATA = "updateRoleCollectionData";

		public static final String ADD_CONSENT_FORM = "/consentForm/add";

		public static final String ADD_CONSENT_FORM_DATA = "/consentForm/add/data";

		public static final String GET_CONSENT_FORM = "/consentForm";

		public static final String DELETE_CONSENT_FORM = "/consentForm/{consentFormId}/delete";

		public static final String DOWNLOAD_CONSENT_FORM = "/consentForm/download/{consentFormId}/";

		public static final String EMAIL_CONSENT_FORM = "/consentForm/{consentFormId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String UPDATE_PID = "/update/pid";

		public static final String ADD_FORM_CONTENT = "/formContent/add";

		public static final String GET_FORM_CONTENT = "/formContent/{doctorId}/{locationId}/{hospitalId}/";

		public static final String DELETE_FORM_CONTENT = "/formContent/{contentId}/delete";

		public static final String ADD_EDIT_USER_REMINDERS = "user/reminders/addEdit";

		public static final String GET_USER_REMINDERS = "user/reminders/{userId}";

		public static final String GET_USER_ADDRESS = "user/address/";

		public static final String ADD_EDIT_USER_ADDRESS = "user/address/addEdit";

		public static final String DELETE_USER_ADDRESS = "user/address/{addressId}/delete";

		public static final String DELETE_PATIENT = "/patient/{doctorId}/{locationId}/{hospitalId}/{patientId}/delete";

		public static final String GET_DELETED_PATIENT = "/patient/getDeleted/{doctorId}/{locationId}/{hospitalId}";

		public static final String UPDATE_PATIENT_NUMBER = "/patient/{doctorId}/{locationId}/{hospitalId}/{oldPatientId}";
	}

	public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

	public interface ClinicalNotesUrls {
		public static final String SAVE_CLINICAL_NOTE = "/add";

		public static final String EDIT_CLINICAL_NOTES = "/{clinicalNotesId}/update";

		public static final String DELETE_CLINICAL_NOTES = "/{clinicalNotesId}/delete";

		public static final String GET_CLINICAL_NOTES_ID = "/{clinicalNotesId}/view";

		public static final String GET_CLINICAL_NOTES_PATIENT_ID = "/{patientId}";

		public static final String GET_CLINIC_NOTES_COUNT = "/getClinicalNotesCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

		public static final String ADD_COMPLAINT = "/complaint/add";

		public static final String ADD_OBSERVATION = "/observation/add";

		public static final String ADD_INVESTIGATION = "/investigation/add";

		public static final String ADD_DIAGNOSIS = "/diagnosis/add";

		public static final String ADD_NOTES = "/notes/add";

		public static final String ADD_DIAGRAM = "/diagram/add";

		public static final String ADD_PRESENT_COMPLAINT = "/presentComplaint/add";

		public static final String ADD_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/add";

		public static final String ADD_GENERAL_EXAM = "/generalExam/add";

		public static final String ADD_SYSTEM_EXAM = "/systemExam/add";

		public static final String ADD_MENSTRUAL_HISTORY = "/menstrualHistory/add";

		public static final String ADD_OBSTETRICS_HISTORY = "/obstetricHistory/add";

		public static final String ADD_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/add";

		public static final String ADD_INDICATION_OF_USG = "/indicationOfUSG/add";

		public static final String ADD_PA = "/pa/add";

		public static final String ADD_PV = "/pv/add";

		public static final String ADD_PS = "/ps/add";

		public static final String ADD_X_RAY_DETAILS = "/xRayDetails/add";

		public static final String ADD_ECG_DETAILS = "/ecgDetails/add";

		public static final String ADD_ECHO = "/echo/add";

		public static final String ADD_HOLTER = "/holter/add";

		public static final String ADD_PC_NOSE = "/pcnose/add";

		public static final String ADD_PC_EARS = "/pcears/add";

		public static final String ADD_PC_THROAT = "/pcthroat/add";

		public static final String ADD_PC_ORAL_CAVITY = "/pcoralCavity/add";

		public static final String ADD_NECK_EXAM = "/neckExam/add";

		public static final String ADD_NOSE_EXAM = "/noseExam/add";

		public static final String ADD_EARS_EXAM = "/earsExam/add";

		public static final String ADD_INDIRECT_LARYGOSCOPY_EXAM = "/indirectLarygoscopyExam/add";

		public static final String ADD_ORAL_CAVITY_THROAT_EXAM = "/oralCavityThroatExam/add";

		public static final String ADD_PROCEDURE_NOTE = "/procedureNote/add";

		public static final String DELETE_COMPLAINT = "/complaint/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_OBSERVATION = "/observation/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_INVESTIGATION = "/investigation/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_DIAGNOSIS = "/diagnosis/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_NOTE = "/notes/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_DIAGRAM = "/diagram/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_CINICAL_ITEMS = "/{type}/{range}";

		public static final String EMAIL_CLINICAL_NOTES = "/{clinicalNotesId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DOWNLOAD_CLINICAL_NOTES = "/download/{clinicalNotesId}";

		public static final String UPDATE_QUERY_CLINICAL_NOTES = "/updateQuery";

		public static final String DELETE_PRESENT_COMPLAINT = "/presentComplaint/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_GENERAL_EXAM = "/generalExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_SYSTEM_EXAM = "/systemExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_MENSTRUAL_HISTORY = "/menstrualHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_OBSTETRIC_HISTORY = "/obstetricHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_INDICATION_OF_USG = "/presentIndicationOfUSG/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PA = "/pa/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PV = "/pv/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PS = "/ps/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_X_RAY_DETAILS = "/xRayDetails/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_ECG_DETAILS = "/ecgDetails/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_ECHO = "/echo/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_HOLTER = "/holter/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PROCEDURE_NOTE = "/procedureNote/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_PC_NOSE = "/pcNose/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_PC_EARS = "/pcEars/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_PC_ORAL_CAVITY = "/pcOralCavity/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_PC_THROAT = "/pcThroat/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_NECK_EXAM = "/neckExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_EARS_EXAM = "/earsExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_NOSE_EXAM = "/noseExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_ORAL_CAVITY_THROAT_EXAM = "/oralCavityThroatExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_INDIRECT_LARYGOSCOPY_EXAM = "/indirectLarygoscopyExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_EDIT_EYE_OBSERVATION = "/eyeObservation/add";

		public static final String DELETE_EYE_OBSERVATION = "/eyeObservation/{id}/delete";

		public static final String GET_EYE_OBSERVATIONS = "/eyeObservations";

		public static final String GET_DIAGNOSES_BY_SPECIALITY = "/getDiagnosesBySpeciality";

		public static final String EMAIL_CLINICAL_NOTES_WEB = "/{clinicalNotesId}/{emailAddress}/mail";

		public static final String DOWNLOAD_MULTIPLE_CLINICAL_NOTES = "/download";

		public static final String EMAIL_MULTIPLE_CLINICAL_NOTES = "/email";

	}

	public static final String FORGOT_PASSWORD_BASE_URL = BASE_URL + "/forgotPassword";

	public interface ForgotPasswordUrls {
		public static final String CHECK_LINK_IS_ALREADY_USED = "/checkLink/{userId}";

		public static final String FORGOT_PASSWORD_DOCTOR = "/forgotPasswordDoctor";

		public static final String FORGOT_PASSWORD_PATIENT = "/forgotPasswordPatient";

		public static final String RESET_PASSWORD_PATIENT = "/resetPasswordPatient";

		public static final String RESET_PASSWORD = "/resetPassword";

		public static final String RESET_PASSWORD_PHARMACY = "/resetPasswordPharmacy";

		public static final String FORGOT_USERNAME = "/forgot-username";

		public static final String RESET_PASSWORD_CB = "/resetPasswordCB";
	}

	public static final String RECORDS_BASE_URL = BASE_URL + "/records";

	public interface RecordsUrls {
		public static final String ADD_RECORDS = "/add";

		public static final String GET_RECORD_BY_ID = "/{recordId}/view";

		public static final String TAG_RECORD = "/tagrecord";

		public static final String SEARCH_RECORD = "/search";

		public static final String GET_RECORDS_PATIENT_ID = "/{patientId}";

		public static final String GET_RECORD_COUNT = "/getRecordCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

		public static final String CREATE_TAG = "/createtag";

		public static final String GET_ALL_TAGS = "/getalltags/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_PATIENT_EMAIL_ADD = "/getpatientemailaddr/{patientId}";

		public static final String EMAIL_RECORD = "/{recordId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DELETE_RECORD = "/{recordId}/delete";

		public static final String DOWNLOAD_RECORD = "/download/{recordId}/{fileId}";

		public static final String DELETE_TAG = "/tagrecord/{tagid}/delete";

		public static final String GET_FLEXIBLE_COUNTS = "/getFlexibleCounts";

		public static final String EDIT_RECORD = "/{recordId}/update";

		public static final String CHANGE_LABEL_AND_DESCRIPTION_RECORD = "/changeLabelAndDescription";

		public static final String ADD_RECORDS_MULTIPART = "/addMultipart";

		public static final String SAVE_RECORDS_IMAGE = "/saveImage";

		public static final String CHANGE_RECORD_STATE = "/{recordId}/{recordsState}/changeState";

		public static final String ADD_USER_RECORDS = "/user/add";

		public static final String UPLOAD_USER_RECORD_FILE = "/upload/file/";

		public static final String GET_USER_RECORD_BY_ID = "user/{recordId}/view";

		public static final String GET_USER_RECORDS = "/user/get";

		public static final String GET_USER_RECORDS_ALLOWANCE = "user/allowance";

		public static final String DELETE_OR_HIDE_USER_RECORD = "user/{recordId}/delete";

		public static final String DELETE_RECORDS_FILE = "file/{recordId}/delete";

		public static final String DELETE_USER_RECORDS_FILE = "user/file/{recordId}/delete";

		public static final String UPDATE_RECORDS_DATA = "/updateData";

		public static final String SHARE_USER_RECORDS_WITH_PATIENT = "user/file/{recordId}/{patientId}/share";

		public static final String SHARE_RECORD_WITH_PATIENT = "/{recordId}/share";

		public static final String GET_RECORDS_DOCTOR_ID = "getByDoctorId/{doctorId}";

	}

	public static final String PRESCRIPTION_BASE_URL = BASE_URL + "/prescription";

	public interface PrescriptionUrls {
		public static final String GET_PRESCRIPTION = "/{prescriptionId}/view";

		public static final String GET_PRESCRIPTION_PATIENT_ID = "/{patientId}";

		public static final String ADD_DRUG = "/drug/add";

		public static final String EDIT_DRUG = "/drug/{drugId}/update";

		public static final String DELETE_DRUG = "/drug/{drugId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_DRUG_ID = "/drug/{drugId}";

		public static final String DELETE_GLOBAL_DRUG = "/drug/{drugId}/delete";

		public static final String ADD_LAB_TEST = "/labTest/add";

		public static final String EDIT_LAB_TEST = "/labTest/{labTestId}/update";

		public static final String DELETE_LAB_TEST = "/labTest/{labTestId}/{locationId}/{hospitalId}/delete";

		public static final String GET_LAB_TEST_BY_ID = "/labTest/{labTestId}";

		public static final String DELETE_GLOBAL_LAB_TEST = "/labTest/{labTestId}/delete";

		public static final String GET_DIAGNOSTIC_TEST = "/diagnosticTest";

		public static final String ADD_EDIT_DIAGNOSTIC_TEST = "/diagnosticTest/addEdit";

		public static final String DELETE_DIAGNOSTIC_TEST = "/diagnosticTest/{diagnosticTestId}/{locationId}/{hospitalId}/delete";

		public static final String GET_DIAGNOSTIC_TEST_BY_ID = "/diagnosticTest/{diagnosticTestId}";

		public static final String DELETE_GLOBAL_DIAGNOSTIC_TEST = "/diagnosticTest/{diagnosticTestId}/delete";

		public static final String GET_PRESCRIPTION_ITEMS = "/{type}/{range}";

		public static final String ADD_TEMPLATE = "/template/add";

		public static final String ADD_TEMPLATE_HANDHELD = "/templateHandheld/add";

		public static final String EDIT_TEMPLATE = "/template/{templateId}/update";

		public static final String DELETE_TEMPLATE = "/template/{templateId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_TEMPLATE_TEMPLATE_ID = "/template/{templateId}/{doctorId}/{locationId}/{hospitalId}/view";

		public static final String GET_TEMPLATE = "/templates";

		public static final String ADD_PRESCRIPTION = "/add";

		public static final String ADD_PRESCRIPTION_HANDHELD = "/prescriptionHandheld/add";

		public static final String EDIT_PRESCRIPTION = "/{prescriptionId}/update";

		public static final String DELETE_PRESCRIPTION = "/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{patientId}/delete";

		public static final String GET_PRESCRIPTION_COUNT = "/count/{doctorId}/{patientId}/{locationId}/{hospitalId}";

		public static final String ADD_DRUG_TYPE = "/drugType/add";

		public static final String EDIT_DRUG_TYPE = "/drugType/{drugTypeId}/update";

		public static final String DELETE_DRUG_TYPE = "/drugType/{drugTypeId}/delete";

		public static final String ADD_DRUG_STRENGTH = "/drugStrength/add";

		public static final String EDIT_DRUG_STRENGTH = "/drugStrength/{drugStrengthId}/update";

		public static final String DELETE_DRUG_STRENGTH = "/drugStrength/{drugStrengthId}/delete";

		public static final String ADD_DRUG_DOSAGE = "/drugDosage/add";

		public static final String EDIT_DRUG_DOSAGE = "/drugDosage/{drugDosageId}/update";

		public static final String DELETE_DRUG_DOSAGE = "/drugDosage/{drugDosageId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_DRUG_DIRECTION = "/drugDirection/add";

		public static final String EDIT_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/update";

		public static final String DELETE_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_DRUG_DURATION_UNIT = "/drugDurationUnit/add";

		public static final String EDIT_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/update";

		public static final String DELETE_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String EMAIL_PRESCRIPTION = "/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String SMS_PRESCRIPTION = "/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/sms";

		public static final String CHECK_PRESCRIPTION_EXISTS_FOR_PATIENT = "/prescriptionExist/{uniqueEmrId}";

		public static final String DOWNLOAD_PRESCRIPTION = "/download/{prescriptionId}";

		public static final String ADD_DRUG_TO_DOCTOR = "/drug/{drugId}/{doctorId}/{locationId}/{hospitalId}/makeFavourite";

		public static final String ADD_ADVICE = "/advice";

		public static final String DELETE_ADVICE = "/advice/{adviceId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_CUSTOM_DRUG_TO_FAV = "/makeCustomDrugFavourite";

		public static final String ADD_FAVOURITE_DRUG = "/favouriteDrug/add";

		public static final String ADD_GENERIC_NAME_IN_DRUGS = "/addGenericNameInDrugs";

		public static final String DRUGS_INTERACTION = "/drugs/interaction/";

		public static final String ADD_GENERIC_CODES_WITH_REACTION = "/genericCodes/";

		public static final String GET_GENERIC_CODES_WITH_REACTION = "/genericCodes/get";

		public static final String ADD_FAVOURITES_TO_DRUGS = "favourites/addToDrug";

		public static final String ADD_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/";

		public static final String UPLOAD_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/upload";

		public static final String DELETE_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/delete";

		public static final String ADD_EYE_PRESCRPTION = "eyePrescription/add";

		public static final String EDIT_EYE_PRESCRPTION = "eyePrescription/edit";

		public static final String GET_EYE_PRESCRPTION_BY_ID = "eyePrescription/{id}/get";

		public static final String GET_EYE_PRESCRPTIONS = "eyePrescription/getAll";

		public static final String EMAIL_EYE_PRESCRIPTION = "/eye/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DOWNLOAD_EYE_PRESCRIPTION = "/eye/download/{prescriptionId}";

		public static final String SMS_EYE_PRESCRIPTION = "eye/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/sms";

		public static final String DELETE_EYE_PRESCRIPTION = "eye/{prescriptionId}/{doctorId}/{locationId}/{hospitalId}/{patientId}/delete";

		public static final String GET_CUSTOM_DRUGS = "/getCustomDrugs";

		public static final String ADD_EDIT_INSTRUCTIONS = "/addEditInstructions";

		public static final String GET_INSTRUCTIONS = "/getInstructions";

		public static final String DELETE_INSTRUCTIONS = "/deleteInstructions";

		public static final String UPDATE_GENERIC_CODES = "/genericCodes/Update";

		public static final String GET_DRUG_SUBSTITUTES = "/drugs/getSubstitutes";

		public static final String EMAIL_PRESCRIPTION_WEB = "/{prescriptionId}/{emailAddress}/mail";

		public static final String SMS_PRESCRIPTION_WEB = "/{prescriptionId}/{mobileNumber}/sms";

		public static final String EMAIL_EYE_PRESCRIPTION_WEB = "/eye/{prescriptionId}/{emailAddress}/mail";

		public static final String SMS_EYE_PRESCRIPTION_WEB = "eye/{prescriptionId}/{mobileNumber}/sms";

		public static final String DELETE_PRESCRIPTION_WEB = "/{prescriptionId}/delete";

		public static final String UPDATE_DRUG_RANKING_ON_BASIS_OF_RANKING = "drugs/updateRanking";

		public static final String UPLOAD_DRUGS = "drugs/upload";

		public static final String UPDATE_DRUG_INTERACTION = "drugInteraction/update";

		public static final String ADD_NUTRITION_REFERRAL = "/addNutritionReferral";
	}

	public static final String HISTORY_BASE_URL = BASE_URL + "/history";

	public interface HistoryUrls {
		public static final String ADD_DISEASE = "/disease/add";

		public static final String EDIT_DISEASE = "/disease/{diseaseId}/update";

		public static final String DELETE_DISEASE = "/disease/{diseaseId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_DISEASES = "/diseases/{range}";

		public static final String ADD_REPORT_TO_HISTORY = "/report/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_CLINICAL_NOTES_TO_HISTORY = "/clinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_PRESCRIPTION_TO_HISTORY = "/prescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_PATIENT_TREATMENT_TO_HISTORY = "/patientTreament/{treatmentId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_SPECIAL_NOTES = "/addSpecialNotes";

		public static final String ASSIGN_MEDICAL_HISTORY = "/assignMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String ASSIGN_FAMILY_HISTORY = "/assignFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String ASSIGN_PERSONAL_HISTORY = "/assignPersonalHistory";

		public static final String ASSIGN_DRUG_ALLERGIES = "/assignDrugsAndAllergies";

		public static final String HANDLE_MEDICAL_HISTORY = "/medicalHistory";

		public static final String GET_MEDICAL_AND_FAMILY_HISTORY = "/getMedicalAndFamilyHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String HANDLE_FAMILY_HISTORY = "/familyHistory";

		public static final String REMOVE_REPORTS = "/removeReports/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_CLINICAL_NOTES = "/removeClinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_PRESCRIPTION = "/removePrescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_PATIENT_TREATMENT = "/removePatientTreatment/{treatmentId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_MEDICAL_HISTORY = "/removeMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_FAMILY_HISTORY = "/removeFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_PATIENT_HISTORY_OTP_VERIFIED = "/getPatientHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}/{otpVerified}";

		public static final String GET_PATIENT_HISTORY_OTP_VERIFIED_WEB = "/getPatientHistory";

		public static final String GET_PATIENT_HISTORY = "/{patientId}";

		public static final String MAIL_MEDICAL_DATA = "mailMedicalData";

		public static final String GET_HISTORY = "/getHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		// public static final String ADD_VISITS_TO_HISTORY =
		// "/visits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";
		//
		// public static final String REMOVE_VISITS =
		// "/removeVisits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_MULTIPLE_DATA = "getMultipleData";

		public static final String SUBMIT_BIRTH_HITORY = "/submitBirthHistory";

		public static final String GET_BIRTH_HISTORY = "/getBirthHistory/{patientId}";
	}

	public static final String DOCTOR_PROFILE_URL = BASE_URL + "/doctorProfile";

	public interface DoctorProfileUrls {
		public static final String ADD_EDIT_NAME = "/addEditName";

		public static final String ADD_EDIT_EXPERIENCE = "/addEditExperience";

		public static final String ADD_EDIT_CONTACT = "/addEditContact";

		public static final String ADD_EDIT_EDUCATION = "/addEditEducation";

		public static final String ADD_EDIT_SPECIALITY = "/addEditSpeciality";

		public static final String ADD_EDIT_ACHIEVEMENT = "/addEditAchievement";

		public static final String ADD_EDIT_PROFESSIONAL_STATEMENT = "/addEditProfessionalStatement";

		public static final String ADD_EDIT_REGISTRATION_DETAIL = "/addEditRegistrationDetail";

		public static final String ADD_EDIT_EXPERIENCE_DETAIL = "/addEditExperienceDetail";

		public static final String ADD_EDIT_PROFILE_PICTURE = "/addEditProfilePicture";

		public static final String ADD_EDIT_COVER_PICTURE = "/addEditCoverPicture";

		public static final String ADD_EDIT_PROFESSIONAL_MEMBERSHIP = "/addEditProfessionalMembership";

		public static final String ADD_EDIT_MEDICAL_COUNCILS = "/addEditMedicalCouncils";

		public static final String GET_MEDICAL_COUNCILS = "/getMedicalCouncils";

		public static final String INSERT_PROFESSIONAL_MEMBERSHIPS = "/insertProfessionalMemberships";

		public static final String GET_PROFESSIONAL_MEMBERSHIPS = "/getProfessionalMemberships";

		public static final String ADD_EDIT_CLINIC_PROFILE = "/clinicProfile/addEdit";

		public static final String ADD_EDIT_APPOINTMENT_NUMBERS = "/clinicProfile/addEditAppointmentNumbers";

		public static final String ADD_EDIT_VISITING_TIME = "/clinicProfile/addEditVisitingTime";

		public static final String ADD_EDIT_CONSULTATION_FEE = "/clinicProfile/addEditConsultationFee";

		public static final String ADD_EDIT_APPOINTMENT_SLOT = "/clinicProfile/addEditAppointmentSlot";

		public static final String ADD_EDIT_GENERAL_INFO = "/clinicProfile/addEditGeneralInfo";

		public static final String GET_SPECIALITIES = "/getSpecialities";

		public static final String GET_EDUCATION_INSTITUTES = "/getEducationInstitutes";

		public static final String GET_DOCTOR_PROFILE = "/{doctorId}/view";

		public static final String GET_DOCTOR_PROFILE_BY_SLUG_URL = "/{slugURL}/{userUId}/view";

		public static final String GET_EDUCATION_QUALIFICATIONS = "/getEducationQualifications";

		public static final String ADD_EDIT_MULTIPLE_DATA = "/addEditMultipleData";

		public static final String ADD_EDIT_FACILITY = "clinicProfile/editFacility";

		public static final String ADD_EDIT_GENDER = "addEditGender";

		public static final String ADD_EDIT_DOB = "addEditDOB";

		public static final String SET_RECOMMENDATION = "/recommendation/{doctorId}/{locationId}/{patientId}";

		public static final String SET_CLINIC_RECOMMENDATION = "/recommendation/{locationId}/{patientId}";

		public static final String GET_PATIENT = "/getPatient";

		public static final String GET_LABS_WITH_REPORTS_COUNT = "/getLabsWithReportCount/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_REPORTS_FOR_SPECIFIC_DOCTOR = "/getReports/{prescribedByDoctorId}/{prescribedByLocationId}/{prescribedByHospitalId}";

		public static final String GET_DOCTOR_STATS = "/{doctorId}/getStats";

		public static final String UPDATE_EMR_SETTING = "/{doctorId}/updateEMRSetting";

		public static final String UPDATE_PRESCRIPTION_SMS = "/{doctorId}/prescriptionSMS";

		public static final String ADD_EDIT_SEO = "/addEditSEO";

		public static final String UPDATE_SAVE_TO_INVENTORY = "/{doctorId}/saveToInventory";

		public static final String UPDATE_SHOW_INVENTORY = "/{doctorId}/showInventory";

		public static final String UPDATE_SHOW_INVENTORY_COUNT = "/{doctorId}/showInventoryCount";

	}

	public static final String PATIENT_VISIT_BASE_URL = BASE_URL + "/patientVisit";

	public interface PatientVisitUrls {

		public static final String ADD_MULTIPLE_DATA = "/add";

		public static final String EMAIL = "/email/{visitId}/{emailAddress}";

		public static final String GET_VISIT = "/{visitId}";

		public static final String GET_VISITS = "/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_VISITS_FOR_WEB = "/get";

		public static final String GET_VISITS_HANDHELD = "/handheld/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String DELETE_VISITS = "/{visitId}/delete";

		public static final String SMS_VISITS = "/{visitId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/sms";

		public static final String DOWNLOAD_PATIENT_VISIT = "/download/{visitId}";

		public static final String SMS_VISITS_WEB = "/{visitId}/{mobileNumber}/sms";

		public static final String GET_PATIENT_LAST_VISIT = "/last/{doctorId}/{locationId}/{hospitalId}/{patientId}";

	}

	public static final String ACCESS_CONTROL_BASE_URL = BASE_URL + "/accessControl";

	public interface AccessControlUrls {
		public static final String GET_ACCESS_CONTROLS = "getAccessControls/{roleOrUserId}/{locationId}/{hospitalId}";

		public static final String SET_ACCESS_CONTROLS = "setAccessControls";
	}

	public static final String ISSUE_TRACK_BASE_URL = BASE_URL + "/issueTrack";

	public interface IssueTrackUrls {

		public static final String RAISE_ISSUE = "add";

		public static final String DELETE_ISSUE = "/{issueId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String UPDATE_STATUS_DOCTOR_SPECIFIC = "/{issueId}/{status}/{doctorId}/{locationId}/{hospitalId}/update";

		public static final String UPDATE_STATUS_ADMIN = "/{issueId}/{status}/update";
	}

	public static final String PRINT_SETTINGS_BASE_URL = BASE_URL + "/printSettings";

	public interface PrintSettingsUrls {

		public static final String SAVE_SETTINGS_DEFAULT_DATA = "saveDefault";

		public static final String SAVE_PRINT_SETTINGS = "add";

		public static final String DELETE_PRINT_SETTINGS = "/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_PRINT_SETTINGS = "/{printFilter}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_GENERAL_NOTES = "/getGeneralNotes/{doctorId}/{locationId}/{hospitalId}";

	}

	public static final String SOLR_CLINICAL_NOTES_BASEURL = BASE_URL + "/solr/clinicalNotes";

	public interface SolrClinicalNotesUrls {
		public static final String SEARCH_COMPLAINTS = "searchComplaints/{range}";

		public static final String SEARCH_DIAGNOSES = "searchDiagnoses/{range}";

		public static final String SEARCH_NOTES = "searchNotes/{range}";

		public static final String SEARCH_DIAGRAMS = "searchDiagrams/{range}";

		public static final String SEARCH_DIAGRAMS_BY_SPECIALITY = "searchDiagramsBySpeciality/{searchTerm}";

		public static final String SEARCH_INVESTIGATIONS = "searchInvestigations/{range}";

		public static final String SEARCH_OBSERVATIONS = "searchObservations/{range}";

		public static final String SEARCH_PRESENT_COMPLAINT = "searchPresentComplaint/{range}";

		public static final String SEARCH_PRESENT_COMPLAINT_HISTORY = "searchPresentComplaintHistory/{range}";

		public static final String SEARCH_PROVISIONAL_DIAGNOSIS = "searchProvisionalDiagnosis/{range}";

		public static final String SEARCH_GENERAL_EXAM = "searchGeneralExam/{range}";

		public static final String SEARCH_SYSTEM_EXAM = "searchSystemExam/{range}";

		public static final String SEARCH_OBSTETRIC_HISTORY = "searchObstetricHistory/{range}";

		public static final String SEARCH_MENSTRUAL_HISTORY = "searchMenstrualHistory/{range}";

		public static final String SEARCH_INDICATION_OF_USG = "searchIndicationOfUSG/{range}";

		public static final String SEARCH_PA = "pa/{range}";

		public static final String SEARCH_PV = "pv/{range}";

		public static final String SEARCH_PS = "ps/{range}";

		public static final String SEARCH_X_RAY_DETAILS = "xRayDetails/{range}";

		public static final String SEARCH_ECG_DETAILS = "ecgDetails/{range}";

		public static final String SEARCH_ECHO = "echo/{range}";

		public static final String SEARCH_HOLTER = "holter/{range}";

		public static final String SEARCH_PROCEDURE_NOTE = "procedureNote/{range}";

		public static final String SEARCH_PC_NOSE = "pcNose/{range}";

		public static final String SEARCH_PC_EARS = "pcEars/{range}";

		public static final String SEARCH_PC_THROAT = "pcThroat/{range}";

		public static final String SEARCH_PC_ORAL_CAVITY = "pcOralCavity/{range}";

		public static final String SEARCH_NECK_EXAM = "neckExam/{range}";

		public static final String SEARCH_NOSE_EXAM = "noseExam/{range}";

		public static final String SEARCH_EARS_EXAM = "earsExam/{range}";

		public static final String SEARCH_ORAL_CAVITY_THROAT_EXAM = "oralCavityThroatExam/{range}";

		public static final String SEARCH_INDIRECT_LARYGOSCOPY_EXAM = "indirectLarygoscopyExam/{range}";

	}

	public static final String SOLR_PRESCRIPTION_BASEURL = BASE_URL + "/solr/prescription";

	public interface SolrPrescriptionUrls {

		public static final String SEARCH_DRUG = "searchDrug/{range}";

		public static final String SEARCH_LAB_TEST = "searchLabTest/{range}";

		public static final String SEARCH_DIAGNOSTIC_TEST = "searchDiagnosticTest/{range}";

		public static final String SEARCH_ADVICE = "searchAdvice/{range}";
	}

	public static final String SOLR_REGISTRATION_BASEURL = BASE_URL + "/solr/registration";

	public interface SolrRegistrationUrls {
		public static final String SEARCH_PATIENT = "searchPatient/{locationId}/{hospitalId}/{searchTerm}";

		public static final String SEARCH_PATIENT_ADV = "searchPatient";
	}

	public static final String APPOINTMENT_BASE_URL = BASE_URL + "/appointment";

	public interface AppointmentUrls {

		public static final String ADD_COUNTRY = "/country/add";

		public static final String ADD_STATE = "/state/add";

		public static final String ADD_CITY = "/city/add";

		public static final String ACTIVATE_DEACTIVATE_CITY = "/activateCity/{cityId}";

		public static final String GET_CITY = "/cities";

		public static final String GET_COUNTRIES = "/countries";

		public static final String GET_STATES = "/states";

		public static final String GET_CITY_ID = "/getCity/{cityId}";

		public static final String ADD_LANDMARK_LOCALITY = "/landmarkLocality/add";

		public static final String GET_CLINIC = "/clinic/{locationId}";

		public static final String GET_LAB = "/lab/{locationId}";

		public static final String ADD_EDIT_EVENT = "/event";

		public static final String CANCEL_EVENT = "/event/{eventId}/{doctorId}/{locationId}/cancel";

		public static final String GET_PATIENT_APPOINTMENTS = "/patient";

		public static final String GET_PATIENT_LAST_APPOINTMENT = "/patient/last/{patientId}/{locationId}";

		public static final String GET_TIME_SLOTS = "getTimeSlots/{doctorId}/{locationId}/{date}";

		public static final String SEND_REMINDER_TO_PATIENT = "/sendReminder/patient/{appointmentId}";

		public static final String ADD_PATIENT_IN_QUEUE = "/queue/add";

		public static final String REARRANGE_PATIENT_IN_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}/{patientId}/{appointmentId}/{sequenceNo}/rearrange";

		public static final String GET_PATIENT_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_APPOINTMENT_ID = "/{appointmentId}/view";

		public static final String PATIENT_COUNT = "/patientCount/{locationId}";

		public static final String GET_DOCTORS = "/getDoctorsWithAppointmentCount/{locationId}";

		public static final String CHANGE_STATUS_IN_APPOINTMENT = "/changeStatus/{doctorId}/{locationId}/{hospitalId}/{patientId}/{appointmentId}/{status}";

		public static final String ADD_CUSTOM_APPOINTMENT = "/custom/add";

		public static final String GET_CUSTOM_APPOINTMENT_LIST = "/custom/get";

		public static final String GET_CUSTOM_APPOINTMENT_BY_ID = "/custom/{appointmentId}/get";

		public static final String GET_CUSTOM_APPOINTMENT_AVG_DETAIL = "/custom/getAVGdetail";

		public static final String DELETE_CUSTOM_APPOINTMENT = "/custom/{appointmentId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_CLINIC_BY_SLUG_URL = "/clinic/{slugUrl}/web";

		public static final String GET_LAB_BY_SLUG_URL = "/lab/{slugUrl}/web";

		public static final String UPDATE_APPOINTMENT_DOCTOR = "/updateDoctor/{appointmentId}/{doctorId}";

		public static final String DOWNLOAD_PATIENT_CARD = "/downloadpatientCard";

		public static final String DOWNLOAD_APPOINTMENT_CALENDER = "/calendar/{locationId}/{hospitalId}/download";

		public static final String GET_EVENTS = "/event/get";

		public static final String GET_EVENT_BY_ID = "/event/{eventId}";
	}

	public static final String PATIENT_TREATMENT_BASE_URL = BASE_URL + "/treatment";

	public interface PatientTreatmentURLs {

		public static final String ADD_EDIT_SERVICE = "/service/add";

		public static final String ADD_EDIT_SERVICE_COST = "/serviceCost/add";

		public static final String DELETE_SERVICE = "/service/{treatmentServiceId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_SERVICE_COST = "/serviceCost/{treatmentServiceId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_SERVICES = "/{type}/{range}";

		public static final String ADD_EDIT_PATIENT_TREATMENT = "/add";

		public static final String CHANGE_SERVICE_STATUS = "/{treatmentId}/{doctorId}/{locationId}/{hospitalId}/changeStatus";

		public static final String DELETE_PATIENT_TREATMENT = "/{treatmentId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_PATIENT_TREATMENT_BY_ID = "/{treatmentId}/view";

		public static final String GET_PATIENT_TREATMENT_PATIENT_ID = "/{patientId}";

		public static final String EMAIL_PATIENT_TREATMENT = "/{treatmentId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DOWNLOAD_PATIENT_TREATMENT = "/download/{treatmentId}";

		public static final String GENERATE_TREATMENT_CODE = "/treatmentCode/add";

		public static final String ADD_FAVOURITES_TO_TREATMENT_SERVICES = "favourites/addToTreatmentService";

		public static final String ADD_TREATMENT_SERVICES_TO_DOCTOR = "/service/{serviceId}/{doctorId}/{locationId}/{hospitalId}/makeFavourite";

		public static final String GET_TREATMENT_SERVICES_BY_SPECIALITY = "/getServicesBySpeciality";

		public static final String EMAIL_PATIENT_TREATMENT_WEB = "/{treatmentId}/{emailAddress}/mail";

		public static final String DELETE_PATIENT_TREATMENT_WEB = "/{treatmentId}/delete";

	}

	public static final String SOLR_CITY_BASE_URL = BASE_URL + "/solr/city";

	public interface SolrCityUrls {

		public static final String SEARCH_LOCATION = "searchLocation";

		public static final String SEARCH_LOCATION_WEB = "searchLocation/web";
	}

	public static final String SMS_BASE_URL = BASE_URL + "/sms";

	public interface SMSUrls {

		public static final String SEND_SMS = "/send";

		public static final String GET_SMS_DETAILS = "/getDetails";

		public static final String UPDATE_DELIVERY_REPORTS = "/updateDeliveryReports";

		public static final String ADD_NUMBER = "/addNumber/{mobileNumber}";

		public static final String DELETE_NUMBER = "/deleteNumber/{mobileNumber}";

		public static final String ADD_EDIT_SMS_FORMAT = "/format/add";

		public static final String GET_SMS_FORMAT = "/format/{doctorId}/{locationId}/{hospitalId}";

		public static final String SEND_DOWNLOAD_APP_SMS_TO_PATIENT = "/sendDownloadSmsToPatient";

		public static final String SEND_BULK_SMS = "bulk/{message}";

	}

	public static final String EMAIL_TRACK_BASE_URL = BASE_URL + "/email";

	public interface EmailTrackUrls {

	}

	public static final String EMAIL_BASE_URL = BASE_URL + "/mail";

	public interface EmailUrls {
		public static final String UNSUBSCRIBE_MAIL = "/unsubscribe";

	}

	public static final String OTP_BASE_URL = BASE_URL + "/otp";

	public interface OTPUrls {

		public static final String OTP_GENERATOR = "/{doctorId}/{locationId}/{hospitalId}/{patientId}/generate";

		public static final String OTP_GENERATOR_MOBILE = "/{mobileNumber}";

		public static final String VERIFY_OTP = "/{doctorId}/{locationId}/{hospitalId}/{patientId}/{otpNumber}/verify";

		public static final String VERIFY_OTP_MOBILE = "/{mobileNumber}/{otpNumber}/verify";

	}

	public static final String SOLR_APPOINTMENT_BASE_URL = BASE_URL + "/solr/appointment";

	public interface SolrAppointmentUrls {

		public static final String SEARCH = "/search";

		public static final String GET_DOCTORS = "/doctors";

		public static final String GET_PHARMACIES = "/pharmacies";

		public static final String GET_LABS = "/labs";

		public static final String ADD_SPECIALITY = "/addSpecialization";

		public static final String SEND_SMS_TO_DOCTOR = "/smsToDoctor";

		public static final String SEND_SMS_TO_PHARMACY = "/smsToPharmacy";

		public static final String GET_DOCTOR_WEB = "/doctors/web";

		public static final String GET_PHARMACIES_WEB = "/pharmacies/web";

		public static final String GET_LABS_WEB = "/labs/web";

		public static final String GET_DOCTORS_CARD = "/doctorsCard";
	}

	public static final String GENERAL_TESTS_URL = BASE_URL + "/tests";

	public static final String SOLR_MASTER_BASE_URL = BASE_URL + "/solr/master";

	public interface SolrMasterUrls {

		public static final String SEARCH_REFERENCE = "/reference/{range}";

		public static final String SEARCH_DISEASE = "/disease/{range}";

		public static final String SEARCH_BLOOD_GROUP = "/bloodGroup";

		public static final String SEARCH_PROFESSION = "/profession";

		public static final String SEARCH_MEDICAL_COUNCIL = "/medicalCouncil";

		public static final String SEARCH_EDUCATION_INSTITUTE = "/educationInstitute";

		public static final String SEARCH_EDUCATION_QUALIFICATION = "/educationQualification";

		public static final String SEARCH_PROFESSIONAL_MEMBERSHIP = "/professionalMembership";

		public static final String SEARCH_SPECIALITY = "/speciality";
	}

	public static final String ADMIN_BASE_URL = BASE_URL;

	public interface AdminUrls {

		public static final String ADD_RESUMES = "/resumes/add";

		public static final String ADD_CONTACT_US = "/contactUs/add";

		public static final String SEND_APP_LINK = "/sendLink";

		public static final String Add_SUBCRIPTION_DETAIL = "/addSubscriptionDetails";

		public static final String SEND_BIRTHDAY_WISH = "/sendwish";

		public static final String DISCARD_DUPLICATE_CLINICAL_ITEMS = "clinicalItems/duplicate/discard/{doctorId}";

	}

	public static final String PUSH_NOTIFICATION_BASE_URL = BASE_URL + "/notification";

	public interface PushNotificationUrls {

		public static final String ADD_DEVICE = "/device/add";

		public static final String BROADCAST_NOTIFICATION = "/broadcast";

		public static final String READ_NOTIFICATION = "/read/{deviceId}";
	}

	public static final String VERSION_CONTROL_BASE_URL = BASE_URL + "/version";

	public interface VersionControlUrls {
		public static final String CHECK_VERSION = "/check";
		public static final String CHANGE_VERSION = "/change";
	}

	public static final String SOLR_PATIENT_TREATMENT_BASE_URL = BASE_URL + "/solr/treatment";

	public interface SolrPatientTreatmentUrls {

		public static final String SEARCH = "/{type}/{range}";

	}

	public static final String REPORTS_BASE_URL = BASE_URL + "/reports";

	public interface ReportsUrls {
		public static final String GET_IPD_REPORTS = "/getIPDReports";
		public static final String GET_OPD_REPORTS = "/getOPDReports";
		public static final String GET_OT_REPORTS = "/getOTReports";
		public static final String GET_DELIVERY_REPORTS = "/getDeliveryReports";

		public static final String SUBMIT_IPD_REPORTS = "/submitIPDReports";
		public static final String SUBMIT_OPD_REPORTS = "/submitOPDReports";
		public static final String SUBMIT_OT_REPORTS = "/submitOTReports";
		public static final String SUBMIT_DELIVERY_REPORTS = "/submitDeliveryReports";

		public static final String ADD_PRESCRIPTION_IN_OPD_REPORTS = "/addOPDReports";
		public static final String ADD_CLINICAL_INDICATOR = "/addClinicalIndicator";
		public static final String GET_CLINICAL_INDICATOR = "/getClinicalIndicator/{id}";
		public static final String GET_CLINICAL_INDICATORS = "/getClinicalIndicator/get";
		public static final String DELETE_CLINICAL_INDICATORS = "/clinicalIndicator/{id}/delete";
		public static final String ADD_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER = "/addEquipmentLogAMCAndServicingRegister";
		public static final String GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER = "/getEquipmentLogAMCAndServicingRegister/{id}";
		public static final String GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTERS = "/getEquipmentLogAMCAndServicingRegister";
		public static final String DELETE_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER = "/equipmentLogAMCAndServicingRegister/{id}/delete";
		public static final String ADD_REPAIR_RECORDS_OR_COMPLAINCE_BOOK = "/addRepairRecordsOrComplianceBook";
		public static final String GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOK = "/getRepairRecordsOrComplianceBook/{id}";
		public static final String GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOKS = "/getRepairRecordsOrComplianceBook";
		public static final String DELETE_REPAIR_RECORDS_OR_COMPLAINCE_BOOK = "/repairRecordsOrComplianceBook/{id}/delete";
		public static final String ADD_BROKEN_APPOINTMENT = "/addBrokenAppointment";
		public static final String GET_BROKEN_APPOINTMENT = "/getBrokenAppointment/{id}";
		public static final String GET_BROKEN_APPOINTMENTS = "/getBrokenAppointment";
		public static final String DELETE_BROKEN_APPOINTMENT = "/brokenAppointment/{id}/delete";
		public static final String DOWNLOAD_OT_REPORTS = "OTReports/download/{otId}";
		public static final String DOWNLOAD_DELIVERY_REPORT = "deliveryReport/download/{reportId}";

		public static final String UPDATE_OT_REPORTS = "/updateOTReports";
	}

	public static final String DYNAMIC_UI_BASE_URL = BASE_URL + "/dynamicUI";

	public interface DynamicUIUrls {
		public static final String GET_ALL_PERMISSIONS_FOR_DOCTOR = "/getAllPermissionsForDoctor/{doctorId}";
		public static final String GET_PERMISSIONS_FOR_DOCTOR = "/getPermissionsForDoctor/{doctorId}";
		public static final String GET_BOTH_PERMISSION_FOR_DOCTOR = "/getBothPermissionsForDoctor/{doctorId}";
		public static final String POST_PERMISSIONS = "/postPermissions";
		public static final String GET_DATA_PERMISSION_FOR_DOCTOR = "/getDataPermissionForDoctor/{doctorId}";
		public static final String POST_DATA_PERMISSIONS = "/postDataPermissions";
		public static final String GET_DENTAL_LAB_PERMISSION_FOR_LAB = "/getDataPermissionForLab/{dentalLabId}";
		public static final String POST_DENTAL_LAB_PERMISSIONS = "/postDentalLabPermissions";
		public static final String GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB = "/getAllDataPermissionForLab";
		public static final String ADD_EDIT_KIOSK_PERMISSION = "/kioskPermission/add";
		public static final String GET_KIOSK_PERMISSION = "/kioskPermission/{doctorId}/get";
	}

	public static final String BLOGS_BASE_URL = BASE_URL + "/blogs";

	public interface BlogsUrls {
		public static final String GET_BLOGS_CATEGORY = "/getBlogCategory";

		public static final String GET_BLOGS = "/getBlogs";

		public static final String GET_BLOG_List = "/get";

		public static final String GET__MOST_LIKES_OR_VIEWED_BLOGS = "/getMostLIkesOrViewedBlogs";

		public static final String GET_BLOG_BY_ID = "/getBlog/{blogId}";

		public static final String GET_BLOG_BY_SLUG_URL = "/getBlogbySlugUrl/{slugURL}";

		public static final String ADD_EDIT_FEVOURITE_BLOGS = "/addTOFovourite/{blogId}/{userId}";

		public static final String GET_FEVOURITE_BLOGS = "/getFovourite";

		public static final String LIKE_THE_BLOG = "/likeTheBlog/{blogId}/{userId}";

	}

	public static final String BILLING_BASE_URL = BASE_URL + "/billing";

	public interface BillingUrls {

		public static final String GET_INVOICE = "/invoice/{invoiceId}/view";

		public static final String GET_INVOICE_RECEIPT_INITIALS = "/initials/{locationId}";

		public static final String UPDATE_INVOICE_RECEIPT_INITIALS = "/updateInitials";

		public static final String ADD_EDIT_INVOICE = "/invoice/add";

		public static final String ADD_EDIT_RECEIPT = "/receipt/add";

		public static final String GET_INVOICES = "/invoice/{type}";

		public static final String GET_AVAILABLE_ADVANCE_AMOUNT = "/advanceAmount/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_RECEIPTS = "/receipt";

		public static final String DELETE_INVOICE = "/invoice/{invoiceId}/delete";

		public static final String DELETE_RECEIPT = "/receipt/{receiptId}/delete";

		public static final String ADD_INVOICE_AND_PAY = "/invoice/addAndPay";

		public static final String GET_TOTAL_DUE_AMOUNT = "/dueAmount/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_LEDGER = "/ledger/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_TOTAL_DUE_AND_ADVANCE_AMOUNT = "/amount/{locationId}/{hospitalId}/{patientId}";

		public static final String CREATE_LEDGER = "/ledger";

		public static final String DOWNLOAD_INVOICE = "/downloadInvoice/{invoiceId}";

		public static final String DOWNLOAD_RECEIPT = "/downloadReceipt/{receiptId}";

		public static final String EMAIL_INVOICE = "invoice/{invoiceId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String EMAIL_RECEIPT = "receipt/{receiptId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DUE_AMOUNT_REMAINDER = "dueAmount/{patientId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/remainder";

		public static final String DOWNLOAD_MULTIPLE_RECEIPT = "receipt/download";

		public static final String CHANGE_INVOICE_ITEM_TREATMENT_STATUS = "changeInvoiceItemTreatmentStatus";

		public static final String EMAIL_MULTIPLE_RECEIPT = "receipt/email";;
	}

	public static final String LOCALE_BASE_URL = BASE_URL + "/locale";

	public interface LocaleUrls {

		public static final String UPLOAD = "/upload";
		public static final String EDIT_LOCALE_CONTACT_DETAILS = "/editContactDetails";
		public static final String EDIT_LOCALE_ADDRESS_DETAILS = "/editAddressDetails";
		public static final String EDIT_LOCALE_OTHER_DETAILS = "/editOtherDetails";
		public static final String EDIT_LOCALE_VISIT_DETAILS = "/editVisitDetails";
		public static final String EDIT_LOCALE_IMAGES = "/{id}/editImages";
		public static final String GET_LOCALE_DETAILS = "/get";
		public static final String GET_LOCALE_BY_SLUGURL = "/get/{slugUrl}";
		public static final String ADD_USER_REQUEST = "/addUserRequest";
		public static final String ADD_PHARMACY_RESPONSE = "/addPharmacyResponse";
		public static final String GET_PATIENT_ORDER_HISTORY = "/getPatientOrderHistory/{userId}";
		public static final String GET_PHARMCIES_FOR_ORDER = "/getPharmaciesForOrder";
		public static final String GET_PHARMCIES_COUNT_FOR_ORDER = "/getPharmaciesCountForOrder";
		public static final String UPLOAD_RX_IMAGE = "/uploadRXImage";
		public static final String ADD_EDIT_RECOMMENDATION = "/addEditRecommendation";
		public static final String ORDER_DRUG = "/orderDrugs";
		public static final String GET_USER_FAKE_REQUEST_COUNT = "/getFakeRequestCount/{patientId}";
		public static final String GET_PATIENT_ORDERS = "/patient/orders/{userId}";
		public static final String GET_PATIENT_REQUEST = "/patient/requests/{userId}";
		public static final String CANCEL_ORDER_DRUG = "orderDrugs/cancel/{orderId}/{userId}";
	}

	public static final String FEEDBACK_BASE_URL = BASE_URL + "/feedback";

	public interface FeedbackUrls {
		public static final String ADD_EDIT_GENERAL_APPOINTMENT_FEEDBACK = "/addEditAppointmentGeneralFeedback";
		public static final String ADD_EDIT_PRESCRIPTION_FEEDBACK = "/addEditPRescriptionFeedback";
		public static final String ADD_EDIT_PHARMACY_FEEDBACK = "/addEditPharmacyFeedback";
		public static final String ADD_EDIT_DAILY_IMPROVEMENT_FEEDBACK = "/addEditDailyImprovementFeedback";
		public static final String ADD_EDIT_PATIENT_FEEDBACK = "/addEditPatientFeedback";

		public static final String GET_GENERAL_APPOINTMENT_FEEDBACK = "/getAppointmentGeneralFeedback";
		public static final String GET_PRESCRIPTION_FEEDBACK = "/getPrescriptionFeedback";
		public static final String GET_PHARMACY_FEEDBACK = "/getPharmacyFeedback";
		public static final String GET_DAILY_IMPROVEMENT_FEEDBACK = "/getDailyImprovementFeedback";
		public static final String GET_PATIENT_FEEDBACK = "/getPatientFeedback";

		public static final String ADD_PATIENT_FEEDBACK_REPLY = "/addPatientFeedbackReply";
		public static final String APPROVE_PATIENT_FEEDBACK = "/approvePatientFeedback";
	}

	public static final String DISCHARGE_SUMMARY_BASE_URL = BASE_URL + "/dischargeSummary";

	public interface DischargeSummaryUrls {
		public static final String GET_DISCHARGE_SUMMARY = "/getDischargeSummery";
		public static final String GET_DISCHARGE_SUMMARY_BY_VISIT = "/byVisit";
		public static final String ADD_DISCHARGE_SUMMARY = "/add";
		public static final String VIEW_DISCHARGE_SUMMARY = "/view/{dischargeSummeryId}";
		public static final String DELETE_DISCHARGE_SUMMARY = "/{dischargeSummeryId}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DOWNLOAD_DISCHARGE_SUMMARY = "/download/{dischargeSummeryId}/";
		public static final String EMAIL_DISCHARGE_SUMMARY = "/{dischargeSummeryId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";
		public static final String UPDATE_DISCHARGE_SUMMARY_DATA = "/updateData";
		public static final String ADD_BABY_NOTES = "/babyNotes/add";
		public static final String ADD_LABOUR_NOTES = "/loabourNotes/add";
		public static final String ADD_OPERATION_NOTES = "/operationNotes/add";
		public static final String ADD_CEMENT = "/cement/add";
		public static final String ADD_IMPLANT = "/implant/add";
		public static final String DELETE_BABY_NOTES = "/babyNotes/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_OPERAION_NOTES = "/operationNotes/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_LABOUR_NOTES = "/labourNote/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_CEMENT = "/cement/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DELETE_IMPLANT = "/implant/{id}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String GET_DISCHARGE_SUMMARY_ITEMS = "/{type}/{range}";
		public static final String EMAIL_DISCHARGE_SUMMARY_WEB = "/{dischargeSummeryId}/{emailAddress}/mail";
		public static final String ADD_EDIT_FLOWSHEETS = "/addEditFlowsheets";
		public static final String DOWNLOAD_FLOWSHEETS = "/flowsheet/{id}/download";
		public static final String DOWNLOAD_FLOWSHEETS_BY_DISCHARGE_SUMMARY_ID = "/flowsheet/download/{dischargeSummaryId}";
		public static final String GET_FLOWSHEETS = "/getFlowsheets";
		public static final String GET_FLOWSHEET_BY_ID = "/getFlowsheetById/{id}";
		public static final String ADD_DIAGRAM = "/addDiagram";
		public static final String UPLOAD_DIAGRAM = "/diagram/upload";
		public static final String UPLOAD_MULTIPART_DIAGRAM = "/diagram/multipart/upload";
	}

	public static final String SOLR_DISCHARGE_SUMMARY_BASE_URL = BASE_URL + "/solr/dischargeSummary";

	public interface SolrDischargeSummaryUrls {
		public static final String SEARCH_LABOUR_NOTES = "searchLabourNotes/{range}";

		public static final String SEARCH_OPERATION_NOTES = "searchOperationNotes/{range}";

		public static final String SEARCH_BABY_NOTES = "searchBabyNotes/{range}";

		public static final String SEARCH_CEMENT = "searchCement/{range}";

		public static final String SEARCH_IMPLANT = "searchImplant/{range}";
	}

	public static final String VIDEO_BASE_URL = BASE_URL + "/video";

	public interface VideoUrls {
		public static final String ADD_VIDEO = "add";

		public static final String GET_VIDEO = "get";

		public static final String ADD_MY_VIDEO = "addMyVideo";

		public static final String GET_MY_VIDEO = "getMyVideo";

	}

	public static final String LAB_BASE_URL = BASE_URL + "/lab";

	public interface LabUrls {
		public static final String GET_CLINICS_WITH_REPORTS_COUNT = "/getClinicWithReportCount/{doctorId}/{locationId}/{hospitalId}";
		public static final String GET_REPORTS_FOR_SPECIFIC_DOCTOR = "/getReports/{doctorId}/{locationId}/{hospitalId}";
		public static final String ADD_EDIT_PICKUP_REQUEST = "/addEditPickupRequest";
		public static final String GET_PICKUP_REQUEST_BY_ID = "/getPickupRequestById";
		public static final String GET_COLLECTION_BOY_LIST = "/getCollectionBoys";
		public static final String GET_CB_LIST_BY_PARENT_LAB = "/getCBListByParentLab";
		public static final String GET_DAUGHTER_LAB_LIST_BY_CB = "/getDaughterListByCB";
		public static final String ADD_CB_LAB_ASSOCIATION = "/addCBLabAssociation";
		public static final String GET_CB_LAB_ASSOCIATION = "/getCBLabAssociation";
		public static final String ADD_EDIT_COLLECTION_BOY = "/addEditCollectionBoy";
		public static final String DISCARD_COLLECTION_BOY = "/discardCollectionBoy";
		public static final String CHANGE_AVAILABILITY_OF_CB = "/changeCBAvailability";
		public static final String VERIFY_CRN = "/verifyCRN";
		public static final String GET_RATE_CARDS = "/getRateCards";
		public static final String GET_RATE_CARD_TEST = "/getRateCardTests";
		public static final String GET_RATE_CARD_TEST_BY_DL = "/getRateCardTestsForDaughterLab";
		public static final String ADD_EDIT_RATE_CARD = "/addEditRateCards";
		public static final String ADD_EDIT_RATE_CARD_TESTS = "/addEditRateCardTests";
		public static final String GET_COLLECTION_BOY_REQUEST_LIST = "/getCollectionBoyRequestList";
		public static final String GET_ASSOCIATED_LABS = "/getAssociatedLabs";
		public static final String GET_CLINICS_AND_LABS = "/clinics";
		public static final String GET_SPECIMEN_LIST = "/getSpecimens";
		public static final String EDIT_COLLECTION_BOY = "/editCollectionBoy";
		public static final String ADD_EDIT_LAB_RATE_CARD_ASSOCIAITION = "/addEditLabRateCardAssociations";
		public static final String GET_DL_RATE_CARD = "/getDLRateCard";
		public static final String GET_PICKUPS_FOR_CB = "/getPickupsForCB";
		public static final String GET_PICKUPS_FOR_DL = "/getPickupsForDL";
		public static final String GET_PICKUPS_FOR_PL = "/getPickupsForPL";
		public static final String GET_LAB_REPORTS = "/getLabReports";
		public static final String UPLOAD_REPORTS_MULTIPART = "/uploadReportsMultipart";
		public static final String UPLOAD_REPORTS = "/uploadReports";
		public static final String GET_REPORTS_FOR_SAMPLES = "/getReportsForSample";
		public static final String UPDATE_REQUEST_STATUS = "/updateRequestStatus/{id}";
		public static final String EDIT_LAB_REPORTS = "/editLabReports";
		public static final String GET_GROUPED_LAB_TEST = "/getGroupedLabTest";
		public static final String GET_LAB_REPORTS_FOR_DOCTOR = "/getLabReportsForDoctor";
		public static final String GET_LAB_REPORTS_FOR_LAB = "/getLabReportsForLab";
		public static final String UPLOAD_REPORTS_TO_DOCTOR = "/uploadReportsToDoctor";
		public static final String CHANGE_PATIENT_SHARE_STATUS = "/changePatientShareStatus";
		public static final String ADD_EDIT_DENTAL_WORKS = "/addEditDentalWorks";
		public static final String GET_DENTAL_WORKS = "/getDentalWorks";
		public static final String DELETE_DENTAL_WORKS = "/deleteDentalWorks";
		public static final String DOWNLOAD_REQUISATION_FORM = "/requisationForm/download";
		public static final String ALLOCATE_COLLECTION_BOY_DYNAMICALLY = "/allocateDynamicCB";
		public static final String ADD_TO_FAVOURITE_RATE_CARD_TEST = "/rateCardTest/{locationId}/{hospitalId}/{diagnosticTestId}/makeFavourite";

	}

	public static final String INVENTORY_BASE_URL = BASE_URL + "/inventory";

	public interface InventoryUrls {
		public static final String ADD_INVENTORY_ITEM = "/addInventoryItem";
		public static final String GET_INVENTORY_ITEMS = "/getInventoryItemList";
		public static final String GET_INVENTORY_ITEM_BY_ID = "/getInventoryItem/{id}";
		public static final String GET_MANUFACTURERS = "/getManufacturerList";
		public static final String ADD_INVENTORY_STOCK = "/addInventoryStock";
		public static final String GET_INVENTORY_STOCKS = "/getInventoryStocks";
		public static final String ADD_INVENTORY_BATCH = "/addInventoryBatch";
		public static final String GET_INVENTORY_BATCHES = "/getInventoryBatches";
		public static final String DISCARD_INVENTORY_ITEM = "/discardInventoryItem/{id}";
		public static final String DISCARD_MANUFACTURER = "/discardManufacturer/{id}";
		public static final String DISCARD_INVENTORY_STOCK = "/discardInventoryStock/{id}";
		public static final String DISCARD_INVENTORY_BATCH = "/discardInventoryBatch/{id}";
		public static final String ADD_MANUFACTURER = "/addManufacturer";
		public static final String ADD_EDIT_SETTINGS = "/addEditSettings";
		public static final String GET_SETTINGS = "/getSettings";
		public static final String GET_INVENTORY_BATCHES_BY_RESOURCE_ID = "/getBatchesByResourceId";

	}

	public static final String ANALYTICS_BASE_URL = BASE_URL + "/analytics";

	public interface AnalyticsUrls {
		public static final String GET_PATIENT_ANALYTICS_DATA = "/patient/{doctorId}/{locationId}/{hospitalId}";
		public static final String GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS = "/mostPrescribed/{type}/{locationId}/{hospitalId}";
		public static final String GET_APPOINTMENT_ANALYTICS_DATA = "/appointment";
		public static final String GET_APPOINTMENT_AVERAGE_TIME_ANALYTICS_DATA = "/appointment/averageTime";
		public static final String GET_APPOINTMENT_COUNT_ANALYTICS_DATA = "/appointment/count";
		public static final String GET_INCOME_DETAILS_ANALYTICS_DATA = "/incomeDetails";
		public static final String GET_INCOME_ANALYTICS_DATA = "/income";
		public static final String GET_PAYMENT_DETAILS_ANALYTICS_DATA = "/paymentDetails";
		public static final String GET_PAYMENT_ANALYTICS_DATA = "/payment";
		public static final String GET_AMOUNT_DUE_ANALYTICS_DATA = "/amountDue";
		public static final String GET_TREATMENTS_ANALYTICS_DATA = "/treatments";
		public static final String GET_PATIENT_ANALYTIC = "/doctor/patient";
		public static final String GET_TREATMENT_ANALYTIC = "/doctor/treatmentService";
		public static final String GET_APPOINTMENT_ANALYTIC = "/doctor/appointment";
		public static final String GET_PRESCRIPTION_ANALYTIC = "/doctor/prescription";
		public static final String GET_PRESCRIPTION_ITEM_ANALYTIC = "/doctor/prescription/{type}";
		public static final String GET_PATIENT_VISIT_ANALYTIC = "/doctor/visit";

	}

	public static final String ADMIT_CARD_URL = BASE_URL + "/admitCard";

	public interface AdmitCardUrls {

		public static final String GET_ADMIT_CARDS = "/getAdmitCard";
		public static final String ADD_ADMIT_CARD = "/add";
		public static final String VIEW_ADMIT_CARD = "/view/{admitCardId}";
		public static final String DELETE_ADMIT_CARD = "/{admitCardId}/{doctorId}/{locationId}/{hospitalId}/delete";
		public static final String DOWNLOAD_ADMIT_CARD = "/download/{admitCardId}/";
		public static final String EMAIL_ADMIT_CARD = "/{admitCardId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";
		public static final String EMAIL_ADMIT_CARD_WEB = "/{admitCardId}/{emailAddress}/mail";

	}

	public static final String RANKING_BASE_URL = BASE_URL + "/ranking";

	public interface RankingUrls {

		public static final String GET_DOCTORS_RANKING = "/doctors";
	}

	public static final String USER_FAVOURITES_BASE_URL = BASE_URL + "/favourite";

	public interface UserFavouritesUrls {

		public static final String ADD_REMOVE_FROM_FAVOURITES = "/addRemove/{resourceType}/{userId}/{resourceId}";

		public static final String GET_FAVOURITE_DOCTORS = "/doctors/{userId}";

		public static final String GET_FAVOURITE_PHARMACIES = "/pharmacies/{userId}";

		public static final String GET_FAVOURITE_LABS = "/labs/{userId}";
	}

	public static final String DIAGNOSTIC_TEST_ORDER_BASE_URL = BASE_URL + "/test";

	public interface DiagnosticTestOrderUrls {

		public static final String SEARCH_LABS = "/searchLabs";

		public static final String GET_SAMPLE_PICKUP_TIME_SLOTS = "/pickUpTimeSlots";

		public static final String PLACE_ORDER = "/placeOrder";

		public static final String GET_PATIENT_ORDERS = "/orders/patient/{userId}";

		public static final String GET_LAB_ORDERS = "/orders/lab/{locationId}";

		public static final String CANCEL_ORDER_DIAGNOSTIC_TEST = "/cancelOrder/{orderId}/{userId}";

		public static final String GET_ORDER_BY_ID = "/order/{orderId}/view";

		public static final String GET_ORDERS = "orders";

		public static final String ADD_EDIT_DIAGNOSTIC_TEST_PACKAGE = "packages/addEdit";

		public static final String GET_DIAGNOSTIC_TEST_PACKAGES = "packages/{locationId}/{hospitalId}";

		public static final String SEARCH_DIAGNOSTIC_TEST = "searchDiagnosticTest";
	}

	public static final String UPLOAD_DATA_BASE_URL = BASE_URL + "/upload";

	public interface UploadDataUrls {

		public static final String PATIENTS = "/patients/{doctorId}/{locationId}/{hospitalId}";

		public static final String PRESCRIPTIONS = "/prescriptions/{doctorId}/{locationId}/{hospitalId}";

		public static final String APPOINTMENTS = "/appointments/{doctorId}/{locationId}/{hospitalId}";

		public static final String TREATMENT_PLANS = "/treatments/plans/{doctorId}/{locationId}/{hospitalId}";

		public static final String TREATMENTS = "/treatments/{doctorId}/{locationId}/{hospitalId}";

		public static final String ASSIGN_PNUM_TO_PATIENTS = "/assignPNUMToPatients/{doctorId}/{locationId}/{hospitalId}";

		public static final String DELETE_PATIENTS = "/deletePatients/{doctorId}/{locationId}/{hospitalId}";

		public static final String UPDATE_EMR = "/update/EMR";

		public static final String TREATMENT_SERVICES = "/treatments/services/{doctorId}/{locationId}/{hospitalId}";

		public static final String CLINICAL_NOTES = "/clinicalnotes/{doctorId}/{locationId}/{hospitalId}";

		public static final String INVOICES = "/invoices/{doctorId}/{locationId}/{hospitalId}";

		public static final String PAYMENTS = "/payments/{doctorId}/{locationId}/{hospitalId}";

		public static final String UPDATE_TREATMENTS = "/treatments/{doctorId}/{locationId}/{hospitalId}/update";

		public static final String UPLOAD_IMAGES = "/images/{doctorId}/{locationId}/{hospitalId}/";

	}

	public static final String DOWNLOAD_DATA_BASE_URL = BASE_URL + "/download";

	public interface DownloadDataUrls {

		public static final String PATIENTS = "/patients/{doctorId}/{locationId}/{hospitalId}";

		public static final String PRESCRIPTIONS = "/prescriptions/{doctorId}/{locationId}/{hospitalId}";

		public static final String APPOINTMENTS = "/appointments/{doctorId}/{locationId}/{hospitalId}";

		public static final String TREATMENT_PLANS = "/treatments/plans/{doctorId}/{locationId}/{hospitalId}";

		public static final String TREATMENTS = "/treatments/{doctorId}/{locationId}/{hospitalId}";

		public static final String ASSIGN_PNUM_TO_PATIENTS = "/assignPNUMToPatients/{doctorId}/{locationId}/{hospitalId}";

		public static final String DELETE_PATIENTS = "/deletePatients/{doctorId}/{locationId}/{hospitalId}";

		public static final String CLINICAL_NOTES = "/clinicalnotes/{doctorId}/{locationId}/{hospitalId}";

		public static final String INVOICES = "/invoices/{doctorId}/{locationId}/{hospitalId}";

		public static final String PAYMENTS = "/payments/{doctorId}/{locationId}/{hospitalId}";

		public static final String DATA = "/data";

	}

	public static final String DENTAL_LAB_BASE_URL = BASE_URL + "/dentalLab";

	public interface DentalLabUrls {

		public static final String ADD_EDIT_DENTAL_WORKS = "/addEditDentalWorks";
		public static final String GET_DENTAL_WORKS = "/getDentalWorks";
		public static final String DELETE_DENTAL_WORKS = "/deleteDentalWorks";
		public static final String CHANGE_LAB_TYPE = "/changeLabType";
		public static final String ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION = "/addEditDentalLabDoctorAssociation";
		public static final String GET_DENTAL_LAB_DOCTOR_ASSOCIATION = "/getDentalLabDoctorAssociation";
		public static final String GET_DENTAL_LAB_DOCTOR_ASSOCIATION_FOR_DOCTOR = "/getDentalLabDoctorAssociationForDoctor";
		public static final String ADD_EDIT_DENTAL_WORK_PICKUP = "/addEditDentalWorkPickup";
		public static final String GET_DENTAL_WORK_PICKUPS = "/getDentalWorkPickups";
		public static final String DELETE_DENTAL_WORK_PICKUPS = "/getDentalWorkPickups";
		public static final String ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION = "/addEditRateCardWorkAssociation";
		public static final String GET_RATE_CARD_WORKS = "/getRateCardWork";
		public static final String ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION = "/addEditRateCardDoctorAssociation";
		public static final String GET_RATE_CARD_DOCTOR_ASSOCIATION = "/getRateCardDoctorAssociation";
		public static final String ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION = "/addEditCollectionBoyDoctorAssociation";
		public static final String GET_COLLECTION_BOY_DOCTOR_ASSOCIATION = "/getCollectionBoyDoctorAssociation";
		public static final String GET_CB_LIST_FOR_DENTAL_LAB = "/getCBListForDentalLab";
		public static final String CHANGE_REQUEST_STATUS = "/changeRequestStatus";
		public static final String ADD_DENTAL_IMAGE_MULTIPART = "/addDentalImageMultipart";
		public static final String ADD_DENTAL_IMAGE_BASE_64 = "/addDentalImageBase64";
		public static final String UPDATE_DENTAL_STAGES_FOR_LAB = "/updateDentalStagesForLab";
		public static final String UPDATE_DENTAL_STAGES_FOR_DOCTOR = "/updateDentalStagesForDoctor";
		public static final String GET_RATE_CARD_WORKS_BY_RATE_CARD = "/getRateCardWorkByRateCard";
		public static final String CANCEL_REQUEST = "/cancelRequest";
		public static final String UPDATE_ETA = "/updateETA";
		public static final String DISCARD_REQUEST = "/discardRequest/{requestId}";
		public static final String GET_PICKUP_REQUEST_BY_ID = "/getRequestById/{requestId}";
		public static final String DOWNLOAD_DENTAL_LAB_REPORT = "/getReportById/{requestId}";
		public static final String DOWNLOAD_DENTAL_LAB_INSPECTION_REPORT = "/inspectionReport/{requestId}/download";
		public static final String DOWNLOAD_MULTIPLE_DENTAL_LAB_INSPECTION_REPORT = "/inspectionReport/download";
		public static final String DOCTOR_REGISTRATION = "/doctorRegistration";
		public static final String ADD_EDIT_TAX = "/addEditTax";
		public static final String ADD_EDIT_INVOICE = "/billing/addEditInvoice";
		public static final String ADD_EDIT_RECEIPT = "/billing/addEditReceipt";
		public static final String GET_INVOICES = "/billing/getInvoices";
		public static final String GET_RECEIPTS = "/billing/getReceipts";
		public static final String GET_INVOICE_BY_ID = "/billing/getInvoiceById";
		public static final String GET_RECEIPT_BY_ID = "/billing/getReceiptById";
		public static final String DISCARD_INVOICE = "/billing/discardInvoice";
		public static final String DISCARD_RECEIPT = "/billing/discardReceipt";
		public static final String GET_AMOUNT = "/billing/getAmount";
		public static final String DOWNLOAD_DENTAL_WORK_INVOICE = "/billing/invoice/{invoiceId}/download";
		public static final String DOWNLOAD_DENTAL_WORK_RECEIPT = "/billing/receipt/{receiptId}/download";
	}

	public static final String CERTIFICATE_BASE_URL = BASE_URL + "/certificate/";

	public interface CertificateTemplatesUrls {
		public static final String ADD_CERTIFICATE_TEMPLATES = "template/add";

		public static final String GET_CERTIFICATE_TEMPLATE_BY_ID = "/template/{templateId}/view";

		public static final String GET_CERTIFICATE_TEMPLATES = "template";

		public static final String DELETE_CERTIFICATE_TEMPLATES = "/template/{templateId}/delete";

		public static final String ADD_PATIENT_CERTIFICATE = "/patient/add";

		public static final String GET_PATIENT_CERTIFICATES = "/patient/";

		public static final String DELETE_PATIENT_CERTIFICATE = "/patient/{certificateId}/delete";

		public static final String GET_PATIENT_CERTIFICATE_BY_ID = "/patient/{certificateId}/view";

		public static final String DOWNLOAD_PATIENT_CERTIFICATE = "/patient/{certificateId}/download";

		public static final String SAVE_CERTIFICATE_SIGN_IMAGE = "/patient/saveCertifcateSign";

	}

	public static final String DOCTOR_LAB_URL = BASE_URL + "/doctorLab";

	public interface DoctorLabUrls {

		public static final String ADD_DOCTOR_LAB_REPORT = "/addEditDoctorLabReport";
		public static final String UPLOAD_DOCTOR_LAB_MULTIPART_FILE = "/upload/file/multipart";
		public static final String UPLOAD_DOCTOR_LAB_FILE = "/upload/file";
		public static final String GET_DOCTOR_LAB_REPORTS = "/getDoctorLabReports";
		public static final String GET_DOCTOR_LAB_REPORT_BY_ID = "/{reportId}/view";
		public static final String ADD_TO_FAVOURITE_DOCTOR_LIST = "/addFavouriteDoctor";
		public static final String GET_FAVOURITE_DOCTOR = "/getFavouriteDoctor";
		public static final String ADD_DOCTOR_REFERENCE = "/referDoctor";
		public static final String SEARCH_DOCTOR = "/searchDoctor";
		public static final String UPDATE_IS_SHARE_WITH_PATIENT = "/updateIsShareWithPatient/{reportId}";
		public static final String UPDATE_IS_SHARE_WITH_DOCTOR = "/updateIsShareWithDoctor/{reportId}";
		public static final String DELETE_FAVOURITE_DOCTOR = "fevouriteDoctor/{id}/delete";
		public static final String DELETE_DOCTOR_LAB_REPORTS = "/{reportId}/delete";

	}

	public static final String SOLR_DENTAL_WORKS_BASE_URL = BASE_URL + "/solr/dentalWorks";

	public interface ESDentalLabsUrl {
		public static final String SEARCH_DENTAL_WORKS = "/searchDentalWorks/{range}";
	}

	public static final String NUTRITION_BASE_URL = BASE_URL + "/nutrition";

	public interface NutritionUrl {

		public static final String ADD_EDIT_NUTRITION_REFERENCE = "/addEditNutritionReference";
		public static final String GET_NUTRITION_REFERENCES = "/getNutritionReferences";
		public static final String GET_NUTRITION_ANALYTICS = "/getNutritionAnalytics";

	}

	public static final String DENTAL_IMAGING_URL = BASE_URL + "/dentalImaging";

	public interface DentalImagingUrl {

		public static final String ADD_EDIT_DENTAL_IMAGING_REQUEST = "/addEditRequest";
		public static final String GET_REQUESTS = "/getRequests";
		public static final String GET_SERVICE_LOCATION = "/getServiceLocation";
		public static final String GET_SERVICES = "/getServices";
		public static final String ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION = "/addEditDentalImagingLocationAssociation";
		public static final String GET_LOCATION_ASSOCIATED_SERVICES = "/getLocationAssociatedServices";
		public static final String GET_HOSPITAL_LIST = "/getHospitalList";
		public static final String ADD_RECORDS = "/addRecords";
		public static final String DISCARD_REQUEST = "/discardRequest";
		public static final String DISCARD_RECORD = "/discardRecord";
		public static final String GET_ASSOCIATED_DOCTORS = "/getAssociatedDoctors";
		public static final String DOCTOR_REGISTRATION = "/doctorRegistration";
		public static final String ADD_EDIT_INVOICE = "/addEditInvoice";
		public static final String GET_INVOICES = "/getInvoices";
		public static final String DISCARD_INVOICE = "/discardInvoice/{id}";
		public static final String CHANGE_PAYMENT_STATUS = "/changePaymentStatus/{id}";
		public static final String DOWNLOAD_INVOICES = "/invoice/{id}/download";
		public static final String GET_SERVICE_VISIT_ANALYTICS = "/analytics/serviceVisit";
		public static final String GET_REPORTS = "/getReports";
		public static final String GET_PATIENT_VISIT_ANALYTICS = "/analytics/patientVisit";
		public static final String GET_DOCTOR_VISIT_ANALYTICS = "/analytics/doctorVisit";
		public static final String SEND_INVOICE_EMAIL = "/sendInvoiceEmail/{invoiceId}";
		public static final String SEND_REPORT_EMAIL = "/sendReportEmail/{id}";
		public static final String CHANGE_VISIT_STATUS = "/changeVisitStatus/{id}";
		public static final String GET_DETAILED_DOCTOR_VISIT_ANALYTICS = "/analytics/detailedDoctorVisit";
		public static final String GET_DENTAL_IMAGING_DATA = "/getDentalImagingData";
	}

	public static final String SEARCH_BASE_URL = BASE_URL + "/search";

	public interface SearchUrls {

		public static final String SEARCH_DOCTORS = "/doctors";
		public static final String GET_RESOURCES_COUNT_BY_CITY = "/resources/countByCity/{city}";
	}

	public static final String Lab_PRINT_BASE_URL = BASE_URL + "/labPrint";

	public interface LabPrintUrls {
		public static final String ADD_EDIT_LAB_PRINT_SETTING = "/setting/add";
		public static final String GET_Lab_PRINT_SETTING = "/setting/{locationId}/{hospitalId}/get";
		public static final String ADD_EDIT_LAB_PRINT_HEADER = "/header/addEdit";
		public static final String ADD_EDIT_LAB_PRINT_FOOTER = "/footer/addEdit";
		public static final String ADD_EDIT_LAB_DOCUMENT = "/document/addEdit";
		public static final String GET_LAB_DOCUMENTS = "/document/get";
		public static final String GET_LAB_DOCUMENT = "/document/{documentId}/view";
		public static final String DELETE_LAB_DOCUMENT = "/document/{documentId}/delete";
	}

	public static final String PROCEDURE_BASE_URL = BASE_URL + "/procedure";

	public interface ProcedureUrls {
		public static final String ADD_PROCEDURE = "/add";
		public static final String GET_PROCEDURE = "/{id}/get";
		public static final String GET_PROCEDURE_LIST = "/getList";
		public static final String DISCARD_PROCEDURE = "/{id}/discard";
		public static final String ADD_PROCEDURE_STRUCTURE = "/addStructure";
		public static final String GET_PROCEDURE_STRUCTURE = "/{id}/getStructure";
		public static final String GET_PROCEDURE_STRUCTURE_LIST = "/getStructureList";
		public static final String DISCARD_PROCEDURE_STRUCTURE = "/{id}/discardStructure";
		public static final String ADD_DIAGRAM = "/addDiagram";
	}
}
