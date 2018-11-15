package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;
import com.dpdocter.services.WebAppointmentService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.WEB_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.WEB_APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class WebAppointmentApi {

	@Autowired
	private WebAppointmentService webAppointmentService;
	
	@Path(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL, notes = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	public Response<WebDoctorClinicsResponse> getClinicsByDoctorSlugURL(@QueryParam("doctorSlugUrl") String doctorSlugUrl) {
		
		WebDoctorClinicsResponse webDoctorClinicsResponse = webAppointmentService.getClinicsByDoctorSlugURL(doctorSlugUrl);
		Response<WebDoctorClinicsResponse> response = new Response<WebDoctorClinicsResponse>();
		response.setData(webDoctorClinicsResponse);
		return response;
	}
	
	@Path(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	@GET
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	public Response<SlotDataResponse> getTimeSlots(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("date") String date) {
		
		SlotDataResponse slots = webAppointmentService.getTimeSlots(doctorId, locationId, date);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slots);
		return response;
	}
}
