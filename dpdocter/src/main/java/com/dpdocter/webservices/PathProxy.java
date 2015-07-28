package com.dpdocter.webservices;

/**
 * @author veeraj
 */
public interface PathProxy {

    public static final String HOME_URL = "/";

    public static final String BASE_URL = "api/v1";

    public static final String SIGNUP_BASE_URL = BASE_URL + "/signup";

    public interface SignUpUrls {
	public static final String DOCTOR_SIGNUP = "/doctor";

	public static final String PATIENT_SIGNUP = "/patient";

	public static final String ACTIVATE_USER = "/activate/{userId}";

	public static final String CHECK_IF_USERNAME_EXIST = "/check-username-exists/{username}";

	public static final String CHECK_IF_MOBNUM_EXIST = "/check-mobnum-exists/{mobileNumber}";

	public static final String CHECK_IF_EMAIL_ADDR_EXIST = "/check-email-exists/{emailaddress}";

	public static final String PATIENT_PROFILE_PIC_CHANGE = "/patientProfilePicChange";
    }

    public static final String LOGIN_BASE_URL = BASE_URL + "/login";

    public interface LoginUrls {
	public static final String LOGIN_USER = "/user";

	public static final String VERIFY_USER = "/user/verifyUser/{userId}";

	public static final String OTP_GENERATOR = "/user/otpGenerator/{mobileNumber}";

    }

    public static final String CONTACTS_BASE_URL = BASE_URL + "/contacts";

    public interface ContactsUrls {
	public static final String DOCTOR_CONTACTS = "/doctorcontacts/get";

	public static final String DOCTOR_CONTACTS_DOCTOR_SPECIFIC = "/doctorcontacts/get/{doctorId}";

	public static final String DOCTOR_CONTACTS_DOCTOR_SPECIFIC_CREATED_TIME = "/doctorcontacts/get/{doctorId}/{createdTime}";

	public static final String DOCTOR_CONTACTS_HANDHELD_DOCTOR_SPECIFIC = "/doctorcontacts/handheld/{doctorId}/{createdTime}";

	public static final String DOCTOR_CONTACTS_HANDHELD = "/doctorcontacts/handheld/{doctorId}/{locationId}/{hospitalId}/{createdTime}";

	public static final String BLOCK_CONTACT = "/doctorcontacts/block/{doctorId}/{patientId}";

	public static final String ADD_GROUP = "/doctorcontacts/addgroup";

	public static final String EDIT_GROUP = "/doctorcontacts/editgroup";

	public static final String GET_ALL_GROUPS = "/doctorcontacts/getallgroups/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_GROUPS_CREATED_TIME = "/doctorcontacts/getallgroups/{doctorId}/{locationId}/{hospitalId}/{createdTime}";

	public static final String GET_ALL_DOCTOR_SPECIFIC_GROUPS = "doctorcontacts/getallgroups/{doctorId}";

	public static final String GET_ALL_DOCTOR_SPECIFIC_GROUPS_CREATED_TIME = "doctorcontacts/getallgroups/{doctorId}/{createdTime}";

	public static final String DELETE_GROUP = "/doctorcontacts/deletegroup/{groupId}";

	public static final String TOTAL_COUNT = "/doctorcontacts/totalcount";

	public static final String IMPORT_CONTACTS = "/doctorContacts/importContacts";

	public static final String EXPORT_CONTACTS = "doctorContacts/exportContacts";
    }

    public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";

    public interface RegistrationUrls {
	public static final String PATIENT_REGISTER = "/patient";

	public static final String DOCTOR_REGISTER = "/doctor";

	public static final String EXISTING_PATIENTS_BY_PHONE_NUM = "/existing_patients/{mobileNumber}/{locationId}/{hospitalId}";

	public static final String EXISTING_PATIENTS_BY_PHONE_NUM_COUNT = "/existing_patients_count/{mobileNumber}";

	public static final String GET_PATIENT_PROFILE = "/getpatientprofile/{userId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String ADD_REFERRENCE = "/addreferrence";

	public static final String DELETE_REFERRENCE = "/deletereferrence/{referrenceId}";

	public static final String GET_REFERRENCES = "/getReferences/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_CUSTOM_REFERENCES = "/getCustomReferences/{doctorId}/{locationId}/{hospitalId}";

	public static final String PATIENT_ID_GENERATOR = "/generatePatientId/{doctorId}/{locationId}/{hospitalId}";

	public static final String UPDATE_PATIENT_ID_GENERATOR_LOGIC = "updatePatientIdGeneratorLogic/{doctorId}/{patientInitial}/{patientCounter}";

	public static final String GET_PATIENT_INITIAL_COUNTER = "getPatientInitialAndCounter/{doctorId}";

	public static final String GET_CLINIC_DETAILS = "settings/getClinicDetails/{clinicId}";

	public static final String UPDATE_CLINIC_PROFILE = "settings/updateClinicProfile";

	public static final String UPDATE_CLINIC_ADDRESS = "settings/updateClinicAddress";

	public static final String UPDATE_CLINIC_TIMING = "settings/updateClinicTiming";
    }

    public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

    public interface ClinicalNotesUrls {
	public static final String SAVE_CLINICAL_NOTE = "/save";

	public static final String EDIT_CLINICAL_NOTES = "/edit";

	public static final String DELETE_CLINICAL_NOTES = "/delete/{clinicalNotesId}";

	public static final String GET_CLINICAL_NOTES_ID = "/getbyid/{clinicalNotesId}";

	public static final String GET_CLINICAL_NOTES_DOCTOR_ID = "get/{doctorId}/{patientId}/{isOTPVerified}";

	public static final String GET_CLINICAL_NOTES_DOCTOR_ID_CT = "get/{doctorId}/{patientId}/{createdTime}/{isOTPVerified}";

	public static final String GET_CLINICAL_NOTES = "/get/{doctorId}/{locationId}/{hospitalId}/{patientId}/{isOTPVerified}";

	public static final String GET_CLINICAL_NOTES_CT = "/get/{doctorId}/{locationId}/{hospitalId}/{patientId}/{createdTime}/{isOTPVerified}";

	public static final String GET_CLINIC_NOTES_COUNT = "getClinicalNotesCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

	public static final String ADD_COMPLAINT = "/addcomplaint";

	public static final String ADD_OBSERVATION = "/addobservation";

	public static final String ADD_INVESTIGATION = "/addinvestigation";

	public static final String ADD_DIAGNOSIS = "/adddiagnosis";

	public static final String ADD_NOTES = "/addnotes";

	public static final String ADD_DIAGRAM = "/adddiagram";

	public static final String DELETE_COMPLAINT = "/deletecomplaint/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String DELETE_OBSERVATION = "/deleteobservation/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String DELETE_INVESTIGATION = "/deleteinvestigation/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String DELETE_DIAGNOSIS = "/deletediagnosis/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String DELETE_NOTE = "/deletenotes/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String DELETE_DIAGRAM = "/deletediagram/{id}/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_CUSTOM_COMPLAINTS = "getCustomComplaints/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";

	public static final String GET_CUSTOM_DIAGNOSIS = "getCustomDiagnosis/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";

	public static final String GET_CUSTOM_INVESTIGATIONS = "getCustomInvestigations/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";

	public static final String GET_CUSTOM_OBSERVATIONS = "getCustomObservations/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";

	public static final String GET_COMPLAINTS = "getComplaints/{doctorId}/{createdTime}";

	public static final String GET_INVESTIGATIONS = "getInvestigations/{doctorId}/{createdTime}";

	public static final String GET_OBSERVATIONS = "getObservations/{doctorId}/{createdTime}";

	public static final String GET_DIAGNOSIS = "getDiagnosis/{doctorId}/{createdTime}";

	public static final String GET_NOTES = "getNotes/{doctorId}/{createdTime}";

	public static final String GET_DIAGRAMS = "getDiagrams/{doctorId}/{createdTime}";

	public static final String GET_GLOBAL_DIAGRAMS = "getGlobalDiagrams/{createdTime}";

    }

    public static final String FORGOT_PASSWORD_BASE_URL = BASE_URL + "/forgotPassword";

    public interface ForgotPasswordUrls {
	public static final String FORGOT_PASSWORD_DOCTOR = "/forgotPasswordDoctor";

	public static final String FORGOT_PASSWORD_PATIENT = "/forgotPasswordPatient";

	public static final String RESET_PASSWORD = "/reset-password";

	public static final String FORGOT_USERNAME = "/forgot-username";
    }

    public static final String RECORDS_BASE_URL = BASE_URL + "/records";

    public interface RecordsUrls {
	public static final String ADD_RECORDS = "/addrecords";

	public static final String TAG_RECORD = "/tagrecord";

	public static final String CHANGE_LABEL_RECORD = "/changelabel";

	public static final String SEARCH_RECORD = "/search";

	public static final String SEARCH_RECORD_DOCTOR_ID = "/search/{doctorId}";

	public static final String SEARCH_RECORD_DOCTOR_ID_CT = "/search/{doctorId}/{createdTime}";

	public static final String SEARCH_RECORD_ALL_FIELDS = "/search/{doctorId}/{locationId}/{hospitalId}";

	public static final String SEARCH_RECORD_ALL_FIELDS_CT = "/search/{doctorId}/{locationId}/{hospitalId}/{createdTime}";

	public static final String GET_RECORD_COUNT = "getRecordCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

	public static final String CREATE_TAG = "/createtag";

	public static final String GET_ALL_TAGS = "/getalltags/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_PATIENT_EMAIL_ADD = "/getpatientemailaddr/{patientId}";

	public static final String EMAIL_RECORD = "/emailrecord/{recordId}/{emailAddress}";

	public static final String DELETE_RECORD = "/deleterecord/{recordId}";

	public static final String DOWNLOAD_RECORD = "/downloadrecord/{recordId}";

	public static final String DELETE_TAG = "/deletetag/{tagid}";

	public static final String EDIT_DESCRIPTION = "editDescription";

	public static final String GET_FLEXIBLE_COUNTS = "getFlexibleCounts";

	public static final String EDIT_RECORD = "/editRecord";

    }

    public static final String PRESCRIPTION_BASE_URL = BASE_URL + "/prescription";

    public interface PrescriptionUrls {
	public static final String ADD_DRUG = "addDrug";

	public static final String EDIT_DRUG = "editDrug";

	public static final String DELETE_DRUG = "deleteDrug/{drugId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_DRUG_ID = "getDrugs/{drugId}";

	public static final String GET_DRUGS_DOCTOR_SPECIFIC = "getDrugDetails/{doctorId}";

	public static final String GET_DRUGS_DOCTOR_SPECIFIC_CT = "getDrugDetails/{doctorId}/{createdTime}";

	public static final String GET_DRUGS_ALL_FIELDS = "getDrugDetails/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_DRUGS_ALL_FIELDS_CT = "getDrugDetails/{doctorId}/{hospitalId}/{locationId}/{createdTime}";

	public static final String DELETE_GLOBAL_DRUG = "deleteDrug/{drugId}";

	public static final String ADD_TEMPLATE = "addTemplate";

	public static final String ADD_TEMPLATE_HANDHELD = "addTemplateHandheld";

	public static final String EDIT_TEMPLATE = "editTemplate";

	public static final String DELETE_TEMPLATE = "deleteTemplate/{templateId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_TEMPLATE_TEMPLATE_ID = "getTemplate/{templateId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_TEMPLATE_DOCTOR_SPECIFIC = "getTemplates/{doctorId}";

	public static final String GET_TEMPLATE_ALL_FIELDS = "getTemplates/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_TEMPLATE_DOCTOR_SPECIFIC_CT = "getTemplates/{doctorId}/{createdTime}";

	public static final String GET_TEMPLATE_ALL_FIELDS_CT = "getTemplates/{doctorId}/{hospitalId}/{locationId}/{createdTime}";

	public static final String ADD_PRESCRIPTION = "addPrescription";

	public static final String ADD_PRESCRIPTION_HANDHELD = "addPrescriptionHandheld";

	public static final String EDIT_PRESCRIPTION = "editPrescription";

	public static final String DELETE_PRESCRIPTION = "deletePrescription/{prescriptionId}/{doctorId}/{hospitalId}/{locationId}/{patientId}";

	public static final String GET_PRESCRIPTION = "getPrescription/{doctorId}/{hospitalId}/{locationId}/{patientId}/{isOTPVarified}";

	public static final String GET_PRESCRIPTION_CREATED_TIME = "getPrescription/{doctorId}/{hospitalId}/{locationId}/{patientId}/{isOTPVarified}/{createdTime}";

	public static final String GET_PRESCRIPTION_COUNT = "getPrescriptionCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_DRUG_TYPE = "getAllDrugType";

	public static final String GET_CUSTOM_DRUG_TYPE = "getCustomDrugType/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_DRUG_STRENGTH_UNIT = "getAllDrugStrengthUnit";

	public static final String GET_CUSTOM_DRUG_STRENGTH_UNIT = "getCustomDrugStrengthUnit/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_DRUG_DOSAGE = "getAllDrugDosage";

	public static final String GET_CUSTOM_DRUG_DOSAGE = "getAllDrugDosage/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_DRUG_DURATION_UNIT = "getAllDrugDurationUnit";

	public static final String GET_CUSTOM_DRUG_DURATION_UNIT = "getCustomDrugDurationUnit/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_ALL_DRUG_DIRECTION_UNIT = "getAllDrugDirection";

	public static final String GET_CUSTOM_DRUG_DIRECTION_UNIT = "getCustomDrugDirection/{doctorId}/{locationId}/{hospitalId}";

	public static final String ADD_DRUG_TYPE = "addDrugType";

	public static final String EDIT_DRUG_TYPE = "editDrugType";

	public static final String DELETE_DRUG_TYPE = "deleteDrugType/{drugTypeId}";

	public static final String ADD_DRUG_STRENGTH = "addDrugStrength";

	public static final String EDIT_DRUG_STRENGTH = "editDrugStrength";

	public static final String DELETE_DRUG_STRENGTH = "deleteDrugStrength/{drugStrengthId}";

	public static final String ADD_DRUG_DOSAGE = "addDrugDosage";

	public static final String EDIT_DRUG_DOSAGE = "editDrugDosage";

	public static final String DELETE_DRUG_DOSAGE = "deleteDrugDosage/{drugDosageId}";

	public static final String ADD_DRUG_DIRECTION = "addDrugDirection";

	public static final String EDIT_DRUG_DIRECTION = "editDrugDirection";

	public static final String DELETE_DRUG_DIRECTION = "deleteDrugDirection/{drugDirectionId}";

	public static final String ADD_DRUG_DURATION_UNIT = "addDrugDurationUnit";

	public static final String EDIT_DRUG_DURATION_UNIT = "editDrugDurationUnit";

	public static final String DELETE_DRUG_DURATION_UNIT = "deleteDrugDurationUnit";

    }

    public static final String HISTORY_BASE_URL = BASE_URL + "/history";

    public interface HistoryUrls {
	public static final String ADD_DISEASE = "addDisease";

	public static final String EDIT_DISEASE = "editDisease";

	public static final String DELETE_DISEASE = "deleteDisease/{diseaseId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_DISEASES = "getDiseases/{doctorId}/{hospitalId}/{locationId}";

	public static final String ADD_REPORT_TO_HISTORY = "addReportToHistory/{reportId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String ADD_CLINICAL_NOTES_TO_HISTORY = "addClinicalNotesToHistory/{clinicalNotesId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String ADD_PRESCRIPTION_TO_HISTORY = "addPrescriptionToHistory/{prescriptionId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String ADD_SPECIAL_NOTES = "addSpecialNotes";

	public static final String ASSIGN_MEDICAL_HISTORY = "assignMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String ASSIGN_FAMILY_HISTORY = "assignFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String REMOVE_REPORTS = "removeReports/{reportId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String REMOVE_CLINICAL_NOTES = "removeClinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String REMOVE_PRESCRIPTION = "removePrescription/{prescriptionId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String REMOVE_MEDICAL_HISTORY = "removeMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String REMOVE_FAMILY_HISTORY = "removeFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{hospitalId}/{locationId}";

	public static final String GET_PATIENT_HISTORY_OTP_VERIFIED = "getPatientHistory/{patientId}/{doctorId}/{hospitalId}/{locationId}/{historyFilter}/{otpVerified}";
    }

    public static final String DOCTOR_PROFILE_URL = BASE_URL + "/doctorProfile";

    public interface DoctorProfileUrls {
	public static final String ADD_EDIT_NAME = "addEditName/{doctorId}/{title}/{fname}/{mname}/{lname}";

	public static final String ADD_EDIT_EXPERIENCE = "addEditExperience/{doctorId}/{experience}";

	public static final String ADD_EDIT_CONTACT = "addEditContact";

	public static final String ADD_EDIT_EDUCATION = "addEditEducation";

	public static final String ADD_EDIT_SPECIALITY = "addEditSpeciality";

	public static final String ADD_EDIT_ACHIEVEMENT = "addEditAchievement";

	public static final String ADD_EDIT_PROFESSIONAL_STATEMENT = "addEditProfessionalStatement/{doctorId}/{professionalStatement}";

	public static final String ADD_EDIT_REGISTRATION_DETAIL = "addEditRegistrationDetail";

	public static final String ADD_EDIT_EXPERIENCE_DETAIL = "addEditExperienceDetail";

	public static final String ADD_EDIT_PROFILE_PICTURE = "addEditProfilePicture";

	public static final String ADD_EDIT_PROFESSIONAL_MEMBERSHIP = "addEditProfessionalMembership";

	public static final String GET_DOCTOR_PROFILE = "getDoctorProfile/{doctorId}/{locationId}/{hospitalId}";

	public static final String ADD_EDIT_MEDICAL_COUNCILS = "addEditMedicalCouncils";

	public static final String GET_MEDICAL_COUNCILS = "getMedicalCouncils";

	public static final String INSERT_PROFESSIONAL_MEMBERSHIPS = "insertProfessionalMemberships";

	public static final String GET_PROFESSIONAL_MEMBERSHIPS = "getProfessionalMemberships";

	public static final String ADD_EDIT_APPOINTMENT_NUMBERS = "clinicProfile/addEditAppointmentNumbers";

	public static final String ADD_EDIT_VISITING_TIME = "clinicProfile/addEditVisitingTime";

	public static final String ADD_EDIT_CONSULTATION_FEE = "clinicProfile/addEditConsultationFee";

	public static final String ADD_EDIT_APPOINTMENT_SLOT = "clinicProfile/addEditAppointmentSlot";
    }

    /*
     * public interface SolrTemp { public static final String ADD = "/add";
     * 
     * public static final String DELETE = "/delete";
     * 
     * public static final String SEARCH = "/search/{text}";
     * 
     * public static final String ADD1 = "/add1";
     * 
     * public static final String SEARCH1 = "/search1/{text}";
     * 
     * }
     */

    public static final String SOLR_CLINICAL_NOTES_BASEURL = BASE_URL + "/solr/clinicalNotes";

    public interface SolrClinicalNotesUrls {
	public static final String SEARCH_COMPLAINTS = "searchComplaints/{searchTerm}";

	public static final String SEARCH_DIAGNOSES = "searchDiagnoses/{searchTerm}";

	public static final String SEARCH_NOTES = "searchNotes/{searchTerm}";

	public static final String SEARCH_DIAGRAMS = "searchDiagrams/{searchTerm}";

	public static final String SEARCH_INVESTIGATIONS = "searchInvestigations/{searchTerm}";

	public static final String SEARCH_OBSERVATIONS = "searchObservations/{searchTerm}";
    }

    public static final String SOLR_PRESCRIPTION_BASEURL = BASE_URL + "/solr/prescription";

    public interface SolrPrescriptionUrls {

	public static final String SEARCH_DRUG = "searchDrug/{searchTerm}";
    }

    public static final String SOLR_REGISTRATION_BASEURL = BASE_URL + "/solr/registration";

    public interface SolrRegistrationUrls {
	public static final String SEARCH_PATIENT = "searchPatient/{doctorId}/{locationId}/{hospitalId}/{searchTerm}";
    }
}
