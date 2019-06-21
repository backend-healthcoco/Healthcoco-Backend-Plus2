package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.dpdocter.beans.BirthHistory;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.HistoryFilter;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.DrugsAndAllergiesAddRequest;
import com.dpdocter.request.PersonalHistoryAddRequest;
import com.dpdocter.request.SpecialNotesAddRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.HISTORY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.HISTORY_BASE_URL, description = "Endpoint for history")
public class HistoryApi {

	private static Logger logger = Logger.getLogger(HistoryApi.class.getName());

	@Autowired
	private HistoryServices historyServices;

	@Autowired
	private PatientVisitService patientTrackService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESMasterService esMasterService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.HistoryUrls.ADD_DISEASE)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_DISEASE, notes = PathProxy.HistoryUrls.ADD_DISEASE)
	public Response<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
		if (request == null || request.isEmpty() || DPDoctorUtils.anyStringEmpty(request.get(0).getDoctorId(),
				request.get(0).getLocationId(), request.get(0).getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
		for (DiseaseAddEditResponse addEditResponse : diseases) {
			transactionalManagementService.addResource(new ObjectId(addEditResponse.getId()), Resource.DISEASE, false);
			ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
			BeanUtil.map(addEditResponse, esDiseasesDocument);
			esMasterService.addEditDisease(esDiseasesDocument);
		}
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setDataList(diseases);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.EDIT_DISEASE)
	@PUT
	@ApiOperation(value = PathProxy.HistoryUrls.EDIT_DISEASE, notes = PathProxy.HistoryUrls.EDIT_DISEASE)
	public Response<DiseaseAddEditResponse> editDisease(@PathParam(value = "diseaseId") String diseaseId,
			DiseaseAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(diseaseId, request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(diseaseId);
		DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
		transactionalManagementService.addResource(new ObjectId(diseases.getId()), Resource.DISEASE, false);
		ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
		BeanUtil.map(diseases, esDiseasesDocument);
		esMasterService.addEditDisease(esDiseasesDocument);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setData(diseases);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.DELETE_DISEASE)
	@DELETE
	@ApiOperation(value = PathProxy.HistoryUrls.DELETE_DISEASE, notes = PathProxy.HistoryUrls.DELETE_DISEASE)
	public Response<DiseaseAddEditResponse> deleteDisease(@PathParam(value = "diseaseId") String diseaseId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId, doctorId, hospitalId, locationId)) {
			logger.warn("Disease Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Disease Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		DiseaseAddEditResponse diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId,
				locationId, discarded);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setData(diseaseDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_DISEASES)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_DISEASES, notes = PathProxy.HistoryUrls.GET_DISEASES)
	public Response<DiseaseListResponse> getDiseases(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Range or Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Range or Doctor Id Cannot Be Empty");
		}
		Response<DiseaseListResponse> response = historyServices.getDiseases(range, page, size, doctorId,
				hospitalId, locationId, updatedTime, discarded, false, null);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ADD_REPORT_TO_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_REPORT_TO_HISTORY, notes = PathProxy.HistoryUrls.ADD_REPORT_TO_HISTORY)
	public Response<Records> addReportToHistory(@PathParam(value = "reportId") String reportId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(reportId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Records addReportToHistoryResponse = historyServices.addReportToHistory(reportId, patientId, doctorId,
				hospitalId, locationId);
		Response<Records> response = new Response<Records>();
		response.setData(addReportToHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ADD_CLINICAL_NOTES_TO_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_CLINICAL_NOTES_TO_HISTORY, notes = PathProxy.HistoryUrls.ADD_CLINICAL_NOTES_TO_HISTORY)
	public Response<ClinicalNotes> addClinicalNotesToHistory(
			@PathParam(value = "clinicalNotesId") String clinicalNotesId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ClinicalNotes addClinicalNotesToHistoryResponse = historyServices.addClinicalNotesToHistory(clinicalNotesId,
				patientId, doctorId, hospitalId, locationId);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(addClinicalNotesToHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ADD_PRESCRIPTION_TO_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_PRESCRIPTION_TO_HISTORY, notes = PathProxy.HistoryUrls.ADD_PRESCRIPTION_TO_HISTORY)
	public Response<Prescription> addPrescriptionToHistory(@PathParam(value = "prescriptionId") String prescriptionId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
		}

		Prescription addPrescriptionToHistoryResponse = historyServices.addPrescriptionToHistory(prescriptionId,
				patientId, doctorId, hospitalId, locationId);

		Response<Prescription> response = new Response<Prescription>();
		response.setData(addPrescriptionToHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ADD_PATIENT_TREATMENT_TO_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_PATIENT_TREATMENT_TO_HISTORY, notes = PathProxy.HistoryUrls.ADD_PATIENT_TREATMENT_TO_HISTORY)
	public Response<PatientTreatmentResponse> addPatientTreatmentToHistory(
			@PathParam(value = "treatmentId") String treatmentId, @PathParam(value = "patientId") String patientId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Treatment Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Treatment Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
		}

		PatientTreatmentResponse patientTreatmentResponse = historyServices.addPatientTreatmentToHistory(treatmentId,
				patientId, doctorId, hospitalId, locationId);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(patientTreatmentResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ASSIGN_MEDICAL_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ASSIGN_MEDICAL_HISTORY, notes = PathProxy.HistoryUrls.ASSIGN_MEDICAL_HISTORY)
	public Response<HistoryDetailsResponse> assignMedicalHistory(@PathParam(value = "diseaseId") String diseaseId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse assignMedicalHistoryResponse = historyServices.assignMedicalHistory(diseaseId, patientId,
				doctorId, hospitalId, locationId);

		// patient track
		patientTrackService.addRecord(patientId, doctorId, locationId, hospitalId, VisitedFor.PERSONAL_HISTORY);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(assignMedicalHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ASSIGN_FAMILY_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.ASSIGN_FAMILY_HISTORY, notes = PathProxy.HistoryUrls.ASSIGN_FAMILY_HISTORY)
	public Response<HistoryDetailsResponse> assignFamilyHistory(@PathParam(value = "diseaseId") String diseaseId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse assignFamilyHistoryResponse = historyServices.assignFamilyHistory(diseaseId, patientId,
				doctorId, hospitalId, locationId);

		// patient track
		patientTrackService.addRecord(patientId, doctorId, locationId, hospitalId, VisitedFor.FAMILY_HISTORY);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(assignFamilyHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ADD_SPECIAL_NOTES)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.ADD_SPECIAL_NOTES, notes = PathProxy.HistoryUrls.ADD_SPECIAL_NOTES)
	public Response<Boolean> addSpecialNotes(SpecialNotesAddRequest request) {
		if (request == null) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		boolean addSpecialNotesResponse = historyServices.addSpecialNotes(request.getSpecialNotes(),
				request.getPatientId(), request.getDoctorId(), request.getHospitalId(), request.getLocationId());

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addSpecialNotesResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_REPORTS, notes = PathProxy.HistoryUrls.REMOVE_REPORTS)
	public Response<Records> removeReports(@PathParam(value = "reportId") String reportId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(reportId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Records removeReportsResponse = historyServices.removeReports(reportId, patientId, doctorId, hospitalId,
				locationId);
		Response<Records> response = new Response<Records>();
		response.setData(removeReportsResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_CLINICAL_NOTES)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_CLINICAL_NOTES, notes = PathProxy.HistoryUrls.REMOVE_CLINICAL_NOTES)
	public Response<ClinicalNotes> removeClinicalNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id");
			throw new BusinessException(ServiceError.InvalidInput,
					"Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id");
		}
		ClinicalNotes removeClinicalNotesResponse = historyServices.removeClinicalNotes(clinicalNotesId, patientId,
				doctorId, hospitalId, locationId);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(removeClinicalNotesResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_PRESCRIPTION)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_PRESCRIPTION, notes = PathProxy.HistoryUrls.REMOVE_PRESCRIPTION)
	public Response<Prescription> removePrescription(@PathParam(value = "prescriptionId") String prescriptionId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
		}
		Prescription removePrescriptionResponse = historyServices.removePrescription(prescriptionId, patientId,
				doctorId, hospitalId, locationId);
		Response<Prescription> response = new Response<Prescription>();
		response.setData(removePrescriptionResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_PATIENT_TREATMENT)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_PATIENT_TREATMENT, notes = PathProxy.HistoryUrls.REMOVE_PATIENT_TREATMENT)
	public Response<PatientTreatmentResponse> removePatientTreatment(
			@PathParam(value = "treatmentId") String treatmentId, @PathParam(value = "patientId") String patientId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(treatmentId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("TreatmentId Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"TreatmentId Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
		}
		PatientTreatmentResponse removePrescriptionResponse = historyServices.removePatientTreatment(treatmentId,
				patientId, doctorId, hospitalId, locationId);
		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setData(removePrescriptionResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_MEDICAL_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_MEDICAL_HISTORY, notes = PathProxy.HistoryUrls.REMOVE_MEDICAL_HISTORY)
	public Response<HistoryDetailsResponse> removeMedicalHistory(@PathParam(value = "diseaseId") String diseaseId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
			throw new BusinessException(ServiceError.InvalidInput,
					"Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
		}
		HistoryDetailsResponse removeMedicalHistoryResponse = historyServices.removeMedicalHistory(diseaseId, patientId,
				doctorId, hospitalId, locationId);
		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(removeMedicalHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.REMOVE_FAMILY_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.REMOVE_FAMILY_HISTORY, notes = PathProxy.HistoryUrls.REMOVE_FAMILY_HISTORY)
	public Response<HistoryDetailsResponse> removeFamilyHistory(@PathParam(value = "diseaseId") String diseaseId,
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
			throw new BusinessException(ServiceError.InvalidInput,
					"Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
		}
		HistoryDetailsResponse removeFamilyHistoryResponse = historyServices.removeFamilyHistory(diseaseId, patientId,
				doctorId, hospitalId, locationId);
		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(removeFamilyHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED, notes = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED)
	public Response<HistoryDetailsResponse> getPatientHistoryDetailsOTP(
			@PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@MatrixParam("historyFilter") List<String> historyFilter,
			@PathParam(value = "otpVerified") boolean otpVerified, @QueryParam("page") int page,
			@QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id, History Filter Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id, History Filter Cannot Be Empty");
		}
		List<HistoryDetailsResponse> historyDetailsResponses = null;
		if (otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId)) {
			historyDetailsResponses = historyServices.getPatientHistoryDetailsWithVerifiedOTP(patientId, doctorId,
					hospitalId, locationId, historyFilter, page, size, updatedTime);
		} else {
			historyDetailsResponses = historyServices.getPatientHistoryDetailsWithoutVerifiedOTP(patientId, doctorId,
					hospitalId, locationId, historyFilter, page, size, updatedTime);
		}
		if (historyDetailsResponses != null && !historyDetailsResponses.isEmpty())
			for (HistoryDetailsResponse historyDetailsResponse : historyDetailsResponses) {
				if (historyDetailsResponse.getGeneralRecords() != null) {
					for (GeneralData generalData : historyDetailsResponse.getGeneralRecords()) {
						if (generalData.getDataType().equals(HistoryFilter.CLINICAL_NOTES)) {
							if (((ClinicalNotes) generalData.getData()).getDiagrams() != null)
								((ClinicalNotes) generalData.getData()).setDiagrams(
										getFinalDiagrams(((ClinicalNotes) generalData.getData()).getDiagrams()));
						}
					}
				}
			}

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setDataList(historyDetailsResponses);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED_WEB)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED_WEB, notes = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED_WEB)
	public Response<HistoryDetailsResponse> getPatientHistoryDetailsOTPForWEB(
			@QueryParam(value = "patientId") String patientId, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@MatrixParam("historyFilter") List<String> historyFilter,
			@QueryParam(value = "otpVerified") boolean otpVerified, @QueryParam("page") int page,
			@QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id, History Filter Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id, History Filter Cannot Be Empty");
		}
		List<HistoryDetailsResponse> historyDetailsResponses = null;
		if (otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId)) {
			historyDetailsResponses = historyServices.getPatientHistoryDetailsWithVerifiedOTP(patientId, doctorId,
					hospitalId, locationId, historyFilter, page, size, updatedTime);
		} else {
			historyDetailsResponses = historyServices.getPatientHistoryDetailsWithoutVerifiedOTP(patientId, doctorId,
					hospitalId, locationId, historyFilter, page, size, updatedTime);
		}
		if (historyDetailsResponses != null && !historyDetailsResponses.isEmpty())
			for (HistoryDetailsResponse historyDetailsResponse : historyDetailsResponses) {
				if (historyDetailsResponse.getGeneralRecords() != null) {
					for (GeneralData generalData : historyDetailsResponse.getGeneralRecords()) {
						if (generalData.getDataType().equals(HistoryFilter.CLINICAL_NOTES)) {
							if (((ClinicalNotes) generalData.getData()).getDiagrams() != null)
								((ClinicalNotes) generalData.getData()).setDiagrams(
										getFinalDiagrams(((ClinicalNotes) generalData.getData()).getDiagrams()));
						}
					}
				}
			}
		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setDataList(historyDetailsResponses);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY, notes = PathProxy.HistoryUrls.GET_PATIENT_HISTORY)
	public Response<HistoryDetailsResponse> getPatientHistory(@PathParam(value = "patientId") String patientId,
			@MatrixParam("historyFilter") List<String> historyFilter, @QueryParam("page") int page,
			@QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<HistoryDetailsResponse> historyDetailsResponses = null;
		historyDetailsResponses = historyServices.getPatientHistory(patientId, historyFilter, page, size, updatedTime);

		if (historyDetailsResponses != null && !historyDetailsResponses.isEmpty())
			for (HistoryDetailsResponse historyDetailsResponse : historyDetailsResponses) {
				if (historyDetailsResponse.getGeneralRecords() != null) {
					for (GeneralData generalData : historyDetailsResponse.getGeneralRecords()) {
						if (generalData.getDataType().equals(HistoryFilter.CLINICAL_NOTES)) {
							if (((ClinicalNotes) generalData.getData()).getDiagrams() != null)
								((ClinicalNotes) generalData.getData()).setDiagrams(
										getFinalDiagrams(((ClinicalNotes) generalData.getData()).getDiagrams()));
						}
					}
				}
			}
		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setDataList(historyDetailsResponses);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.HANDLE_MEDICAL_HISTORY)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.HANDLE_MEDICAL_HISTORY, notes = PathProxy.HistoryUrls.HANDLE_MEDICAL_HISTORY)
	public Response<Boolean> handleMedicalHistory(MedicalHistoryHandler request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean handleMedicalHistoryResponse = historyServices.handleMedicalHistory(request);

		// patient track
		patientTrackService.addRecord(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), VisitedFor.PERSONAL_HISTORY);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(handleMedicalHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_MEDICAL_AND_FAMILY_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_MEDICAL_AND_FAMILY_HISTORY, notes = PathProxy.HistoryUrls.GET_MEDICAL_AND_FAMILY_HISTORY)
	public Response<HistoryDetailsResponse> getMedicalAndFamilyHistory(@PathParam(value = "patientId") String patientId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse history = historyServices.getMedicalAndFamilyHistory(patientId, doctorId, hospitalId,
				locationId);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(history);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.HANDLE_FAMILY_HISTORY)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.HANDLE_FAMILY_HISTORY, notes = PathProxy.HistoryUrls.HANDLE_FAMILY_HISTORY)
	public Response<Boolean> handleFamilyHistory(MedicalHistoryHandler request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean handleFamilyHistoryResponse = historyServices.handleFamilyHistory(request);

		// patient track
		patientTrackService.addRecord(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), VisitedFor.FAMILY_HISTORY);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(handleFamilyHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.MAIL_MEDICAL_DATA)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.MAIL_MEDICAL_DATA, notes = PathProxy.HistoryUrls.MAIL_MEDICAL_DATA)
	public Response<Boolean> mailMedicalData(MedicalData medicalData) {
		if (medicalData == null || DPDoctorUtils.anyStringEmpty(medicalData.getDoctorId(), medicalData.getLocationId(),
				medicalData.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		boolean mailMedicalDataResponse = historyServices.mailMedicalData(medicalData);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(mailMedicalDataResponse);
		return response;
	}

	// @Path(value = PathProxy.HistoryUrls.ADD_VISITS_TO_HISTORY)
	// @GET
	// public Response<Boolean> addVisitsToHistory(@PathParam(value = "visitId")
	// String visitId,
	// @PathParam(value = "patientId") String patientId, @PathParam(value =
	// "doctorId") String doctorId,
	// @PathParam(value = "locationId") String locationId, @PathParam(value =
	// "hospitalId") String hospitalId) {
	//
	// if (DPDoctorUtils.anyStringEmpty(visitId, patientId, doctorId,
	// hospitalId, locationId)) {
	// logger.warn("Visits Id, Patient Id, Doctor Id, Hospital Id, Location Id
	// Cannot Be Empty");
	// throw new BusinessException(ServiceError.InvalidInput,
	// "Visits Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be
	// Empty");
	// }
	// boolean addClinicalNotesToHistoryResponse =
	// historyServices.addVisitsToHistory(visitId, patientId, doctorId,
	// hospitalId, locationId);
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(addClinicalNotesToHistoryResponse);
	// return response;
	// }
	//
	// @Path(value = PathProxy.HistoryUrls.REMOVE_VISITS)
	// @GET
	// public Response<Boolean> removeVisits(@PathParam(value = "visitId")
	// String visitId,
	// @PathParam(value = "patientId") String patientId, @PathParam(value =
	// "doctorId") String doctorId,
	// @PathParam(value = "locationId") String locationId, @PathParam(value =
	// "hospitalId") String hospitalId) {
	//
	// if (DPDoctorUtils.anyStringEmpty(visitId, patientId, doctorId,
	// hospitalId, locationId)) {
	// logger.warn("Visits Id, Patient Id, Doctor Id, Hospital Id, Location Id
	// Cannot Be Empty");
	// throw new BusinessException(ServiceError.InvalidInput,
	// "Visits Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be
	// Empty");
	// }
	// boolean addClinicalNotesToHistoryResponse =
	// historyServices.removeVisits(visitId, patientId, doctorId, hospitalId,
	// locationId);
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(addClinicalNotesToHistoryResponse);
	// return response;
	// }

	@GET
	@ApiOperation(value = "GET_MULTIPLE_DATA", notes = "GET_MULTIPLE_DATA")
	public Response<HistoryDetailsResponse> getMultipleData(@QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@DefaultValue("true") @QueryParam(value = "inHistory") Boolean inHistory) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<HistoryDetailsResponse> historyDetailsResponses = historyServices.getMultipleData(patientId, doctorId,
				hospitalId, locationId, updatedTime, inHistory, discarded);

		if (historyDetailsResponses != null && !historyDetailsResponses.isEmpty())
			for (HistoryDetailsResponse historyDetailsResponse : historyDetailsResponses) {
				if (historyDetailsResponse.getGeneralRecords() != null) {
					for (GeneralData generalData : historyDetailsResponse.getGeneralRecords()) {
						if (generalData.getDataType().equals(HistoryFilter.CLINICAL_NOTES)) {
							if (((ClinicalNotes) generalData.getData()).getDiagrams() != null)
								((ClinicalNotes) generalData.getData()).setDiagrams(
										getFinalDiagrams(((ClinicalNotes) generalData.getData()).getDiagrams()));
						}
					}
				}
			}
		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setDataList(historyDetailsResponses);
		return response;

	}

	private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	@Path(value = PathProxy.HistoryUrls.GET_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_HISTORY, notes = PathProxy.HistoryUrls.GET_HISTORY)
	public Response<HistoryDetailsResponse> getHistory(@PathParam(value = "patientId") String patientId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId, @MatrixParam("type") List<String> type) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse history = historyServices.getHistory(patientId, doctorId, hospitalId, locationId, type);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(history);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ASSIGN_DRUG_ALLERGIES)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.ASSIGN_DRUG_ALLERGIES, notes = PathProxy.HistoryUrls.ASSIGN_DRUG_ALLERGIES)
	public Response<HistoryDetailsResponse> assignDrugAndAllergies(DrugsAndAllergiesAddRequest request) {
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getHospitalId(),
				request.getLocationId())) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse history = historyServices.assignDrugsAndAllergies(request);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(history);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.ASSIGN_PERSONAL_HISTORY)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.ASSIGN_PERSONAL_HISTORY, notes = PathProxy.HistoryUrls.ASSIGN_PERSONAL_HISTORY)
	public Response<HistoryDetailsResponse> assignPersonalHistory(PersonalHistoryAddRequest request) {
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getHospitalId(),
				request.getLocationId())) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		HistoryDetailsResponse history = historyServices.assignPersonalHistory(request);

		Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
		response.setData(history);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.SUBMIT_BIRTH_HITORY)
	@POST
	@ApiOperation(value = PathProxy.HistoryUrls.SUBMIT_BIRTH_HITORY, notes = PathProxy.HistoryUrls.SUBMIT_BIRTH_HITORY)
	public Response<BirthHistory> submitBirthHistory(BirthHistory request) {
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getHospitalId(),
				request.getLocationId())) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		BirthHistory birthHistory = historyServices.submitBirthHistory(request);

		Response<BirthHistory> response = new Response<BirthHistory>();
		response.setData(birthHistory);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.GET_BIRTH_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.HistoryUrls.GET_BIRTH_HISTORY, notes = PathProxy.HistoryUrls.GET_BIRTH_HISTORY)
	public Response<BirthHistory> getBirthHistory(@QueryParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Patient Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Patient Id Cannot Be Empty");
		}
		BirthHistory birthHistory = historyServices.getBirthHistory(patientId);

		Response<BirthHistory> response = new Response<BirthHistory>();
		response.setData(birthHistory);
		return response;
	}

}
