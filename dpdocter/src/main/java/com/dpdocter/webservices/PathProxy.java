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

	}

	public static final String CONTACTS_BASE_URL = BASE_URL + "/contacts";

	public interface ContactsUrls {
		public static final String DOCTOR_CONTACTS = "/doctorcontacts/get";
		public static final String BLOCK_CONTACT = "/doctorcontacts/block/{doctorId}/{patientId}";
		public static final String ADD_GROUP = "/doctorcontacts/addgroup";
		public static final String EDIT_GROUP = "/doctorcontacts/editgroup";
		public static final String GET_ALL_GROUPS = "/doctorcontacts/getallgroups/{doctorId}/{locationId}/{hospitalId}";
		public static final String DELETE_GROUP = "/doctorcontacts/deletegroup/{groupId}";
		public static final String TOTAL_COUNT = "/doctorcontacts/totalcount";
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
		public static final String GET_REFERRENCES = "/getreferrences/{doctorId}/{locationId}/{hospitalId}";
		public static final String PATIENT_ID_GENERATOR = "/generatePatientId/{doctorId}/{locationId}/{hospitalId}";
	}

	public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

	public interface ClinicalNotesUrls {
		public static final String SAVE_CLINICAL_NOTE = "/save";
		public static final String EDIT_CLINICAL_NOTES = "/edit";
		public static final String DELETE_CLINICAL_NOTES = "/delete/{clinicalNotesId}";
		public static final String GET_CLINICAL_NOTES_ID = "/getbyid/{clinicalNotesId}";
		public static final String GET_CLINICAL_NOTES = "/get/{doctorId}/{patientId}/{isOTPVarified}";

		public static final String ADD_COMPLAINT = "/addcomplaint";
		public static final String ADD_OBSERVATION = "/addobservation";
		public static final String ADD_INVESTIGATION = "/addinvestigation";
		public static final String ADD_DIAGNOSIS = "/adddiagnosis";
		public static final String ADD_NOTES = "/addnotes";
		public static final String ADD_DIAGRAM = "/adddiagram";

		public static final String DELETE_COMPLAINT = "/deletecomplaint/{id}/{doctorId}";
		public static final String DELETE_OBSERVATION = "/deleteobservation/{id}/{doctorId}";
		public static final String DELETE_INVESTIGATION = "/deleteinvestigation/{id}/{doctorId}";
		public static final String DELETE_DIAGNOSIS = "/deletediagnosis/{id}/{doctorId}";
		public static final String DELETE_NOTE = "/deletenotes/{id}/{doctorId}";
		public static final String DELETE_DIAGRAM = "/deletediagram/{id}/{doctorId}";

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
		public static final String CREATE_TAG = "/createtag";
		public static final String GET_ALL_TAGS = "/getalltags/{doctorId}/{locationId}/{hospitalId}";
		public static final String GET_PATIENT_EMAIL_ADD = "/getpatientemailaddr/{patientId}";
		public static final String EMAIL_RECORD = "/emailrecord/{recordId}/{emailAddress}";
		public static final String DELETE_RECORD = "/deleterecord/{recordId}";
		public static final String DOWNLOAD_RECORD = "/downloadrecord/{recordId}";
		public static final String DELETE_TAG = "/deletetag/{tagid}";

	}

}
