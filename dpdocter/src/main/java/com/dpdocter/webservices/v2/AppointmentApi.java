package com.dpdocter.webservices.v2;

import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.v2.Appointment;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.services.MailService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.v2.AppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "AppointmentApiV2")
@RequestMapping(value=PathProxy.APPOINTMENT_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentApi {

	private static Logger logger = LogManager.getLogger(AppointmentApi.class.getName());

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	MailService mailService;

	@PostMapping
	@ApiOperation(value = "ADD_APPOINTMENT", notes = "ADD_APPOINTMENT")
	public Response<Appointment> BookAppoinment(AppointmentRequest request,
			@DefaultValue(value = "false") @RequestParam(value = "isStatusChange") Boolean isStatusChange)
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

	@GetMapping
	@ApiOperation(value = "GET_APPOINTMENTS", notes = "GET_APPOINTMENTS")
	public Response<Appointment> getDoctorAppointments(@RequestParam(value = "locationId") String locationId,
			@MatrixParam(value = "doctorId") List<String> doctorId, @RequestParam(value = "patientId") String patientId,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size,
			@DefaultValue(value = "0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "status") String status, @RequestParam(value = "sortBy") String sortBy,
			@RequestParam(value = "fromTime") String fromTime, @RequestParam(value = "toTime") String toTime ,@DefaultValue("false") @RequestParam("isRegisteredPatientRequired") Boolean isRegisteredPatientRequired,
			@DefaultValue(value = "false") @RequestParam(value = "isWeb") Boolean isWeb,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,@RequestParam("branch") String branch) {

		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, status, sortBy, fromTime, toTime, isRegisteredPatientRequired, isWeb, discarded,branch);
		return response;
	}

	
	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS, notes = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	public Response<Object> getPatientAppointments(@RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "patientId") String patientId,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size,
			@DefaultValue(value = "0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam(value = "type") String type) {

		Response<Object> response = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from,
				to, page, size, updatedTime, type);
		return response;
	}

	
}
