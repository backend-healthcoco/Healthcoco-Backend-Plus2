package com.dpdocter.tests;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.webservices.PathProxy;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Converter {

    public static String ObjectToJSON(Object value) {
	ObjectMapper objectMapper = new ObjectMapper();
	String JSONResult = "";
	try {
	    JSONResult = objectMapper.writeValueAsString(value);
	} catch (JsonGenerationException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return JSONResult;
    }

    public static void main(String[] args) {
	String JSONResult = ObjectToJSON(new DoctorSignupRequest());
	System.out.println("Doctor Signup : " + PathProxy.SIGNUP_BASE_URL + PathProxy.SignUpUrls.DOCTOR_SIGNUP);
	System.out.println("Doctor Signup Request : " + JSONResult);
	JSONResult = ObjectToJSON(new PatientSignUpRequest());
	System.out.println("Patient Signup : " + PathProxy.SIGNUP_BASE_URL + PathProxy.SignUpUrls.PATIENT_SIGNUP);
	System.out.println("Patient Signup Request : " + JSONResult);

	ForgotUsernamePasswordRequest request = new ForgotUsernamePasswordRequest();
	request.setEmailAddress("");
	request.setMobileNumber("0123456789");
	request.setUsername("");
	System.out.println(ObjectToJSON(request));
    }
}
