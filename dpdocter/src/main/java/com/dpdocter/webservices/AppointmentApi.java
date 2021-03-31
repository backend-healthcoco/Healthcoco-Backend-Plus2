package com.dpdocter.webservices;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CustomAppointment;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.request.PrintPatientCardRequest;
import com.dpdocter.response.AVGTimeDetail;
import com.dpdocter.response.LocationWithAppointmentCount;
import com.dpdocter.response.LocationWithPatientQueueDetails;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.APPOINTMENT_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
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

	@GetMapping(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY, notes = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	public Response<Boolean> activateCity(@PathVariable(value = "cityId") String cityId,
			  @RequestParam("activate") Boolean activate) throws MessagingException {
		if (cityId == null) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: city id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean isActivated = false;
		isActivated = appointmentService.activateDeactivateCity(cityId, activate);
		transnationalService.addResource(new ObjectId(cityId), Resource.CITY, false);
		esCityService.activateDeactivateCity(cityId, activate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(isActivated);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_COUNTRIES)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_COUNTRIES, notes = PathProxy.AppointmentUrls.GET_COUNTRIES)
	public Response<City> getCountries() {
		List<City> countries = appointmentService.getCountries();
		Response<City> response = new Response<City>();
		response.setDataList(countries);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_STATES)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_STATES, notes = PathProxy.AppointmentUrls.GET_STATES)
	public Response<City> getStates(@RequestParam(value = "country") String country) {
		List<City> states = appointmentService.getStates(country);
		Response<City> response = new Response<City>();
		response.setDataList(states);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY, notes = PathProxy.AppointmentUrls.GET_CITY)
	public Response<City> getCities(@RequestParam(value = "state") String state) {
		List<City> cities = appointmentService.getCities(state);
		Response<City> response = new Response<City>();
		response.setDataList(cities);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CITY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY_ID, notes = PathProxy.AppointmentUrls.GET_CITY_ID)
	public Response<City> getCityById(@PathVariable(value = "cityId") String cityId) throws MessagingException {
		if (cityId == null) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: city id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		City cities = appointmentService.getCity(cityId);
		Response<City> response = new Response<City>();
		response.setData(cities);
		return response;

	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	public Response<LandmarkLocality> addLandmaklLocality(@RequestBody LandmarkLocality request) throws MessagingException {
		if (request == null) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: Landmark locality request is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LandmarkLocality locality = appointmentService.addLandmaklLocality(request);
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LANDMARKLOCALITY, false);
		ESLandmarkLocalityDocument esLandmarkLocalityDocument = new ESLandmarkLocalityDocument();
		BeanUtil.map(locality, esLandmarkLocalityDocument);
		esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(locality.getLatitude(), locality.getLongitude()));
		esCityService.addLocalityLandmark(esLandmarkLocalityDocument);

		Response<LandmarkLocality> response = new Response<LandmarkLocality>();
		response.setData(locality);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CLINIC)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC, notes = PathProxy.AppointmentUrls.GET_CLINIC)
	public Response<Clinic> getClinic(@PathVariable(value = "locationId") String locationId,
			@RequestParam(value = "role") String role,
			@DefaultValue("false") @RequestParam(value = "active") Boolean active) throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			mailService.sendExceptionMail("Invalid input :: Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		Clinic clinic = appointmentService.getClinic(locationId, role, active);
		Response<Clinic> response = new Response<Clinic>();
		response.setData(clinic);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_LAB)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB, notes = PathProxy.AppointmentUrls.GET_LAB)
	public Response<Lab> getLabs(@PathVariable("locationId") String locationId, @RequestParam("patientId") String patientId,
			@DefaultValue("false") @RequestParam(value = "active") Boolean active) throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			mailService.sendExceptionMail("Invalid input :: Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		Lab lab = appointmentService.getLab(locationId, patientId, active);
		Response<Lab> response = new Response<Lab>();
		response.setData(lab);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL, notes = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL)
	public Response<Clinic> getClinic(@PathVariable(value = "slugUrl") String slugUrl) throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(slugUrl)) {
			logger.warn("slugUrl cannot be empty");
			mailService.sendExceptionMail("Invalid input :: slugUrl cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "slugUrl cannot be empty");
		}
		Clinic clinic = appointmentService.getClinic(slugUrl);
		clinic.setSlugUrl(slugUrl);
		Response<Clinic> response = new Response<Clinic>();
		response.setData(clinic);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL, notes = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL)
	public Response<Lab> getLabs(@PathVariable("slugUrl") String slugUrl) throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(slugUrl)) {
			logger.warn("slugUrl cannot be empty");
			mailService.sendExceptionMail("Invalid input :: slugUrl cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "slugUrl cannot be empty");
		}

		Lab lab = appointmentService.getLab(slugUrl);
		lab.setSlugUrl(slugUrl);
		Response<Lab> response = new Response<Lab>();
		response.setData(lab);
		return response;
	}

	@PostMapping
	@ApiOperation(value = "ADD_APPOINTMENT", notes = "ADD_APPOINTMENT")
	public Response<Appointment> BookAppoinment(@RequestBody AppointmentRequest request,
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
			@RequestParam(value = "fromTime") String fromTime, @RequestParam(value = "toTime") String toTime,
			@DefaultValue("false") @RequestParam("isRegisteredPatientRequired") Boolean isRegisteredPatientRequired,
			@DefaultValue(value = "false") @RequestParam(value = "isWeb") Boolean isWeb,
			@RequestParam(value = "type") String type) {

		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, status, sortBy, fromTime, toTime, isRegisteredPatientRequired, isWeb,type);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS, notes = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	public Response<Object> getPatientAppointments(@RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "patientId") String patientId,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size,
			@DefaultValue(value = "0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam(value = "type") String type) {

		Response<Object> response = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, type);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	public Response<SlotDataResponse> getTimeSlots(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("date") String date,
			@DefaultValue(value = "true") @RequestParam(value = "isPatient") Boolean isPatient)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			mailService.sendExceptionMail("Invalid input :: Doctor Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		Date dateObj = new Date(Long.parseLong(date));
		SlotDataResponse slotDataResponse = appointmentService.getTimeSlots(doctorId, locationId, dateObj, isPatient);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slotDataResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT, notes = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
	public Response<Event> addEditEvent(@RequestBody EventRequest request, 
			@DefaultValue(value = "false") @RequestParam(value = "ALL") Boolean forAllDoctors) throws MessagingException {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: Doctor Id ,Location Id  cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Event event = null;
		if (request.getId() == null)
			event = appointmentService.addEvent(request, forAllDoctors);
		else
			event = appointmentService.updateEvent(request, forAllDoctors);

		Response<Event> response = new Response<Event>();
		response.setData(event);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT, notes = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
	public Response<Boolean> sendReminderToPatient(@PathVariable(value = "appointmentId") String appointmentId)
			throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
			logger.warn("Appointment Id cannot be null");
			mailService.sendExceptionMail("Invalid input :: Appointment Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Appointment Id cannot be null");
		}
		Boolean sendReminder = appointmentService.sendReminderToPatient(appointmentId);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(sendReminder);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
	public Response<PatientQueue> addPatientInQueue(@RequestBody PatientQueueAddEditRequest request) throws MessagingException {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail(
					"Invalid input :: Doctor Id ,Location Id, Hostipal Id or Patient Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<PatientQueue> patientQueues = appointmentService.addPatientInQueue(request);

		Response<PatientQueue> response = new Response<PatientQueue>();
		response.setDataList(patientQueues);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
	@ApiOperation(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
	public Response<PatientQueue> rearrangePatientInQueue(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId, @PathVariable(value = "appointmentId") String appointmentId,
			@PathVariable(value = "sequenceNo") int sequenceNo) throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId, appointmentId)) {
			logger.warn("DoctorId, LocationId, HospitalId, PatientId cannot be null");
			mailService
					.sendExceptionMail("Invalid input :: DoctorId, LocationId, HospitalId, PatientId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"DoctorId, LocationId, HospitalId, PatientId cannot be null");
		}
		List<PatientQueue> patientQueues = appointmentService.rearrangePatientInQueue(doctorId, locationId, hospitalId,
				patientId, appointmentId, sequenceNo);

		Response<PatientQueue> response = new Response<PatientQueue>();
		response.setDataList(patientQueues);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE, notes = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
	public Response<PatientQueue> getPatientQueue(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "status") String status) throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			mailService
					.sendExceptionMail("Invalid input :: DoctorId, LocationId, HospitalId, PatientId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "DoctorId, LocationId, HospitalId cannot be null");
		}
		List<PatientQueue> patientQueues = appointmentService.getPatientQueue(doctorId, locationId, hospitalId, status);

		Response<PatientQueue> response = new Response<PatientQueue>();
		response.setDataList(patientQueues);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_APPOINTMENT_ID)
	@ApiOperation(value = "GET_APPOINTMENT_ID", notes = "GET_APPOINTMENT_ID")
	public Response<Appointment> getAppointmentById(@PathVariable(value = "appointmentId") String appointmentId)
			throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: AppointmentId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.getAppointmentById(new ObjectId(appointmentId));
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.PATIENT_COUNT)
	@ApiOperation(value = "PATIENT_COUNT", notes = "PATIENT_COUNT")
	public Response<LocationWithPatientQueueDetails> getNoOfPatientInQueue(
			@PathVariable(value = "locationId") String locationId, @MatrixParam(value = "doctorId") List<String> doctorId,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to) throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: Location Id cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LocationWithPatientQueueDetails locationWithPatientQueueDetails = appointmentService
				.getNoOfPatientInQueue(locationId, doctorId, from, to);
		Response<LocationWithPatientQueueDetails> response = new Response<LocationWithPatientQueueDetails>();
		response.setData(locationWithPatientQueueDetails);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_DOCTORS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_DOCTORS, notes = PathProxy.AppointmentUrls.GET_DOCTORS)
	public Response<LocationWithAppointmentCount> getDoctorsWithAppointmentCount(
			@PathVariable(value = "locationId") String locationId, @RequestParam(value = "role") String role,
			@DefaultValue("false") @RequestParam(value = "active") Boolean active,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to) throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			mailService.sendExceptionMail("Invalid input :: Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		LocationWithAppointmentCount locationWithAppointmentCount = appointmentService
				.getDoctorsWithAppointmentCount(locationId, role, active, from, to);
		Response<LocationWithAppointmentCount> response = new Response<LocationWithAppointmentCount>();
		response.setData(locationWithAppointmentCount);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT, notes = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT)
	public Response<Object> changeStatusInAppointment(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId, @PathVariable(value = "appointmentId") String appointmentId,
			@PathVariable(value = "status") String status,
			@RequestParam(value = "isObjectRequired") @DefaultValue("false") Boolean isObjectRequired)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId, appointmentId, status)) {
			logger.warn("DoctorId, Location Id, Hospital Id, Patient Id, AppointmentId, status cannot be empty");
			mailService.sendExceptionMail(
					"Invalid input :: DoctorId, Location Id, Hospital Id, Patient Id, AppointmentId, status cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"DoctorId, Location Id, Hospital Id, Patient Id, AppointmentId, status cannot be empty");
		}
		Object changeStatus = appointmentService.changeStatusInAppointment(doctorId, locationId, hospitalId, patientId,
				appointmentId, status, isObjectRequired);
		Response<Object> response = new Response<Object>();
		response.setData(changeStatus);
		return response;

	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT, notes = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT)
	public Response<CustomAppointment> addCustomAppointment(@RequestBody CustomAppointment request) throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getPatientName())) {
			logger.warn("DoctorId, Location Id, Hospital Id, Patient Name cannot be empty");
			mailService.sendExceptionMail(
					"Invalid input :: DoctorId, Location Id, Hospital Id, Patient Name cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"DoctorId, Location Id, Hospital Id, Patient Name cannot be empty");
		}
		CustomAppointment customAppointment = appointmentService.addCustomAppointment(request);
		Response<CustomAppointment> response = new Response<CustomAppointment>();
		response.setData(customAppointment);
		return response;

	}

	@DeleteMapping(PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT, notes = PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT)
	public Response<CustomAppointment> deleteCustomAppointment(@PathVariable("appointmentId") String appointmentId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("doctorId") String doctorId,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId, locationId, hospitalId, doctorId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}

		CustomAppointment customAppointment = appointmentService.deleteCustomAppointment(appointmentId, locationId,
				hospitalId, doctorId, discarded);

		Response<CustomAppointment> response = new Response<CustomAppointment>();
		response.setData(customAppointment);
		return response;
	}

	@GetMapping(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID)
	public Response<CustomAppointment> getCustomAppointmentById(@PathVariable("appointmentId") String appointmentId) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
			logger.warn("appointmentId cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "appointmentId cannot be empty");
		}

		CustomAppointment customAppointment = appointmentService.getCustomAppointmentById(appointmentId);
		Response<CustomAppointment> response = new Response<CustomAppointment>();
		response.setData(customAppointment);
		return response;
	}

	@GetMapping(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST)
	public Response<CustomAppointment> getCustomAppointments(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("doctorId") String doctorId,
			@DefaultValue(value = "0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, doctorId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		List<CustomAppointment> customAppointments = appointmentService.getCustomAppointments(page, size, locationId,
				hospitalId, doctorId, updatedTime, discarded);

		Response<CustomAppointment> response = new Response<CustomAppointment>();
		response.setDataList(customAppointments);
		return response;
	}

	@GetMapping(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL)
	public Response<AVGTimeDetail> getCustomAppointmentAVGTimeDetail(@RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, doctorId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		AVGTimeDetail avgTimeDetail = appointmentService.getCustomAppointmentAVGTimeDetail(locationId, hospitalId,
				doctorId);

		Response<AVGTimeDetail> response = new Response<AVGTimeDetail>();
		response.setData(avgTimeDetail);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT, notes = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	public Response<Appointment> getPatientLastAppointment(@PathVariable(value = "patientId") String patientId,
			@PathVariable(value = "locationId") String locationId, @RequestParam(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(patientId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.getPatientLastAppointment(locationId, doctorId, patientId);
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR, notes = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR)
	public Response<Appointment> updateAppointmentDoctor(@PathVariable(value = "appointmentId") String appointmentId,
			@PathVariable(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.updateAppointmentDoctor(appointmentId, doctorId);
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER)
	@ApiOperation(value = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER, notes = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER)
	public Response<String> downloadCalender(@MatrixParam(value = "doctorId") List<String> doctorIds,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to,
			@RequestParam("groupByDoctor")   Boolean isGroupByDoctor,
			@RequestParam("showMobileNo")   Boolean showMobileNo,
			@RequestParam("showAppointmentStatus")   Boolean showAppointmentStatus,
			@RequestParam("showNotes")   Boolean showNotes,
			@RequestParam("showPatientGroups")   Boolean showPatientGroups,
			@RequestParam("showCategory")   Boolean showCategory,
			@RequestParam("showTreatment")   Boolean showTreatment) {

		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<String> response = new Response<String>();
		response.setData(appointmentService.downloadCalender(doctorIds, locationId, hospitalId, from, to,
				isGroupByDoctor, showMobileNo, showAppointmentStatus, showNotes, showPatientGroups, showCategory,showTreatment));

		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD)
	@ApiOperation(value = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD, notes = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD)
	public Response<String> downloadReport(PrintPatientCardRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(appointmentService.printPatientCard(request));
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_EVENTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_EVENTS, notes = PathProxy.AppointmentUrls.GET_EVENTS)
	public Response<Event> getEvents(@RequestParam(value = "locationId") String locationId,
			@MatrixParam(value = "doctorId") List<String> doctorId, @RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@DefaultValue(value = "0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "sortBy") String sortBy, @RequestParam(value = "fromTime") String fromTime,
			@RequestParam(value = "toTime") String toTime,
			@DefaultValue(value = "false") @RequestParam(value = "byMonth") Boolean byMonth,
			@DefaultValue(value = "false") @RequestParam(value = "isCalenderBlocked") Boolean isCalenderBlocked,
			@RequestParam(value = "state") String state) {

		Response<Event> response = new Response<Event>();

		if (byMonth)
			response = appointmentService.getEventsByMonth(locationId, doctorId, from, to, page, size, updatedTime,
					sortBy, fromTime, toTime, isCalenderBlocked, state);
		else
			response = appointmentService.getEvents(locationId, doctorId, from, to, page, size, updatedTime, sortBy,
					fromTime, toTime, isCalenderBlocked, state);
		
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_EVENT_BY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_EVENT_BY_ID, notes = PathProxy.AppointmentUrls.GET_EVENT_BY_ID)
	public Response<Event> getEventById(@PathVariable(value = "eventId") String eventId) {
		if (DPDoctorUtils.anyStringEmpty(eventId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Event event = appointmentService.getEventById(eventId);
		Response<Event> response = new Response<Event>();
		response.setData(event);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT, notes = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT)
	public Response<Boolean> addNutritionAppointment(@RequestBody NutritionAppointment request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		if (DPDoctorUtils.anyStringEmpty(request.getUserId(), request.getMobileNumber())) {
			throw new BusinessException(ServiceError.InvalidInput, "userId and mobile number should not null or empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentService.addEditNutritionAppointment(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.AppointmentUrls.UPDATE_BOOKED_SLOT)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_BOOKED_SLOT, notes = PathProxy.AppointmentUrls.UPDATE_BOOKED_SLOT)
	public Response<Boolean> update() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentService.update());
		return response;
	}
	
	@GetMapping(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	public Response<SlotDataResponse> getOnlineConsultationTimeSlots(@PathVariable("doctorId") String doctorId,
			@RequestParam(value="consultationType") String consultationtype, @PathVariable("date") String date,
			@DefaultValue(value = "true") @RequestParam(value = "isPatient") Boolean isPatient)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			mailService.sendExceptionMail("Invalid input :: Doctor Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		Date dateObj = new Date(Long.parseLong(date));
		SlotDataResponse slotDataResponse = appointmentService.getOnlineConsultationTimeSlots(doctorId, consultationtype, dateObj, isPatient);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slotDataResponse);
		return response;
	}


}
