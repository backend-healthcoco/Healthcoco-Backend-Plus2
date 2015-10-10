package com.dpdocter.sms.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.sms.services.SMSServices;
import com.dpdocter.webservices.PathProxy;
import common.util.web.Response;

@Component
@Path(PathProxy.SMS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SMSServicesAPI {
    private static Logger logger = Logger.getLogger(SMSServicesAPI.class);

    @Autowired
    private SMSServices smsServices;

    @Path(value = PathProxy.SMSUrls.SEND_SMS)
    @POST
    public Response<Boolean> sendSMS(SMSTrackDetail request) {
	smsServices.sendSMS(request, true);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @GET
    public Response<SMSResponse> getSMS(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId) {
	SMSResponse smsTrackDetails = smsServices.getSMS(page, size, doctorId, locationId, hospitalId);
	Response<SMSResponse> response = new Response<SMSResponse>();
	response.setData(smsTrackDetails);
	return response;
    }

    @Path(value = PathProxy.SMSUrls.GET_SMS_DETAILS)
    @GET
    public Response<SMSTrack> getSMSDetails(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId) {
	List<SMSTrack> smsTrackDetails = smsServices.getSMSDetails(page, size, doctorId, locationId, hospitalId);
	Response<SMSTrack> response = new Response<SMSTrack>();
	response.setDataList(smsTrackDetails);
	return response;
    }

    @Path(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    @POST
    public Response<Boolean> updateDeliveryReports(List<SMSDeliveryReports> request) {
	System.out.println("updating delivery Reports");
	smsServices.updateDeliveryReports(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }
}
