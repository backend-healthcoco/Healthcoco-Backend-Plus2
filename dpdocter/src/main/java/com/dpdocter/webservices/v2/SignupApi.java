package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorOtpRequest;
import com.dpdocter.response.DoctorRegisterResponse;
import com.dpdocter.beans.v2.DoctorSignupRequest;
import com.dpdocter.services.v2.SignUpService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.webservices.v2.PathProxy;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Component(value = "SignUpApiV2")
@Path(PathProxy.SIGNUP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SIGNUP_BASE_URL, description = "")

public class SignupApi {
	
	@Autowired
	private SignUpService signUpService;

	@Autowired
	private TransactionalManagementService transnationalService;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;


	
	private Logger logger = Logger.getLogger(SignupApi.class);
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	public Response<DoctorSignUp> doctorSignup(DoctorSignupRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getFirstName(), request.getEmailAddress(),request.getCity(),
				request.getMobileNumber())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (request.getFirstName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request);
		if (doctorSignUp != null) {
			if (doctorSignUp.getUser() != null) {
				if (doctorSignUp.getUser().getImageUrl() != null) {
					doctorSignUp.getUser().setImageUrl(getFinalImageURL(doctorSignUp.getUser().getImageUrl()));
				}
				if (doctorSignUp.getUser().getThumbnailUrl() != null) {
					doctorSignUp.getUser().setThumbnailUrl(getFinalImageURL(doctorSignUp.getUser().getThumbnailUrl()));
				}
			}
			if (doctorSignUp.getHospital() != null) {
				if (doctorSignUp.getHospital().getHospitalImageUrl() != null) {
					doctorSignUp.getHospital()
							.setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
				}
			}
			transnationalService.checkDoctor(new ObjectId(doctorSignUp.getUser().getId()), null);

		}

		Response<DoctorSignUp> response = new Response<DoctorSignUp>();
		response.setData(doctorSignUp);
		return response;
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.VERIFY_USER)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.VERIFY_USER, notes = PathProxy.SignUpUrls.VERIFY_USER)
	public Response<String> verifyUser(@PathParam(value = "tokenId") String tokenId) {
		if (tokenId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String string = signUpService.verifyUser(tokenId);
		Response<String> response = new Response<String>();
		response.setData(string);
		return response;
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.DOCTOR_REGISTER)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_REGISTER, notes = PathProxy.SignUpUrls.DOCTOR_REGISTER)
	 public Response<DoctorRegisterResponse> DoctorRegister(@RequestBody DoctorOtpRequest request) {
	//	@QueryParam(value = "mobileNumber") String mobileNumber,
	//	 @QueryParam(value = "countryCode") String countryCode
			if (request == null || request.getMobileNumber().isEmpty()) {
			    logger.warn("Mobile number is null");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			DoctorRegisterResponse registerResponse = signUpService.DoctorRegister(request);
		
			Response<DoctorRegisterResponse> response = new Response<DoctorRegisterResponse>();
			if (response != null)
			    response.setData(registerResponse);
			
		    
			return response;
		}
	

	private String getFinalImageURL(String imageURL) {
		return imagePath + imageURL;
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR, notes = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
	public Response<Boolean> resendVerificationEmail(@PathParam(value = "emailaddress") String emailaddress) {
		if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.resendVerificationEmail(emailaddress));
		return response;
	}
	
	
}
