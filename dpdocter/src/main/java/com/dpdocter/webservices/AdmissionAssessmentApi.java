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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.NursingCareExam;
import com.dpdocter.elasticsearch.document.ESNursingCareExaminationDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.InitialAdmissionRequest;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.services.InitialAssessmentService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This api used to get data by nurses at the time of admission
 * 
 * @author Nikita
 *
 */
@Component
@Path(PathProxy.ADMISSION_ASSESSMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMISSION_ASSESSMENT_BASE_URL, description = "Endpoint for form")
public class AdmissionAssessmentApi {

	private static Logger logger = Logger.getLogger(AdmissionAssessmentApi.class.getName());

	@Autowired
	private InitialAssessmentService initialAssessmentService;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Path(value = PathProxy.AdmissionAssessmentsUrls.ADD_EDIT_ADMISSION_FORM)
	@POST
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.ADD_EDIT_ADMISSION_FORM, notes = PathProxy.AdmissionAssessmentsUrls.ADD_EDIT_ADMISSION_FORM)
	public Response<InitialAdmissionResponse> addEditAdmissionAssessmentForm(InitialAdmissionRequest request) {
		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		InitialAdmissionResponse initialAdmissionResponse = initialAssessmentService
				.addEditAdmissionAssessmentForm(request);
		Response<InitialAdmissionResponse> response = new Response<InitialAdmissionResponse>();
		response.setData(initialAdmissionResponse);
		return response;
	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM)
	@GET
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM, notes = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM)
	public Response<InitialAdmissionResponse> getAdmissionAssessmentForms(
			@PathParam(value = "patientId") String patientId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "doctorId") String doctorId,
			@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId, doctorId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<InitialAdmissionResponse> initialAssessmentResponse = initialAssessmentService
				.getAdmissionAssessmentForms(doctorId, locationId, hospitalId, patientId, page, size, discarded);
		Response<InitialAdmissionResponse> response = new Response<InitialAdmissionResponse>();
		response.setDataList(initialAssessmentResponse);
		return response;

	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.ADD_NURSING_CARE)
	@POST
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.ADD_NURSING_CARE, notes = PathProxy.AdmissionAssessmentsUrls.ADD_NURSING_CARE)
	public Response<NursingCareExam> addEditNursingCareData(NursingCareExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNursingCare())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		NursingCareExam nursingCareExam = initialAssessmentService.addEditNursingCareExam(request);

		transactionalManagementService.addResource(new ObjectId(nursingCareExam.getId()), Resource.NURSING_CAREEXAM,
				false);
		ESNursingCareExaminationDocument nursingCareExaminationDocument = new ESNursingCareExaminationDocument();
		BeanUtil.map(nursingCareExam, nursingCareExaminationDocument);
		esClinicalNotesService.addNursingCareExam(nursingCareExaminationDocument);

		Response<NursingCareExam> response = new Response<NursingCareExam>();
		response.setData(nursingCareExam);
		return response;
	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.DELETE_NURSING_CARE)
	@DELETE
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.DELETE_NURSING_CARE, notes = PathProxy.AdmissionAssessmentsUrls.DELETE_NURSING_CARE)
	public Response<Boolean> deleteNoseExam(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean nursingCareExam = initialAssessmentService.deleteNursingCareExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (nursingCareExam != null) {
			transactionalManagementService.addResource(new ObjectId(id), Resource.NURSING_CAREEXAM, false);
			ESNursingCareExaminationDocument eSNursingCareExaminationDocument = new ESNursingCareExaminationDocument();
			BeanUtil.map(nursingCareExam, eSNursingCareExaminationDocument);
			esClinicalNotesService.addNursingCareExam(eSNursingCareExaminationDocument);
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(nursingCareExam);
		return response;
	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM_BY_ID, notes = PathProxy.AdmissionAssessmentsUrls.GET_ADMISSION_FORM_BY_ID)
	public Response<InitialAdmissionResponse> getById(@PathParam("nurseAdmissionFormId") String nurseAdmissionFormId) {
		if (nurseAdmissionFormId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<InitialAdmissionResponse> response = new Response<InitialAdmissionResponse>();
		response.setData(initialAssessmentService.getAdmissionFormById(nurseAdmissionFormId));
		return response;

	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.DELETE_ADMISSION_FORM)
	@DELETE
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.DELETE_ADMISSION_FORM, notes = PathProxy.AdmissionAssessmentsUrls.DELETE_ADMISSION_FORM)
	public Response<Boolean> deleteAdmitCard(@PathParam(value = "nurseAdmissionFormId") String nurseAdmissionFormId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(nurseAdmissionFormId) || StringUtils.isEmpty(doctorId)
				|| StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			logger.warn("nurseAdmissionFormId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"nurseAdmissionFormId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean formResponse = initialAssessmentService.deleteAdmissionAssessment(nurseAdmissionFormId, doctorId,
				hospitalId, locationId, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(formResponse);
		return response;
	}

	@Path(value = PathProxy.AdmissionAssessmentsUrls.DOWNLOAD_ADMISSION_FORM_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.AdmissionAssessmentsUrls.DOWNLOAD_ADMISSION_FORM_BY_ID, notes = PathProxy.AdmissionAssessmentsUrls.DOWNLOAD_ADMISSION_FORM_BY_ID)
	public Response<String> downloadNurseAdmissionFormById(
			@PathParam("nurseAdmissionFormId") String nurseAdmissionFormId) {
		if (nurseAdmissionFormId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(initialAssessmentService.downloadNurseAdmissionFormById(nurseAdmissionFormId));
		return response;

	}
}
