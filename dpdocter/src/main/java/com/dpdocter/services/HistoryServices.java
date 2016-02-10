package com.dpdocter.services;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;

public interface HistoryServices {

    List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request);

    DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request);

    Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId, Boolean discarded);

    List<DiseaseListResponse> getDiseases(String range, int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean discarded);

    List<DiseaseListResponse> getDiseasesByIds(List<String> diseasesIds);

    boolean addReportToHistory(String reportId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean addClinicalNotesToHistory(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean addPrescriptionToHistory(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean addPatientTreatmentToHistory(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean assignMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeReports(String reportId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeClinicalNotes(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removePrescription(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removePatientTreatment(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    List<HistoryDetailsResponse> getPatientHistoryDetailsWithoutVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime);

    List<HistoryDetailsResponse> getPatientHistoryDetailsWithVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime);

    Integer getHistoryCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    boolean handleMedicalHistory(MedicalHistoryHandler request);

    boolean handleFamilyHistory(MedicalHistoryHandler request);

    HistoryDetailsResponse getMedicalAndFamilyHistory(String patientId, String doctorId, String hospitalId, String locationId);

    boolean mailMedicalData(MedicalData medicalData, UriInfo uriInfo);

    boolean addVisitsToHistory(String visitId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeVisits(String visitId, String patientId, String doctorId, String hospitalId, String locationId);

    List<HistoryDetailsResponse> getMultipleData(String patientId, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean inHistory);

    List<HistoryDetailsResponse> getPatientHistory(String patientId, List<String> historyFilter, int page, int size, String updatedTime);

}
