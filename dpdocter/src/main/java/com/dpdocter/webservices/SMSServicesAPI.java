package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.SMSServices;

import common.util.web.Response;

@Component
@Path(PathProxy.SMS_BASE_URL)
public class SMSServicesAPI {
    private static Logger logger = Logger.getLogger(SMSServicesAPI.class);

    @Autowired
    private SMSServices smsServices;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.SEND_SMS)
    @POST
    public Response<Boolean> sendSMS(SMSTrackDetail request) {
	smsServices.sendSMS(request, true);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    public Response<SMSResponse> getSMS(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId) {
	SMSResponse smsTrackDetails = smsServices.getSMS(page, size, doctorId, locationId, hospitalId);
	Response<SMSResponse> response = new Response<SMSResponse>();
	response.setData(smsTrackDetails);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.GET_SMS_DETAILS)
    @GET
    public Response<SMSTrack> getSMSDetails(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "patientId") String patientId,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId) {
	List<SMSTrack> smsTrackDetails = smsServices.getSMSDetails(page, size, patientId, doctorId, locationId, hospitalId);
	Response<SMSTrack> response = new Response<SMSTrack>();
	response.setDataList(smsTrackDetails);
	return response;
    }

    @Path(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    @POST
    public String updateDeliveryReports(String request) {

	try {
	    request = request.replaceFirst("data=", "");
	    ObjectMapper mapper = new ObjectMapper();
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
    public Response<Boolean> deleteNumber(@PathParam(value = "mobileNumber") String mobileNumber) {
	smsServices.deleteNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    /**
     * SMS Content : Pass whichever option is selected as string : PATIENT_NAME,
     * DOCTOR_NAME, APPOINTMENT_ID ,DATE_TIME ,CLINIC_NAME,CLINIC_CONTACT_NUMBER
     */

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT)
    @POST
    public Response<SMSFormat> addSmsFormat(SMSFormat request) {
	if (request == null) {
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
    public Response<SMSFormat> getSmsFormat(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @QueryParam(value = "type") String type) {
	List<SMSFormat> smsFormat = smsServices.getSmsFormat(doctorId, locationId, hospitalId, type);
	Response<SMSFormat> response = new Response<SMSFormat>();
	response.setDataList(smsFormat);
	return response;
    }
}
