package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.services.PatientVisitService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.PATIENT_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientVisitApi {

    private static Logger logger = Logger.getLogger(PatientVisitApi.class.getName());

    @Autowired
    private PatientVisitService patientVisitService;

    @Path(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
    @POST
    public Response<PatientVisitResponse> addMultipleData(AddMultipleDataRequest request) {

	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	PatientVisitResponse patienVisitResponse = patientVisitService.addMultipleData(request);
	Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
	response.setData(patienVisitResponse);
	return response;
    }

    @Path(value = PathProxy.PatientVisitUrls.EMAIL)
    @GET
    public Response<Boolean> email(@PathParam(value = "visitId") String visitId, @PathParam(value = "emailAddress") String emailAddress) {

	if (DPDoctorUtils.anyStringEmpty(visitId, emailAddress)) {
	    logger.warn("Visit Id Or Email AddressIs NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Visit Id Or Email Address Is NULL");
	}
	Boolean isSend = patientVisitService.email(visitId, emailAddress);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isSend);
	return response;
    }

    @Path(value = PathProxy.PatientVisitUrls.GET_VISITS)
    @GET
    public Response<PatientVisitResponse> getVisit(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "patientId") String patientId, @QueryParam(value = "page") int page,
	    @QueryParam(value = "size") int size, @DefaultValue("false") @QueryParam("isOTPVerified") Boolean isOTPVerified,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

	if (StringUtils.isEmpty(patientId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId, patientId, page, size, isOTPVerified,
		updatedTime);

	Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
	response.setDataList(patienVisitResponse);
	return response;
    }

    @Path(value = PathProxy.PatientVisitUrls.DELETE_VISITS)
    @GET
    public Response<Boolean> deleteVisit(@PathParam(value = "visitId") String visitId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	if (StringUtils.isEmpty(visitId)) {
	    logger.warn("Visit Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Visit Id Cannot Be Empty");
	}
	Boolean patienVisitResponse = patientVisitService.deleteVisit(visitId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(patienVisitResponse);
	return response;
    }
    
    @Path(value = PathProxy.PatientVisitUrls.SMS_VISITS)
    @GET
    public Response<Boolean> smsPrescription(@PathParam(value = "visitId") String visitId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "mobileNumber") String mobileNumber) {

	if (DPDoctorUtils.anyStringEmpty(visitId, doctorId, locationId, hospitalId, mobileNumber)) {
	    logger.warn("Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput,
		    "Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
	}
	patientVisitService.smsVisit(visitId, doctorId, locationId, hospitalId, mobileNumber);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }


}
