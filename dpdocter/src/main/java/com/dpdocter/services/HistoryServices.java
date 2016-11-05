package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Treatment;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.response.PatientTreatmentResponse;

import common.util.web.Response;

public interface HistoryServices {

    List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request);

    DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request);

    DiseaseAddEditResponse deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId, Boolean discarded);

    List<DiseaseListResponse> getDiseases(String range, int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean discarded, Boolean isAdmin, String searchTerm);

    List<DiseaseListResponse> getDiseasesByIds(List<ObjectId> diseasesIds);

    Records addReportToHistory(String reportId, String patientId, String doctorId, String hospitalId, String locationId);

    ClinicalNotes addClinicalNotesToHistory(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId);

    Prescription addPrescriptionToHistory(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId);

    PatientTreatmentResponse addPatientTreatmentToHistory(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId);

    HistoryDetailsResponse assignMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    HistoryDetailsResponse assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId);

    Records removeReports(String reportId, String patientId, String doctorId, String hospitalId, String locationId);

    ClinicalNotes removeClinicalNotes(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId);

    Prescription removePrescription(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId);

    PatientTreatmentResponse removePatientTreatment(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId);

    HistoryDetailsResponse removeMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    HistoryDetailsResponse removeFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId);

    List<HistoryDetailsResponse> getPatientHistoryDetailsWithoutVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime);

    List<HistoryDetailsResponse> getPatientHistoryDetailsWithVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime);

    Integer getHistoryCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    boolean handleMedicalHistory(MedicalHistoryHandler request);

    boolean handleFamilyHistory(MedicalHistoryHandler request);

    HistoryDetailsResponse getMedicalAndFamilyHistory(String patientId, String doctorId, String hospitalId, String locationId);

    boolean mailMedicalData(MedicalData medicalData);

    boolean addVisitsToHistory(String visitId, String patientId, String doctorId, String hospitalId, String locationId);

    boolean removeVisits(String visitId, String patientId, String doctorId, String hospitalId, String locationId);

    List<HistoryDetailsResponse> getMultipleData(String patientId, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean inHistory, Boolean discarded);

    List<HistoryDetailsResponse> getPatientHistory(String patientId, List<String> historyFilter, int page, int size, String updatedTime);

	Response getHistory(String patientId, String doctorId, String hospitalId, String locationId, List<String> type);

}
