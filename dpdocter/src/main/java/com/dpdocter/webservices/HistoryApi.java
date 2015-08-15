package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.SpecialNotesAddRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.PatientTrackService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.HISTORY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoryApi {
    @Autowired
    private HistoryServices historyServices;

    @Autowired
    private PatientTrackService patientTrackService;

    @Path(value = PathProxy.HistoryUrls.ADD_DISEASE)
    @POST
    public Response<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setDataList(diseases);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.EDIT_DISEASE)
    @POST
    public Response<DiseaseAddEditResponse> editDisease(DiseaseAddEditRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setData(diseases);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.DELETE_DISEASE)
    @GET
    public Response<Boolean> deleteDisease(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
	if (StringUtils.isEmpty(diseaseId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	Boolean diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(diseaseDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.GET_DISEASES)
    @GET
    public Response<DiseaseListResponse> getDiseases(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	List<DiseaseListResponse> diseaseListResponse = historyServices.getDiseases(doctorId, hospitalId, locationId);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(diseaseListResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.GET_CUSTOM_GLOBAL_DISEASES)
    @GET
    public Response<DiseaseListResponse> getCustomGlobalDiseases(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "createdTime") String createdTime) {
	if (StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Created Time Cannot Be Empty");
	}
	List<DiseaseListResponse> diseaseListResponse = historyServices.getCustomGlobalDiseases(doctorId, createdTime, true);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(diseaseListResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.GET_CUSTOM_GLOBAL_DISEASES_ISDELETED)
    @GET
    public Response<DiseaseListResponse> getCustomGlobalDiseases(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "createdTime") String createdTime,
	    @PathParam(value = "isDeleted") boolean isDeleted) {
	if (StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Created Time Cannot Be Empty");
	}
	List<DiseaseListResponse> diseaseListResponse = historyServices.getCustomGlobalDiseases(doctorId, createdTime, isDeleted);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(diseaseListResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ADD_REPORT_TO_HISTORY)
    @GET
    public Response<Boolean> addReportToHistory(@PathParam(value = "reportId") String reportId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (StringUtils.isEmpty(reportId) || StringUtils.isEmpty(patientId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
		|| StringUtils.isEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	boolean addReportToHistoryResponse = historyServices.addReportToHistory(reportId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addReportToHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ADD_CLINICAL_NOTES_TO_HISTORY)
    @GET
    public Response<Boolean> addClinicalNotesToHistory(@PathParam(value = "clinicalNotesId") String clinicalNotesId,
	    @PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	boolean addClinicalNotesToHistoryResponse = historyServices.addClinicalNotesToHistory(clinicalNotesId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addClinicalNotesToHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ADD_PRESCRIPTION_TO_HISTORY)
    @GET
    public Response<Boolean> addPrescriptionToHistory(@PathParam(value = "prescriptionId") String prescriptionId,
	    @PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(prescriptionId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
	}
	boolean addPrescriptionToHistoryResponse = historyServices.addPrescriptionToHistory(prescriptionId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addPrescriptionToHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ASSIGN_MEDICAL_HISTORY)
    @GET
    public Response<Boolean> assignMedicalHistory(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	boolean assignMedicalHistoryResponse = historyServices.assignMedicalHistory(diseaseId, patientId, doctorId, hospitalId, locationId);

	// patient track
	patientTrackService.addRecord(patientId, doctorId, locationId, hospitalId, VisitedFor.PERSONAL_HISTORY);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(assignMedicalHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ASSIGN_FAMILY_HISTORY)
    @GET
    public Response<Boolean> assignFamilyHistory(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	boolean assignFamilyHistoryResponse = historyServices.assignFamilyHistory(diseaseId, patientId, doctorId, hospitalId, locationId);

	// patient track
	patientTrackService.addRecord(patientId, doctorId, locationId, hospitalId, VisitedFor.FAMILY_HISTORY);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(assignFamilyHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.ADD_SPECIAL_NOTES)
    @POST
    public Response<Boolean> addSpecialNotes(SpecialNotesAddRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	boolean addSpecialNotesResponse = historyServices.addSpecialNotes(request.getSpecialNotes(), request.getPatientId(), request.getDoctorId(),
		request.getHospitalId(), request.getLocationId());

	Response<Boolean> response = new Response<Boolean>();
	response.setData(addSpecialNotesResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.REMOVE_REPORTS)
    @GET
    public Response<Boolean> removeReports(@PathParam(value = "reportId") String reportId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(reportId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Report Id, Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	boolean removeReportsResponse = historyServices.removeReports(reportId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(removeReportsResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.REMOVE_CLINICAL_NOTES)
    @GET
    public Response<Boolean> removeClinicalNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId,
	    @PathParam(value = "patientId") String patientId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Clinical Notes Id, Patient Id, Doctor Id, Hospital Id, Location Id");
	}
	boolean removeClinicalNotesResponse = historyServices.removeClinicalNotes(clinicalNotesId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(removeClinicalNotesResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.REMOVE_PRESCRIPTION)
    @GET
    public Response<Boolean> removePrescription(@PathParam(value = "prescriptionId") String prescriptionId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(prescriptionId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Patient Id, Doctor Id, Hosoital Id, Location Id Cannot Be Empty");
	}
	boolean removePrescriptionResponse = historyServices.removePrescription(prescriptionId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(removePrescriptionResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.REMOVE_MEDICAL_HISTORY)
    @GET
    public Response<Boolean> removeMedicalHistory(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
	}
	boolean removeMedicalHistoryResponse = historyServices.removeMedicalHistory(diseaseId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(removeMedicalHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.REMOVE_FAMILY_HISTORY)
    @GET
    public Response<Boolean> removeFamilyHistory(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId, patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id, Patient Id, Doctor Id, Hospital Id, Location Id");
	}
	boolean removeFamilyHistoryResponse = historyServices.removeFamilyHistory(diseaseId, patientId, doctorId, hospitalId, locationId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(removeFamilyHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.GET_PATIENT_HISTORY_OTP_VERIFIED)
    @GET
    public Response<HistoryDetailsResponse> getPatientHistoryDetailsOTP(@PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "historyFilter") String historyFilter,
	    @PathParam(value = "otpVerified") boolean otpVerified) {
	if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId, historyFilter)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Patient Id, Doctor Id, Hospital Id, Location Id, History Filter Cannot Be Empty");
	}
	List<HistoryDetailsResponse> historyDetailsResponses = null;
	if (otpVerified) {
	    historyDetailsResponses = historyServices.getPatientHistoryDetailsWithVerifiedOTP(patientId, doctorId, hospitalId, locationId, historyFilter);
	} else {
	    historyDetailsResponses = new ArrayList<HistoryDetailsResponse>();
	    historyDetailsResponses.add(historyServices.getPatientHistoryDetailsWithoutVerifiedOTP(patientId, doctorId, hospitalId, locationId, historyFilter));
	}
	Response<HistoryDetailsResponse> response = new Response<HistoryDetailsResponse>();
	response.setDataList(historyDetailsResponses);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.HANDLE_MEDICAL_HISTORY)
    @POST
    public Response<Boolean> handleMedicalHistory(MedicalHistoryHandler request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Null");
	}
	boolean handleMedicalHistoryResponse = historyServices.handleMedicalHistory(request);

	// patient track
	patientTrackService.addRecord(request.getPatientId(), request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
		VisitedFor.PERSONAL_HISTORY);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(handleMedicalHistoryResponse);
	return response;
    }

    @Path(value = PathProxy.HistoryUrls.GET_MEDICAL_HISTORY)
    @GET
    public Response<DiseaseListResponse> getPatientMedicalHistory(@PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	List<DiseaseListResponse>  medicalHistory = new ArrayList<DiseaseListResponse>();
	medicalHistory = historyServices.getPatientMedicalHistory(patientId, doctorId, hospitalId, locationId);
	
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(medicalHistory);
	return response;
    }
    
    @Path(value = PathProxy.HistoryUrls.HANDLE_FAMILY_HISTORY)
    @POST
    public Response<Boolean> handleFamilyHistory(MedicalHistoryHandler request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Null");
	}
	boolean handleFamilyHistoryResponse = historyServices.handleFamilyHistory(request);

	// patient track
	patientTrackService.addRecord(request.getPatientId(), request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
		VisitedFor.FAMILY_HISTORY);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(handleFamilyHistoryResponse);
	return response;
    }
    
    @Path(value = PathProxy.HistoryUrls.GET_FAMILY_HISTORY)
    @GET
    public Response<DiseaseListResponse> getPatientFamilyHistory(@PathParam(value = "patientId") String patientId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "locationId") String locationId) {
    	if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
    	    throw new BusinessException(ServiceError.InvalidInput, "Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	List<DiseaseListResponse>  familyHistory = new ArrayList<DiseaseListResponse>();
    	familyHistory = historyServices.getPatientFamilyHistory(patientId, doctorId, hospitalId, locationId);
    	
    	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
    	response.setDataList(familyHistory);
    	return response;    
    }
}
