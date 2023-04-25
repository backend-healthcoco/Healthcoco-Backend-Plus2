package com.dpdocter.webservices.v2;

import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.v2.Appointment;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.services.MailService;
import com.dpdocter.services.v2.AppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "AppointmentApiV2")
@Path(PathProxy.APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentApi {

	private static Logger logger = Logger.getLogger(AppointmentApi.class.getName());

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	MailService mailService;

	@POST
	@ApiOperation(value = "ADD_APPOINTMENT", notes = "ADD_APPOINTMENT")
	public Response<Appointment> BookAppoinment(AppointmentRequest request,
			@DefaultValue(value = "false") @QueryParam(value = "isStatusChange") Boolean isStatusChange)
			throws MessagingException {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: Doctor Id ,Location Id or Hostipal Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (request.getTime() != null && (request.getTime().getFromTime() > request.getTime().getToTime())) {
			logger.warn("Invalid Time");
			mailService.sendExceptionMail("Invalid input :: Time");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Time");
		} else if (request.getTime() != null
				&& ((request.getTime().getToTime() - request.getTime().getFromTime()) > 120)) {
			logger.warn("Invalid Time");
			mailService.sendExceptionMail("Invalid input : Appointment duration cannot be greater than 120 mins");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Time : Appointment duration cannot be greater than 120 mins");
		}

		Appointment appointment = null;
		if (request.getAppointmentId() == null) {
			appointment = appointmentService.addAppointment(request, true);
		} else {
			appointment = appointmentService.updateAppointment(request, true, isStatusChange);
		}

		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;

	}

	@GET
	@ApiOperation(value = "GET_APPOINTMENTS", notes = "GET_APPOINTMENTS")
	public Response<Appointment> getDoctorAppointments(@QueryParam(value = "locationId") String locationId,
			@MatrixParam(value = "doctorId") List<String> doctorId, @QueryParam(value = "patientId") String patientId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue(value = "0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "status") String status, @QueryParam(value = "sortBy") String sortBy,
			@QueryParam(value = "fromTime") String fromTime, @QueryParam(value = "toTime") String toTime,
			@DefaultValue("false") @QueryParam("isRegisteredPatientRequired") Boolean isRegisteredPatientRequired,
			@DefaultValue(value = "false") @QueryParam(value = "isWeb") Boolean isWeb,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded, @QueryParam("branch") String branch) {
		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, status, sortBy, fromTime, toTime, isRegisteredPatientRequired, isWeb,
				discarded, branch);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS, notes = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	public Response<Object> getPatientAppointments(@QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "patientId") String patientId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue(value = "0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "type") String type) {

		Response<Object> response = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, type);
		return response;
	}

}
