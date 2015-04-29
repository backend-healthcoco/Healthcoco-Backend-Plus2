package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;

public interface HistoryServices {

	List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request);

	DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request);

	Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId);
	
	List<DiseaseListResponse> getDiseases(String doctorId, String hospitalId, String locationId);

	boolean addReportToHistory(String reportId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean addClinicalNotesToHistory(String clinicalNotesId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean addPrescriptionToHistory(String prescriptionId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean assignMedicalHistory(String diseaseId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean assignFamilyHistory(String diseaseId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean addSpetialNotes(List<String> spetialNotes,String patientId,String doctorId, String hospitalId, String locationId);

	boolean removeReports(String reportId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean removeClinicalNotes(String clinicalNotesId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean removePrescription(String prescriptionId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean removeMedicalHistory(String diseaseId,String patientId,String doctorId, String hospitalId, String locationId);
	
	boolean removeFamilyHistory(String diseaseId,String patientId,String doctorId, String hospitalId, String locationId);
	
}
