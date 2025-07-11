package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.PATIENT_TREATMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_TREATMENT_BASE_URL, description = "Endpoint for patient treatment")
public class PatientTreamentAPI {

	private static Logger logger = Logger.getLogger(PatientTreamentAPI.class.getName());

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Autowired
	private PatientVisitService patientTrackService;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private OTPService otpService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESTreatmentService esTreatmentService;

	@Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE)
	@POST
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE)
	public Response<TreatmentService> addEditService(TreatmentService request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		TreatmentService treatmentService = patientTreatmentServices.addEditService(request);
		transactionalManagementService.addResource(new ObjectId(treatmentService.getId()), Resource.TREATMENTSERVICE,
				false);
		ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
		BeanUtil.map(treatmentService, esTreatmentServiceDocument);
		esTreatmentService.addEditService(esTreatmentServiceDocument);

		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(treatmentService);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE_COST)
	@POST
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE_COST, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE_COST)
	public Response<TreatmentServiceCost> addEditServiceCost(TreatmentServiceCost request) {
		if (request == null || request.getTreatmentService() == null
				|| (DPDoctorUtils.anyStringEmpty(request.getTreatmentService().getId())
						&& DPDoctorUtils.anyStringEmpty(request.getTreatmentService().getName()))
				|| DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId(),
						request.getDoctorId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		TreatmentServiceCost treatmentServiceCost = patientTreatmentServices.addEditServiceCost(request);
		transactionalManagementService.addResource(new ObjectId(treatmentServiceCost.getId()),
				Resource.TREATMENTSERVICECOST, false);
		ESTreatmentServiceCostDocument esTreatmentServiceDocument = new ESTreatmentServiceCostDocument();
		BeanUtil.map(treatmentServiceCost, esTreatmentServiceDocument);
		if (treatmentServiceCost.getTreatmentService() != null)
			esTreatmentServiceDocument.setTreatmentServiceId(treatmentServiceCost.getTreatmentService().getId());
		esTreatmentService.addEditServiceCost(esTreatmentServiceDocument);

		Response<TreatmentServiceCost> response = new Response<TreatmentServiceCost>();
		response.setData(treatmentServiceCost);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.DELETE_SERVICE)
	@DELETE
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_SERVICE, notes = PathProxy.PatientTreatmentURLs.DELETE_SERVICE)
	public Response<TreatmentService> deleteService(@PathParam(value = "treatmentServiceId") String treatmentServiceId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(treatmentServiceId, doctorId, hospitalId, locationId)) {
			logger.warn("Treatment Service Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Treatment Service Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		TreatmentService treatmentService = patientTreatmentServices.deleteService(treatmentServiceId, doctorId,
				locationId, hospitalId, discarded);
		if (treatmentService != null) {
			transactionalManagementService.addResource(new ObjectId(treatmentService.getId()),
					Resource.TREATMENTSERVICE, false);
			ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
			BeanUtil.map(treatmentService, esTreatmentServiceDocument);
			esTreatmentService.addEditService(esTreatmentServiceDocument);
		}
		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(treatmentService);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.DELETE_SERVICE_COST)
	@DELETE
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_SERVICE_COST, notes = PathProxy.PatientTreatmentURLs.DELETE_SERVICE_COST)
	public Response<TreatmentServiceCost> deleteServiceCost(
			@PathParam(value = "treatmentServiceId") String treatmentServiceId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(treatmentServiceId, doctorId, hospitalId, locationId)) {
			logger.warn("Treatment Service Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Treatment Service Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		TreatmentServiceCost treatmentServiceCost = patientTreatmentServices.deleteServiceCost(treatmentServiceId,
				doctorId, locationId, hospitalId, discarded);
		if (treatmentServiceCost != null) {
			transactionalManagementService.addResource(new ObjectId(treatmentServiceCost.getId()),
					Resource.TREATMENTSERVICECOST, false);
			ESTreatmentServiceCostDocument esTreatmentServiceDocument = new ESTreatmentServiceCostDocument();
			BeanUtil.map(treatmentServiceCost, esTreatmentServiceDocument);
			esTreatmentService.addEditServiceCost(esTreatmentServiceDocument);
		}
		Response<TreatmentServiceCost> response = new Response<TreatmentServiceCost>();
		response.setData(treatmentServiceCost);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.GET_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_SERVICES, notes = PathProxy.PatientTreatmentURLs.GET_SERVICES)
	public Response<Object> getServices(@PathParam("type") String type, @PathParam("range") String range,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(type, range, doctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		List<?> objects = patientTreatmentServices.getServices(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded);

		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT)
	@POST
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT)
	public Response<PatientTreatmentResponse> addEditPatientTreatment(PatientTreatmentAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		if (request.getTreatments() == null || request.getTreatments().isEmpty()) {
			logger.warn("Patient Treament request cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Patient Treament request cannot be empty");
		}
		PatientTreatmentResponse addEditPatientTreatmentResponse = patientTreatmentServices
				.addEditPatientTreatment(request, true, null, null);
		if (addEditPatientTreatmentResponse != null) {
			String visitId = patientTrackService.addRecord(addEditPatientTreatmentResponse, VisitedFor.TREATMENT,
					request.getVisitId());
			addEditPatientTreatmentResponse.setVisitId(visitId);
		}

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(addEditPatientTreatmentResponse);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.CHANGE_SERVICE_STATUS)
	@POST
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.CHANGE_SERVICE_STATUS, notes = PathProxy.PatientTreatmentURLs.CHANGE_SERVICE_STATUS)
	public Response<PatientTreatmentResponse> changePatientTreatmentStatus(@PathParam("treatmentId") String treatmentId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("doctorId") String doctorId, Treatment treatment) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId, locationId, hospitalId, doctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		if (treatment == null) {
			logger.warn("Patient Treament request cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Patient Treament request cannot be empty");
		}
		PatientTreatmentResponse addEditPatientTreatmentResponse = patientTreatmentServices
				.changePatientTreatmentStatus(treatmentId, doctorId, locationId, hospitalId, treatment);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(addEditPatientTreatmentResponse);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT)
	@DELETE
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT)
	public Response<PatientTreatmentResponse> deletePatientTreatment(@PathParam("treatmentId") String treatmentId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("doctorId") String doctorId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId, locationId, hospitalId, doctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		PatientTreatmentResponse deletePatientTreatmentResponse = patientTreatmentServices
				.deletePatientTreatment(treatmentId, doctorId, locationId, hospitalId, discarded);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(deletePatientTreatmentResponse);
		return response;
	}
	
	@Path(PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT_WEB)
	@DELETE
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT_WEB, notes = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT_WEB)
	public Response<PatientTreatmentResponse> deletePatientTreatmentForWeb(@PathParam("treatmentId") String treatmentId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId, locationId, hospitalId, doctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		PatientTreatmentResponse deletePatientTreatmentResponse = patientTreatmentServices
				.deletePatientTreatmentForWeb(treatmentId, discarded);
		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(deletePatientTreatmentResponse);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID, notes = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID)
	public Response<PatientTreatmentResponse> getPatientTreatmentById(@PathParam("treatmentId") String treatmentId) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId)) {
			logger.warn("TreatmentId cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "TreatmentId cannot be empty");
		}

		PatientTreatmentResponse getPatientTreatmentByIdResponse = patientTreatmentServices
				.getPatientTreatmentById(treatmentId);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(getPatientTreatmentByIdResponse);
		return response;
	}

	@Path(PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_PATIENT_ID)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_PATIENT_ID, notes = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_PATIENT_ID)
	public Response<PatientTreatmentResponse> getPatientTreatmentByPatientId(@PathParam("patientId") String patientId,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "status") String status) {

		List<PatientTreatmentResponse> patientTreatmentResponses = patientTreatmentServices
				.getPatientTreatmentByPatientId(page, size, doctorId, locationId, hospitalId, patientId, updatedTime,
						discarded, false, status);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setDataList(patientTreatmentResponses);
		return response;
	}

	@GET
	@ApiOperation(value = "GET_PATIENT_TREATMENTS", notes = "GET_PATIENT_TREATMENTS")
	public Response<PatientTreatmentResponse> getPatientTreatments(@QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@QueryParam("patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "status") String status) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		List<PatientTreatmentResponse> patientTreatmentResponses = patientTreatmentServices.getPatientTreatments(page,
				size, doctorId, locationId, hospitalId, patientId, updatedTime,
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded, false, status);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setDataList(patientTreatmentResponses);
		return response;
	}

	@Path(value = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT)
	public Response<Boolean> emailPatientTreatment(@PathParam(value = "treatmentId") String treatmentId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(treatmentId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. Patient Treatment Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Patient Treatment Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		patientTreatmentServices.emailPatientTreatment(treatmentId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT_WEB)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT_WEB, notes = PathProxy.PatientTreatmentURLs.EMAIL_PATIENT_TREATMENT_WEB)
	public Response<Boolean> emailPatientTreatmentForWeb(@PathParam(value = "treatmentId") String treatmentId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(treatmentId, emailAddress)) {
			logger.warn(
					"Invalid Input. Patient Treatment Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Patient Treatment Id, EmailAddress Cannot Be Empty");
		}
		patientTreatmentServices.emailPatientTreatment(treatmentId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.PatientTreatmentURLs.DOWNLOAD_PATIENT_TREATMENT)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DOWNLOAD_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.DOWNLOAD_PATIENT_TREATMENT)
	public Response<String> downloadPatientTreatment(@PathParam("treatmentId") String treatmentId,
			@DefaultValue("false") @QueryParam("showPH") Boolean showPH,
			@DefaultValue("false") @QueryParam("showPLH") Boolean showPLH,
			@DefaultValue("false") @QueryParam("showFH") Boolean showFH,
			@DefaultValue("false") @QueryParam("showDA") Boolean showDA) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(
				patientTreatmentServices.downloadPatientTreatment(treatmentId, showPH, showPLH, showFH, showDA));
		return response;
	}

	@Path(value = PathProxy.PatientTreatmentURLs.GENERATE_TREATMENT_CODE)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GENERATE_TREATMENT_CODE, notes = PathProxy.PatientTreatmentURLs.GENERATE_TREATMENT_CODE)
	public Response<Integer> genrateTreatmentCode() {

		Response<Integer> response = new Response<Integer>();
		response.setData(patientTreatmentServices.genrateTreatmentCode());
		return response;
	}

	@Path(value = PathProxy.PatientTreatmentURLs.ADD_FAVOURITES_TO_TREATMENT_SERVICES)
	@POST
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_FAVOURITES_TO_TREATMENT_SERVICES, notes = PathProxy.PatientTreatmentURLs.ADD_FAVOURITES_TO_TREATMENT_SERVICES)
	public Response<TreatmentService> addFevourateToTreatmentService(TreatmentService request) {

		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(patientTreatmentServices.addFavouritesToService(request, null));
		return response;
	}

	@Path(value = PathProxy.PatientTreatmentURLs.ADD_TREATMENT_SERVICES_TO_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_TREATMENT_SERVICES_TO_DOCTOR, notes = PathProxy.PatientTreatmentURLs.ADD_TREATMENT_SERVICES_TO_DOCTOR)
	public Response<TreatmentService> makeDrugFavourite(@PathParam("serviceId") String serviceId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(serviceId, doctorId, locationId, hospitalId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		TreatmentService treatmentServiceResponse = patientTreatmentServices.makeServiceFavourite(serviceId, doctorId,
				locationId, hospitalId);
		transactionalManagementService.addResource(new ObjectId(treatmentServiceResponse.getId()),
				Resource.TREATMENTSERVICE, false);
		if (treatmentServiceResponse != null) {
			ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
			BeanUtil.map(treatmentServiceResponse, esTreatmentServiceDocument);

			esTreatmentService.addEditService(esTreatmentServiceDocument);
		}
		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(treatmentServiceResponse);
		return response;
	}
	
	@Path(value = PathProxy.PatientTreatmentURLs.GET_TREATMENT_SERVICES_BY_SPECIALITY)
	@GET
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_TREATMENT_SERVICES_BY_SPECIALITY, notes = PathProxy.PatientTreatmentURLs.GET_TREATMENT_SERVICES_BY_SPECIALITY)
	public Response<TreatmentService> getServicesBySpeciality(@QueryParam("speciality") String speciality) {
		if (DPDoctorUtils.anyStringEmpty(speciality)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<TreatmentService> treatmentServiceResponse = patientTreatmentServices.getListBySpeciality(speciality);
		
		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setDataList(treatmentServiceResponse);;
		return response;
	}
}
