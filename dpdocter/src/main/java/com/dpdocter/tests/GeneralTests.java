package com.dpdocter.tests;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

public class GeneralTests {
	@Autowired
	private RecordsRepository recordsRepository;

	public static void main(String args[]) {
		PatientProfilePicChangeRequest request = new PatientProfilePicChangeRequest();
		System.out.println(Converter.ObjectToJSON(request));
		System.out.println(Converter.ObjectToJSON(new FileDetails()));
		System.out.println(Converter.ObjectToJSON(new LoginRequest()));
		System.out.println(Converter.ObjectToJSON(new LoginResponse()));
		System.out.println(Converter.ObjectToJSON(new ResetPasswordRequest()));
		// System.out.println(PathProxy.FORGOT_PASSWORD_BASE_URL +
		// PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR);
		// System.out.println("Request : " + Converter.ObjectToJSON(request));
		// System.out.println("Response : " + Converter.ObjectToJSON(new
		// Response<String>()));
		// System.out.println(PathProxy.FORGOT_PASSWORD_BASE_URL +
		// PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT);
		// System.out.println("Request : " + Converter.ObjectToJSON(request));
		// System.out.println("Response : " + Converter.ObjectToJSON(new
		// Response<Boolean>()));
		// System.out.println(PathProxy.FORGOT_PASSWORD_BASE_URL +
		// PathProxy.ForgotPasswordUrls.FORGOT_USERNAME);
		// System.out.println("Request : " + Converter.ObjectToJSON(request));
		// System.out.println("Response : " + Converter.ObjectToJSON(new
		// Response<Boolean>()));
		// System.out.println(PathProxy.FORGOT_PASSWORD_BASE_URL +
		// PathProxy.ForgotPasswordUrls.RESET_PASSWORD);
		// System.out.println("Request : " + Converter.ObjectToJSON(new
		// ResetPasswordRequest()));
		// System.out.println("Response : " + Converter.ObjectToJSON(new
		// Response<String>()));
	}
}
