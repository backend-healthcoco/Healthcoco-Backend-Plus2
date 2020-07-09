package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.OnlineConsultationAnalytics;
import com.dpdocter.services.OnlineConsultationService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "OnlineConsultationAnalyticsApi")
@Path(PathProxy.ONLINE_CONSULTATION_ANALYTICS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ONLINE_CONSULTATION_ANALYTICS_BASE_URL, description = "Endpoint for appointment")
public class OnlineConsultationAnalyticsApi {
	
	@Autowired
	private OnlineConsultationService onlineConsultationService;
	
	@Path(value = PathProxy.OnlineConsultationAnalyticsUrls.ONLINE_CONSULTATION_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.OnlineConsultationAnalyticsUrls.ONLINE_CONSULTATION_ANALYTICS, notes = PathProxy.OnlineConsultationAnalyticsUrls.ONLINE_CONSULTATION_ANALYTICS)
	public Response<OnlineConsultationAnalytics> getPatientAppointments(@QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "type") String type,
			@QueryParam(value = "fromDate") String fromDate, @QueryParam(value = "toDate") String toDate) {

		Response<OnlineConsultationAnalytics> response =new Response<OnlineConsultationAnalytics>();
		response.setData(onlineConsultationService.getConsultationAnalytics(fromDate, toDate, doctorId, locationId, type));
		return response;
	}


	
}
