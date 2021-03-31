package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
(PathProxy.SMS_BASE_URL)
@Api(value = PathProxy.SMS_BASE_URL, description = "Endpoint for sms")
public class SMSServicesAPI {
    private static Logger logger = LogManager.getLogger(SMSServicesAPI.class);

    @Autowired
    private SMSServices smsServices;

    @Autowired
    private TransactionalManagementService transactionalManagementService;

//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    (value = PathProxy.SMSUrls.SEND_SMS)
//    @PostMapping
//    @ApiOperation(value = PathProxy.SMSUrls.SEND_SMS, notes = PathProxy.SMSUrls.SEND_SMS)
//    public Response<Boolean> sendSMS(SMSTrackDetail request) {
//	smsServices.sendSMS(request, true);
//	Response<Boolean> response = new Response<Boolean>();
//	response.setData(true);
//	return response;
//    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GetMapping
    @ApiOperation(value = "GET_SMS", notes = "GET_SMS")
    public Response<SMSResponse> getSMS(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
	    @RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId) {
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
    @GetMapping(value = PathProxy.SMSUrls.GET_SMS_DETAILS)
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_DETAILS, notes = PathProxy.SMSUrls.GET_SMS_DETAILS)
    public Response<SMSTrack> getSMSDetails(@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "patientId") String patientId,
	    @RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
	    @RequestParam(value = "hospitalId") String hospitalId) {
    	if(DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)){
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<SMSTrack> smsTrackDetails = smsServices.getSMSDetails(page, size, patientId, doctorId, locationId, hospitalId);
	Response<SMSTrack> response = new Response<SMSTrack>();
	response.setDataList(smsTrackDetails);
	return response;
    }

    
    @PostMapping(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
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
    @GetMapping(value = PathProxy.SMSUrls.ADD_NUMBER)
    @ApiOperation(value = PathProxy.SMSUrls.ADD_NUMBER, notes = PathProxy.SMSUrls.ADD_NUMBER)
    public Response<Boolean> addNumber(@PathVariable(value = "mobileNumber") String mobileNumber) {
	smsServices.addNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GetMapping(value = PathProxy.SMSUrls.DELETE_NUMBER)
    @ApiOperation(value = PathProxy.SMSUrls.DELETE_NUMBER, notes = PathProxy.SMSUrls.DELETE_NUMBER)
    public Response<Boolean> deleteNumber(@PathVariable(value = "mobileNumber") String mobileNumber) {
	smsServices.deleteNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PostMapping(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT)
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
    @GetMapping(value = PathProxy.SMSUrls.GET_SMS_FORMAT)
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_FORMAT, notes = PathProxy.SMSUrls.GET_SMS_FORMAT)
    public Response<SMSFormat> getSmsFormat(@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
	    @PathVariable(value = "hospitalId") String hospitalId, @RequestParam(value = "type") String type) {
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
    @GetMapping(value = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT)
    @ApiOperation(value = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT, notes = PathProxy.SMSUrls.SEND_DOWNLOAD_APP_SMS_TO_PATIENT)
    public Response<Boolean> sendPromotionalSMSToPatient() {
    	
	Boolean send = transactionalManagementService.sendPromotionalSMSToPatient();
	Response<Boolean> response = new Response<Boolean>();
	response.setData(send);
	return response;
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GetMapping(value = PathProxy.SMSUrls.SEND_BULK_SMS)
    @ApiOperation(value = PathProxy.SMSUrls.SEND_BULK_SMS, notes = PathProxy.SMSUrls.SEND_BULK_SMS)
    public Response<String> sendBulkSMS(@MatrixParam("mobileNumbers") List<String> mobileNumbers, @PathVariable(value = "message") String message) {
    	
	String send = smsServices.getBulkSMSResponse(mobileNumbers, message, null,null,0L);
	Response<String> response = new Response<String>();
	response.setData(send);
	return response;
    }
}
