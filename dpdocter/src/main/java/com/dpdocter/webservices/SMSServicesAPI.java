package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SMS_BASE_URL)
@Api(value = PathProxy.SMS_BASE_URL, description = "Endpoint for sms")
public class SMSServicesAPI {
    private static Logger logger = Logger.getLogger(SMSServicesAPI.class);

    @Autowired
    private SMSServices smsServices;

    @Autowired
    private TransactionalManagementService transactionalManagementService;

//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path(value = PathProxy.SMSUrls.SEND_SMS)
//    @POST
//    @ApiOperation(value = PathProxy.SMSUrls.SEND_SMS, notes = PathProxy.SMSUrls.SEND_SMS)
//    public Response<Boolean> sendSMS(SMSTrackDetail request) {
//	smsServices.sendSMS(request, true);
//	Response<Boolean> response = new Response<Boolean>();
//	response.setData(true);
//	return response;
//    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @ApiOperation(value = "GET_SMS", notes = "GET_SMS")
    public Response<SMSResponse> getSMS(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId) {
    	if(DPDoctorUtils.allStringsEmpty(doctorId, locationId)){
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	SMSResponse smsTrackDetails = smsServices.getSMS(page, size, doctorId, locationId, hospitalId);
	Response<SMSResponse> response = new Response<SMSResponse>();
	response.setData(smsTrackDetails);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.GET_SMS_DETAILS)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_DETAILS, notes = PathProxy.SMSUrls.GET_SMS_DETAILS)
    public Response<SMSTrack> getSMSDetails(@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam(value = "patientId") String patientId,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId) {
    	if(DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)){
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<SMSTrack> smsTrackDetails = smsServices.getSMSDetails(page, size, patientId, doctorId, locationId, hospitalId);
	Response<SMSTrack> response = new Response<SMSTrack>();
	response.setDataList(smsTrackDetails);
	return response;
    }

    @Path(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    @POST
    @ApiOperation(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS, notes = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    public String updateDeliveryReports(String request) {

	try {
	    request = request.replaceFirst("data=", "");
	    ObjectMapper mapper = new ObjectMapper();
	    @SuppressWarnings("deprecation")
		List<SMSDeliveryReports> list = mapper.readValue(request, TypeFactory.collectionType(List.class, SMSDeliveryReports.class));
	    smsServices.updateDeliveryReports(list);
	} catch (JsonParseException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (JsonMappingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (IOException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	}
	return "true";
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.ADD_NUMBER)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.ADD_NUMBER, notes = PathProxy.SMSUrls.ADD_NUMBER)
    public Response<Boolean> addNumber(@PathParam(value = "mobileNumber") String mobileNumber) {
	smsServices.addNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.DELETE_NUMBER)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.DELETE_NUMBER, notes = PathProxy.SMSUrls.DELETE_NUMBER)
    public Response<Boolean> deleteNumber(@PathParam(value = "mobileNumber") String mobileNumber) {
	smsServices.deleteNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT)
    @POST
    @ApiOperation(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT, notes = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT)
    public Response<SMSFormat> addSmsFormat(SMSFormat request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),request.getHospitalId())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	SMSFormat smsFormat = smsServices.addSmsFormat(request);
	Response<SMSFormat> response = new Response<SMSFormat>();
	response.setData(smsFormat);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.GET_SMS_FORMAT)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_FORMAT, notes = PathProxy.SMSUrls.GET_SMS_FORMAT)
    public Response<SMSFormat> getSmsFormat(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @QueryParam(value = "type") String type) {
    	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<SMSFormat> smsFormat = smsServices.getSmsFormat(doctorId, locationId, hospitalId, type);
	Response<SMSFormat> response = new Response<SMSFormat>();
	response.setDataList(smsFormat);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT, notes = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT)
    public Response<Boolean> sendPromotionalSMSToPatient() {
    	
	Boolean send = transactionalManagementService.sendPromotionalSMSToPatient();
	Response<Boolean> response = new Response<Boolean>();
	response.setData(send);
	return response;
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.SEND_BULK_SMS)
    @GET
    @ApiOperation(value = PathProxy.SMSUrls.SEND_BULK_SMS, notes = PathProxy.SMSUrls.SEND_BULK_SMS)
    public Response<String> sendBulkSMS(@MatrixParam("mobileNumbers") List<String> mobileNumbers, @PathParam(value = "message") String message) {
    	
	String send = smsServices.getBulkSMSResponse(mobileNumbers, message, null,null,0L);
	Response<String> response = new Response<String>();
	response.setData(send);
	return response;
    }
}
