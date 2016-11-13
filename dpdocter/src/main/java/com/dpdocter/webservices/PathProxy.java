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

		public static final String SUBMIT_DOCTOR_CONTACT = "/submitDoctorContact";

		public static final String SUBMIT_CLINIC_CONTACT = "/submitClinicContact";

		public static final String RESEND_VERIFICATION_EMAIL_TO_DOCTOR = "/resendVerificationEmail/{emailaddress}";

	}

	public static final String LOGIN_BASE_URL = BASE_URL + "/login";

	public interface LoginUrls {

		public static final String LOGIN_USER = "/user";

		public static final String LOGIN_PATIENT = "/patient";
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

		public static final String UPDATE_PATIENT_ID_GENERATOR_LOGIC = "/updatePatientIdGeneratorLogic/{locationId}/{patientInitial}/{patientCounter}";

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
		public static final String ADD_SUGGESTION = "/suggestion/add";
		public static final String GET_SUGGESTION = "/getSuggestion/{userId}";
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
	}

	public static final String FORGOT_PASSWORD_BASE_URL = BASE_URL + "/forgotPassword";

	public interface ForgotPasswordUrls {
		public static final String CHECK_LINK_IS_ALREADY_USED = "/checkLink/{userId}";

		public static final String FORGOT_PASSWORD_DOCTOR = "/forgotPasswordDoctor";

		public static final String FORGOT_PASSWORD_PATIENT = "/forgotPasswordPatient";

		public static final String RESET_PASSWORD_PATIENT = "/resetPasswordPatient";

		public static final String RESET_PASSWORD = "/resetPassword";

		public static final String FORGOT_USERNAME = "/forgot-username";
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

		public static final String DOWNLOAD_RECORD = "/download/{recordId}";

		public static final String DELETE_TAG = "/tagrecord/{tagid}/delete";

		public static final String GET_FLEXIBLE_COUNTS = "/getFlexibleCounts";

		public static final String EDIT_RECORD = "/{recordId}/update";

		public static final String CHANGE_LABEL_AND_DESCRIPTION_RECORD = "/changeLabelAndDescription";

		public static final String ADD_RECORDS_MULTIPART = "/add";

		public static final String SAVE_RECORDS_IMAGE = "/saveImage";

		public static final String CHANGE_RECORD_STATE = "/{recordId}/{recordsState}/changeState";
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

		public static final String CHECK_PRESCRIPTION_EXISTS_FOR_PATIENT = "/prescriptionExist/{uniqueEmrId}/{patientId}";

		public static final String DOWNLOAD_PRESCRIPTION = "/download/{prescriptionId}";

		public static final String ADD_DRUG_TO_DOCTOR = "/drug/{drugId}/{doctorId}/{locationId}/{hospitalId}/makeFavourite";

		public static final String ADD_ADVICE = "/advice";

		public static final String DELETE_ADVICE = "/advice/{adviceId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_CUSTOM_DRUG_TO_FAV = "/makeCustomDrugFavourite";

		public static final String ADD_FAVOURITE_DRUG = "/favouriteDrug/add";

		public static final String ADD_GENERIC_NAME_IN_DRUGS = "/addGenericNameInDrugs";
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

		public static final String GET_PATIENT_HISTORY = "/{patientId}";

		public static final String MAIL_MEDICAL_DATA = "mailMedicalData";

		// public static final String ADD_VISITS_TO_HISTORY =
		// "/visits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";
		//
		// public static final String REMOVE_VISITS =
		// "/removeVisits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_MULTIPLE_DATA = "getMultipleData";
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

		public static final String GET_EDUCATION_QUALIFICATIONS = "/getEducationQualifications";

		public static final String ADD_EDIT_MULTIPLE_DATA = "/addEditMultipleData";

		public static final String ADD_EDIT_FACILITY = "clinicProfile/editFacility";

		public static final String ADD_EDIT_GENDER = "addEditGender";

		public static final String ADD_EDIT_DOB = "addEditDOB";

		public static final String SET_RECOMMENDATION = "/recommendation/{doctorId}/{locationId}/{patientId}";

	}

	public static final String PATIENT_VISIT_BASE_URL = BASE_URL + "/patientVisit";

	public interface PatientVisitUrls {

		public static final String ADD_MULTIPLE_DATA = "/add";

		public static final String EMAIL = "/email/{visitId}/{emailAddress}";

		public static final String GET_VISIT = "/{visitId}";

		public static final String GET_VISITS = "/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_VISITS_HANDHELD = "/handheld/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String DELETE_VISITS = "/{visitId}/delete";

		public static final String SMS_VISITS = "/{visitId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/sms";

		public static final String DOWNLOAD_PATIENT_VISIT = "/download/{visitId}";

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
		public static final String SEARCH_PATIENT = "searchPatient/{doctorId}/{locationId}/{hospitalId}/{searchTerm}";

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

		public static final String GET_TIME_SLOTS = "getTimeSlots/{doctorId}/{locationId}/{date}";

		public static final String SEND_REMINDER_TO_PATIENT = "/sendReminder/patient/{appointmentId}";

		public static final String ADD_PATIENT_IN_QUEUE = "/queue/add";

		public static final String REARRANGE_PATIENT_IN_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}/{patientId}/{appointmentId}/{sequenceNo}/rearrange";

		public static final String GET_PATIENT_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_APPOINTMENT_ID = "/{appointmentId}/view";
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

	}

	public static final String SOLR_CITY_BASE_URL = BASE_URL + "/solr/city";

	public interface SolrCityUrls {

		public static final String SEARCH_LOCATION = "searchLocation";
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

	}

	public static final String EMAIL_TRACK_BASE_URL = BASE_URL + "/email";

	public interface EmailTrackUrls {

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

		public static final String GET_LABS = "/labs";

		public static final String ADD_SPECIALITY = "/addSpecialization";
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

	}

	public static final String BLOGS_BASE_URL = BASE_URL + "/blogs";

	public interface BlogsUrls {
		public static final String GET_BLOGS = "/getBlogs";

		public static final String GET_BLOG_BY_ID = "/getBlog/{blogId}";

		public static final String LIKE_THE_BLOG = "/likeTheBlog/{blogId}/{userId}";

	}

}
