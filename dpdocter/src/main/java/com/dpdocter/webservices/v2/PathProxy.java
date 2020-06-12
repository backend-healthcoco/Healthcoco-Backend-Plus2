package com.dpdocter.webservices.v2;

public interface PathProxy {

	public static final String HOME_URL = "/";

	public static final String BASE_URL = "/v2";
	
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

	
	public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";

	public interface RegistrationUrls {
		
		public static final String GET_USERS = "/users/{locationId}/{hospitalId}";
	}
	
	public static final String ADMIT_CARD_URL = BASE_URL + "/admitCard";
	
	public interface AdmitCardUrls {

		public static final String GET_ADMIT_CARDS = "/getAdmitCard";
		//public static final String ADD_ADMIT_CARD = "/add";
		//public static final String VIEW_ADMIT_CARD = "/view/{admitCardId}";
		//public static final String DELETE_ADMIT_CARD = "/{admitCardId}/{doctorId}/{locationId}/{hospitalId}/delete";
		//public static final String DOWNLOAD_ADMIT_CARD = "/download/{admitCardId}/";
		//public static final String EMAIL_ADMIT_CARD = "/{admitCardId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";
		//public static final String EMAIL_ADMIT_CARD_WEB = "/{admitCardId}/{emailAddress}/mail";

	}
	
	public static final String REPORTS_BASE_URL = BASE_URL + "/reports";

	public interface ReportsUrls {
		public static final String GET_IPD_REPORTS = "/getIPDReports";
		public static final String GET_OPD_REPORTS = "/getOPDReports";
		public static final String GET_OT_REPORTS = "/getOTReports";
		public static final String GET_DELIVERY_REPORTS = "/getDeliveryReports";
	}
	
	public static final String PRESCRIPTION_BASE_URL = BASE_URL + "/prescription";

	public interface PrescriptionUrls {
		
		public static final String SEARCH_DRUGS = "/searchDrug";
		public static final String GET_PRESCRIPTIONS_FOR_EMR = "/getEMR";
		public static final String GET_DRUGS_BY_CODE = "/getDrugByCode";
		
	}
	
	public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

	public interface ClinicalNotesUrls {
		
	}
	
	public static final String PATIENT_TREATMENT_BASE_URL = BASE_URL + "/treatment";

	public interface PatientTreatmentURLs {
		
	}
	
	public static final String PATIENT_VISIT_BASE_URL = BASE_URL + "/patientVisit";
	
	public interface PatientVisitUrls {

		public static final String GET_VISITS = "/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_VISITS_FOR_WEB = "/get";
	}
	
	public static final String DISCHARGE_SUMMARY_BASE_URL = BASE_URL + "/dischargeSummary";

	public interface DischargeSummaryUrls {
		public static final String GET_DISCHARGE_SUMMARY = "/getDischargeSummery";
	}
	
	public static final String SOLR_PRESCRIPTION_BASEURL = BASE_URL + "/solr/prescription";
	
	public interface SolrPrescriptionUrls {

		public static final String SEARCH_DRUG = "searchDrug/{range}";

	}
	
	public static final String SIGNUP_BASE_URL = BASE_URL + "/signup";
	
	public interface SignUpUrls {
	
	public static final String DOCTOR_SIGNUP = "/doctor";
	
	public static final String VERIFY_USER = "/verify/{tokenId}";
	
	public static final String DOCTOR_REGISTER = "/doctorRegister";
	
	public static final String RESEND_VERIFICATION_EMAIL_TO_DOCTOR = "/resendVerificationEmail/{emailaddress}";

	
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


}
