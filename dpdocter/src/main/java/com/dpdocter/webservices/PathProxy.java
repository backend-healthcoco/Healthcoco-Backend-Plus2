package com.dpdocter.webservices;
/**
 * @author veeraj
 */
public interface PathProxy {

	public static final String HOME_URL = "/";
	public static final String BASE_URL = "api/v1";
	
	
	
	public static final String SIGNUP_BASE_URL = BASE_URL + "/signup";
	public interface SignUpUrls {
		public static final String DOCTER_SIGNUP = "/docter";
		public static final String PATIENT_SIGNUP = "/patient";
		public static final String ACTIVATE_USER = "/activate/{userId}";
	}
	
	public static final String LOGIN_BASE_URL = BASE_URL + "/login";
	public interface LoginUrls {
		public static final String LOGIN_USER = "/user";

	}

}