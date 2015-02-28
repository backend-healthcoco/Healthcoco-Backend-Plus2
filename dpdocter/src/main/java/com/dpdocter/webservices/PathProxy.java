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
		public static final String GET_ALL_GROUPS = "/doctorcontacts/getallgroups/{doctorId}";
		public static final String DELETE_GROUP = "/doctorcontacts/deletegroup/{groupId}";
		public static final String TOTAL_COUNT = "/doctorcontacts/totalcount";
	}
	public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";
	public interface RegistrationUrls {
		public static final String PATIENT_REGISTER = "/patient";
		public static final String DOCTOR_REGISTER = "/doctor";
		public static final String EXISTING_PATIENTS_BY_PHONE_NUM = "/existing_patients/{phoneNumber}";
		public static final String GET_PATIENT_PROFILE = "/getpatientprofile/{userId}/{doctorId}";

	}
	
	public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";
	public interface ClinicalNotesUrls {
		public static final String ADD_NOTES = "/add";
		public static final String EDIT_NOTES = "/edit";
		public static final String DELETE_NOTES = "/delete/{clinicalNotesId}";
		public static final String GET_CLINICAL_NOTES_ID = "/getbyid/{clinicalNotesId}";
		public static final String GET_CLINICAL_NOTES = "/get/{doctorId}/{patientId}/{isOTPVarified}";
		

	}
	public static final String FORGOT_PASSWORD_BASE_URL = BASE_URL + "/forgot-password";
	public interface ForgotPasswordUrls {
		public static final String FORGOT_PASSWORD = "/forgot";
		public static final String RESET_PASSWORD = "/reset-password";
	}
	
	public static final String RECORDS_BASE_URL = BASE_URL + "/records";
	public interface RecordsUrls {
		public static final String ADD_RECORDS = "/addrecords";
		public static final String TAG_RECORD = "/tagrecord";
		public static final String CHANGE_LABEL_RECORD = "/changelabel";
		public static final String SEARCH_RECORD = "/search";
		
	}

}