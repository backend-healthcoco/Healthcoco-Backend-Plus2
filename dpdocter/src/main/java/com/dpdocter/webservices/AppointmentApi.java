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

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentApi {

	private static Logger logger = Logger.getLogger(AppointmentApi.class.getName());

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	MailService mailService;

	@Path(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY, notes = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	public Response<Boolean> activateCity(@PathParam(value = "cityId") String cityId,
			@DefaultValue("true") @QueryParam("activate") Boolean activate) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_COUNTRIES)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_COUNTRIES, notes = PathProxy.AppointmentUrls.GET_COUNTRIES)
	public Response<City> getCountries() {
		List<City> countries = appointmentService.getCountries();
		Response<City> response = new Response<City>();
		response.setDataList(countries);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_STATES)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_STATES, notes = PathProxy.AppointmentUrls.GET_STATES)
	public Response<City> getStates(@QueryParam(value = "country") String country) {
		List<City> states = appointmentService.getStates(country);
		Response<City> response = new Response<City>();
		response.setDataList(states);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_CITY)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY, notes = PathProxy.AppointmentUrls.GET_CITY)
	public Response<City> getCities(@QueryParam(value = "state") String state) {
		List<City> cities = appointmentService.getCities(state);
		Response<City> response = new Response<City>();
		response.setDataList(cities);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_CITY_ID)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY_ID, notes = PathProxy.AppointmentUrls.GET_CITY_ID)
	public Response<City> getCityById(@PathParam(value = "cityId") String cityId) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	public Response<LandmarkLocality> addLandmaklLocality(LandmarkLocality request) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_CLINIC)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC, notes = PathProxy.AppointmentUrls.GET_CLINIC)
	public Response<Clinic> getClinic(@PathParam(value = "locationId") String locationId,
			@QueryParam(value = "role") String role,
			@DefaultValue("false") @QueryParam(value = "active") Boolean active) throws MessagingException {

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

	@Path(value = PathProxy.AppointmentUrls.GET_LAB)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB, notes = PathProxy.AppointmentUrls.GET_LAB)
	public Response<Lab> getLabs(@PathParam("locationId") String locationId, @QueryParam("patientId") String patientId,
			@DefaultValue("false") @QueryParam(value = "active") Boolean active) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL, notes = PathProxy.AppointmentUrls.GET_CLINIC_BY_SLUG_URL)
	public Response<Clinic> getClinic(@PathParam(value = "slugUrl") String slugUrl) throws MessagingException {

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

	@Path(value = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL, notes = PathProxy.AppointmentUrls.GET_LAB_BY_SLUG_URL)
	public Response<Lab> getLabs(@PathParam("slugUrl") String slugUrl) throws MessagingException {
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
			@QueryParam(value = "type") String type,@DefaultValue(value = "false") @QueryParam(value = "isAnonymousAppointment") Boolean isAnonymousAppointment) {

		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, status, sortBy, fromTime, toTime, isRegisteredPatientRequired, isWeb,type,isAnonymousAppointment);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS, notes = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
	public Response<Object> getPatientAppointments(@QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "patientId") String patientId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue(value = "0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam(value = "type") String type) {

		Response<Object> response = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, type);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	public Response<SlotDataResponse> getTimeSlots(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("date") String date,
			@DefaultValue(value = "true") @QueryParam(value = "isPatient") Boolean isPatient)
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

	@Path(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT, notes = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
	public Response<Event> addEditEvent(EventRequest request, 
			@DefaultValue(value = "false") @QueryParam(value = "ALL") Boolean forAllDoctors) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT, notes = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
	public Response<Boolean> sendReminderToPatient(@PathParam(value = "appointmentId") String appointmentId)
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

	@Path(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
	public Response<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
	public Response<PatientQueue> rearrangePatientInQueue(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "appointmentId") String appointmentId,
			@PathParam(value = "sequenceNo") int sequenceNo) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE, notes = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
	public Response<PatientQueue> getPatientQueue(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "status") String status) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_APPOINTMENT_ID)
	@GET
	@ApiOperation(value = "GET_APPOINTMENT_ID", notes = "GET_APPOINTMENT_ID")
	public Response<Appointment> getAppointmentById(@PathParam(value = "appointmentId") String appointmentId)
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

	@Path(value = PathProxy.AppointmentUrls.PATIENT_COUNT)
	@GET
	@ApiOperation(value = "PATIENT_COUNT", notes = "PATIENT_COUNT")
	public Response<LocationWithPatientQueueDetails> getNoOfPatientInQueue(
			@PathParam(value = "locationId") String locationId, @MatrixParam(value = "doctorId") List<String> doctorId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to) throws MessagingException {
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

	@Path(value = PathProxy.AppointmentUrls.GET_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_DOCTORS, notes = PathProxy.AppointmentUrls.GET_DOCTORS)
	public Response<LocationWithAppointmentCount> getDoctorsWithAppointmentCount(
			@PathParam(value = "locationId") String locationId, @QueryParam(value = "role") String role,
			@DefaultValue("false") @QueryParam(value = "active") Boolean active,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to) throws MessagingException {

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

	@Path(value = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT, notes = PathProxy.AppointmentUrls.CHANGE_STATUS_IN_APPOINTMENT)
	public Response<Object> changeStatusInAppointment(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "appointmentId") String appointmentId,
			@PathParam(value = "status") String status,
			@QueryParam(value = "isObjectRequired") @DefaultValue("false") Boolean isObjectRequired)
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

	@Path(value = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT, notes = PathProxy.AppointmentUrls.ADD_CUSTOM_APPOINTMENT)
	public Response<CustomAppointment> addCustomAppointment(CustomAppointment request) throws MessagingException {

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

	@Path(PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT)
	@DELETE
	@ApiOperation(value = PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT, notes = PathProxy.AppointmentUrls.DELETE_CUSTOM_APPOINTMENT)
	public Response<CustomAppointment> deleteCustomAppointment(@PathParam("appointmentId") String appointmentId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("doctorId") String doctorId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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

	@Path(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_BY_ID)
	public Response<CustomAppointment> getCustomAppointmentById(@PathParam("appointmentId") String appointmentId) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
			logger.warn("appointmentId cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "appointmentId cannot be empty");
		}

		CustomAppointment customAppointment = appointmentService.getCustomAppointmentById(appointmentId);
		Response<CustomAppointment> response = new Response<CustomAppointment>();
		response.setData(customAppointment);
		return response;
	}

	@Path(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_LIST)
	public Response<CustomAppointment> getCustomAppointments(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId,
			@DefaultValue(value = "0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
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

	@Path(PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL, notes = PathProxy.AppointmentUrls.GET_CUSTOM_APPOINTMENT_AVG_DETAIL)
	public Response<AVGTimeDetail> getCustomAppointmentAVGTimeDetail(@QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId) {
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

	@Path(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT, notes = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	public Response<Appointment> getPatientLastAppointment(@PathParam(value = "patientId") String patientId,
			@PathParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(patientId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.getPatientLastAppointment(locationId, doctorId, patientId);
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR, notes = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT_DOCTOR)
	public Response<Appointment> updateAppointmentDoctor(@PathParam(value = "appointmentId") String appointmentId,
			@PathParam(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(appointmentId, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.updateAppointmentDoctor(appointmentId, doctorId);
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER, notes = PathProxy.AppointmentUrls.DOWNLOAD_APPOINTMENT_CALENDER)
	public Response<String> downloadCalender(@MatrixParam(value = "doctorId") List<String> doctorIds,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
			@QueryParam("groupByDoctor") @DefaultValue("true") Boolean isGroupByDoctor,
			@QueryParam("showMobileNo") @DefaultValue("true") Boolean showMobileNo,
			@QueryParam("showAppointmentStatus") @DefaultValue("true") Boolean showAppointmentStatus,
			@QueryParam("showNotes") @DefaultValue("true") Boolean showNotes,
			@QueryParam("showPatientGroups") @DefaultValue("true") Boolean showPatientGroups,
			@QueryParam("showCategory") @DefaultValue("true") Boolean showCategory,
			@QueryParam("showTreatment") @DefaultValue("true") Boolean showTreatment) {

		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<String> response = new Response<String>();
		response.setData(appointmentService.downloadCalender(doctorIds, locationId, hospitalId, from, to,
				isGroupByDoctor, showMobileNo, showAppointmentStatus, showNotes, showPatientGroups, showCategory,showTreatment));

		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD, notes = PathProxy.AppointmentUrls.DOWNLOAD_PATIENT_CARD)
	public Response<String> downloadReport(PrintPatientCardRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(appointmentService.printPatientCard(request));
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_EVENTS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_EVENTS, notes = PathProxy.AppointmentUrls.GET_EVENTS)
	public Response<Event> getEvents(@QueryParam(value = "locationId") String locationId,
			@MatrixParam(value = "doctorId") List<String> doctorId, @QueryParam(value = "from") String from,
			@QueryParam(value = "to") String to, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@DefaultValue(value = "0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "sortBy") String sortBy, @QueryParam(value = "fromTime") String fromTime,
			@QueryParam(value = "toTime") String toTime,
			@DefaultValue(value = "false") @QueryParam(value = "byMonth") Boolean byMonth,
			@DefaultValue(value = "false") @QueryParam(value = "isCalenderBlocked") Boolean isCalenderBlocked,
			@QueryParam(value = "state") String state) {

		Response<Event> response = new Response<Event>();

		if (byMonth)
			response = appointmentService.getEventsByMonth(locationId, doctorId, from, to, page, size, updatedTime,
					sortBy, fromTime, toTime, isCalenderBlocked, state);
		else
			response = appointmentService.getEvents(locationId, doctorId, from, to, page, size, updatedTime, sortBy,
					fromTime, toTime, isCalenderBlocked, state);
		
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.GET_EVENT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_EVENT_BY_ID, notes = PathProxy.AppointmentUrls.GET_EVENT_BY_ID)
	public Response<Event> getEventById(@PathParam(value = "eventId") String eventId) {
		if (DPDoctorUtils.anyStringEmpty(eventId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Event event = appointmentService.getEventById(eventId);
		Response<Event> response = new Response<Event>();
		response.setData(event);
		return response;
	}

	@Path(value = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT)
	@POST
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT, notes = PathProxy.AppointmentUrls.ADD_NUTRITION_APPOINTMENT)
	public Response<Boolean> addNutritionAppointment(NutritionAppointment request) {
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
	
	@Path(value = PathProxy.AppointmentUrls.UPDATE_BOOKED_SLOT)
	@GET
	public Response<Boolean> update() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentService.update());
		return response;
	}
	
	@Path(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	@GET
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	public Response<SlotDataResponse> getOnlineConsultationTimeSlots(@PathParam("doctorId") String doctorId,
			@QueryParam(value="consultationType") String consultationtype, @PathParam("date") String date,
			@DefaultValue(value = "true") @QueryParam(value = "isPatient") Boolean isPatient)
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
