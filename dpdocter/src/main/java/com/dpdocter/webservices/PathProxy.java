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

	public static final String ACTIVATE_USER = "/activate/{tokenId}";

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

	public static final String DOCTOR_REGISTER = "/doctor";

	public static final String EXISTING_PATIENTS_BY_PHONE_NUM = "/existing_patients/{mobileNumber}/{locationId}/{hospitalId}";

	public static final String EXISTING_PATIENTS_BY_PHONE_NUM_COUNT = "/existing_patients_count/{mobileNumber}";

	public static final String GET_PATIENT_PROFILE = "/getpatientprofile/{userId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String ADD_REFERRENCE = "/referrence/add";

	public static final String DELETE_REFERRENCE = "/referrence/{referrenceId}/delete";

	public static final String GET_REFERRENCES = "/getReferences/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_CUSTOM_REFERENCES = "/getCustomReferences/{doctorId}/{locationId}/{hospitalId}";

	public static final String PATIENT_ID_GENERATOR = "/generatePatientId/{doctorId}/{locationId}/{hospitalId}";

	public static final String UPDATE_PATIENT_ID_GENERATOR_LOGIC = "/updatePatientIdGeneratorLogic/{doctorId}/{locationId}/{patientInitial}/{patientCounter}";

	public static final String GET_PATIENT_INITIAL_COUNTER = "/getPatientInitialAndCounter/{doctorId}/{locationId}";

	public static final String GET_CLINIC_DETAILS = "/settings/getClinicDetails/{clinicId}";

	public static final String UPDATE_CLINIC_PROFILE = "/settings/updateClinicProfile";

	public static final String UPDATE_CLINIC_ADDRESS = "/settings/updateClinicAddress";

	public static final String UPDATE_CLINIC_TIMING = "/settings/updateClinicTiming";
	
	public static final String CHANGE_CLINIC_LOGO = "/settings/changeClinicLogo";
	
	public static final String ADD_CLINIC_IMAGE = "/settings/clinicImage/add";
	
	public static final String DELETE_CLINIC_IMAGE = "/settings/clinicImage/{locationId}/{counter}/delete";
	
	public static final String ADD_BLOOD_GROUP = "/settings/bloodGroup/add";
	
	public static final String GET_BLOOD_GROUP = "/settings/bloodGroup";
	
	public static final String ADD_PROFESSION = "/settings/profession/add";
	
	public static final String GET_PROFESSION = "/settings/profession";
    
 }

    public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

    public interface ClinicalNotesUrls {
	public static final String SAVE_CLINICAL_NOTE = "/add";

	public static final String EDIT_CLINICAL_NOTES = "/{clinicalNotesId}/update";

	public static final String DELETE_CLINICAL_NOTES = "/{clinicalNotesId}/delete";

	public static final String GET_CLINICAL_NOTES_ID = "/{clinicalNotesId}/view";

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
	public static final String ADD_RECORDS = "/add";

	public static final String TAG_RECORD = "/tagrecord";

	public static final String CHANGE_LABEL_RECORD = "/changelabel";

	public static final String SEARCH_RECORD = "/search";

	public static final String GET_RECORD_COUNT = "/getRecordCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

	public static final String CREATE_TAG = "/createtag";

	public static final String GET_ALL_TAGS = "/getalltags/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_PATIENT_EMAIL_ADD = "/getpatientemailaddr/{patientId}";

	public static final String EMAIL_RECORD = "/emailrecord/{recordId}/{emailAddress}";

	public static final String DELETE_RECORD = "/{recordId}/delete";

	public static final String DOWNLOAD_RECORD = "/download/{recordId}";

	public static final String DELETE_TAG = "/tagrecord/{tagid}/delete";

	public static final String EDIT_DESCRIPTION = "/editDescription";

	public static final String GET_FLEXIBLE_COUNTS = "/getFlexibleCounts";

	public static final String EDIT_RECORD = "/{recordId}/update";

    }

    public static final String PRESCRIPTION_BASE_URL = BASE_URL + "/prescription";

    public interface PrescriptionUrls {
	public static final String ADD_DRUG = "/drug/add";

	public static final String EDIT_DRUG = "/drug/{drugId}/update";

	public static final String DELETE_DRUG = "/drug/{drugId}/{doctorId}/{locationId}/{hospitalId}/delete";

	public static final String GET_DRUG_ID = "/drug/{drugId}";

	public static final String DELETE_GLOBAL_DRUG = "/drug/{drugId}/delete";

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

	public static final String DELETE_DRUG_DOSAGE = "/drugDosage/{drugDosageId}/delete";

	public static final String ADD_DRUG_DIRECTION = "/drugDirection/add";

	public static final String EDIT_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/update";

	public static final String DELETE_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/delete";

	public static final String ADD_DRUG_DURATION_UNIT = "/drugDurationUnit/add";

	public static final String EDIT_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/update";

	public static final String DELETE_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/delete";

    }

    public static final String HISTORY_BASE_URL = BASE_URL + "/history";

    public interface HistoryUrls {
	public static final String ADD_DISEASE = "/disease/add";

	public static final String EDIT_DISEASE = "/disease{diseaseId}//update";

	public static final String DELETE_DISEASE = "/disease/{diseaseId}/{doctorId}/{locationId}/{hospitalId}/delete";

	public static final String GET_DISEASES = "/diseases/{range}";

	public static final String ADD_REPORT_TO_HISTORY = "/report/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

	public static final String ADD_CLINICAL_NOTES_TO_HISTORY = "/clinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

	public static final String ADD_PRESCRIPTION_TO_HISTORY = "/prescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

	public static final String ADD_SPECIAL_NOTES = "/addSpecialNotes";

	public static final String ASSIGN_MEDICAL_HISTORY = "/assignMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String ASSIGN_FAMILY_HISTORY = "/assignFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String HANDLE_MEDICAL_HISTORY = "/medicalHistory";

	public static final String GET_MEDICAL_AND_FAMILY_HISTORY = "/getMedicalAndFamilyHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String HANDLE_FAMILY_HISTORY = "/familyHistory";

	public static final String REMOVE_REPORTS = "/removeReports/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String REMOVE_CLINICAL_NOTES = "/removeClinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String REMOVE_PRESCRIPTION = "/removePrescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String REMOVE_MEDICAL_HISTORY = "/removeMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String REMOVE_FAMILY_HISTORY = "/removeFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

	public static final String GET_PATIENT_HISTORY_OTP_VERIFIED = "/getPatientHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}/{historyFilter}/{otpVerified}";
    }

    public static final String DOCTOR_PROFILE_URL = BASE_URL + "/doctorProfile";

    public interface DoctorProfileUrls {
	public static final String ADD_EDIT_NAME = "/addEditName";

	public static final String ADD_EDIT_EXPERIENCE = "/addEditExperience/{doctorId}/{experience}";

	public static final String ADD_EDIT_CONTACT = "/addEditContact";

	public static final String ADD_EDIT_EDUCATION = "/addEditEducation";

	public static final String ADD_EDIT_SPECIALITY = "/addEditSpeciality";

	public static final String ADD_EDIT_ACHIEVEMENT = "/addEditAchievement";

	public static final String ADD_EDIT_PROFESSIONAL_STATEMENT = "/addEditProfessionalStatement";

	public static final String ADD_EDIT_REGISTRATION_DETAIL = "/addEditRegistrationDetail";

	public static final String ADD_EDIT_EXPERIENCE_DETAIL = "/addEditExperienceDetail";

	public static final String ADD_EDIT_PROFILE_PICTURE = "/addEditProfilePicture";

	public static final String ADD_EDIT_PROFESSIONAL_MEMBERSHIP = "/addEditProfessionalMembership";

	public static final String GET_DOCTOR_PROFILE = "/{doctorId}/{locationId}/{hospitalId}/view";

	public static final String ADD_EDIT_MEDICAL_COUNCILS = "/addEditMedicalCouncils";

	public static final String GET_MEDICAL_COUNCILS = "/getMedicalCouncils";

	public static final String INSERT_PROFESSIONAL_MEMBERSHIPS = "/insertProfessionalMemberships";

	public static final String GET_PROFESSIONAL_MEMBERSHIPS = "/getProfessionalMemberships";

	public static final String ADD_EDIT_APPOINTMENT_NUMBERS = "/clinicProfile/addEditAppointmentNumbers";

	public static final String ADD_EDIT_VISITING_TIME = "/clinicProfile/addEditVisitingTime";

	public static final String ADD_EDIT_CONSULTATION_FEE = "/clinicProfile/addEditConsultationFee";

	public static final String ADD_EDIT_APPOINTMENT_SLOT = "/clinicProfile/addEditAppointmentSlot";
    }

    public static final String PATIENT_TRACK_BASE_URL = BASE_URL + "/patientTrack";

//    public interface PatientTrackUrls {
//	public static final String RECENTLY_VISITED = "recentlyVisited/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";
//
//	public static final String MOST_VISITED = "mostVisited/{doctorId}/{locationId}/{hospitalId}/{page}/{size}";
//    }

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

	public static final String SEARCH_PATIENT_ADV = "searchPatient";
    }

    public static final String CITY_BASE_URL = BASE_URL + "/city";

    public interface CityUrls {

	public static final String ADD_CITY = "/addCity";

	public static final String ACTIVATE_CITY = "/activateCity/{cityId}";

	public static final String DEACTIVATE_CITY = "/deactivateCity/{cityId}";

	public static final String GET_CITY = "/getCities";

	public static final String GET_CITY_ID = "/getCity/{cityId}";

	public static final String ADD_LOCALITY = "/addLocality";

	public static final String ADD_LANDMARK = "/addLandmark";

	public static final String GET_LANDMARK_LOCALITY = "/getLandmarkLocality/{cityId}";

    }

    public static final String SOLR_CITY_BASE_URL = BASE_URL + "/solr/city";

    public interface SolrCityUrls {
	public static final String SEARCH_CITY = "searchCity/{searchTerm}";

	public static final String SEARCH_LANDMARK_LOCALITY = "searchLandmarkLocality/{cityId}/{searchTerm}";
    }

}
