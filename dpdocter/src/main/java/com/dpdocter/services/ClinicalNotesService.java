package com.dpdocter.services;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;

public interface ClinicalNotesService {
	ClinicalNotes addNotes(ClinicalNotesAddRequest request);

	ClinicalNotes getNotesById(String id);

	ClinicalNotes editNotes(ClinicalNotesEditRequest request);

	void deleteNote(String id);

	List<ClinicalNotes> getPatientsClinicalNotesWithVerifiedOTP(String patientId, String createdTime);

	List<ClinicalNotes> getPatientsClinicalNotesWithoutVerifiedOTP(String patientId, String doctorId, String locationId, String hospitalId, String createdTime);

	Complaint addEditComplaint(Complaint complaint);

	Observation addEditObservation(Observation observation);

	Investigation addEditInvestigation(Investigation investigation);

	Diagnoses addEditDiagnosis(Diagnoses diagnosis);

	Notes addEditNotes(Notes notes);

	Diagram addEditDiagram(Diagram diagram);

	void deleteComplaint(String id, String doctorId, String locationId, String hospitalId);

	void deleteObservation(String id, String doctorId, String locationId, String hospitalId);

	void deleteInvestigation(String id, String doctorId, String locationId, String hospitalId);

	void deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId);

	void deleteNotes(String id, String doctorId, String locationId, String hospitalId);

	void deleteDiagram(String id, String doctorId, String locationId, String hospitalId);

	List<Complaint> getCustomComplaints(String doctorId, String locationId, String hospitalId, int page, int size);

	List<Diagnoses> getCustomDiagnosis(String doctorId, String locationId, String hospitalId, int page, int size);

	List<Investigation> getCustomInvestigations(String doctorId, String locationId, String hospitalId, int page, int size);

	List<Observation> getCustomObservations(String doctorId, String locationId, String hospitalId, int page, int size);

	@Query(value = "{'doctorId': ?0, 'patientId': ?1, 'locationId': ?2, 'hospitalId': ?3}", count = true)
	Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId);

	List<Complaint> getComplaints(String doctorId, String createdTime);

	List<Investigation> getInvestigations(String doctorId, String createdTime);

	List<Observation> getObservations(String doctorId, String createdTime);

	List<Diagnoses> getDiagnosis(String doctorId, String createdTime);

	List<Notes> getNotes(String doctorId, String createdTime);

	List<Diagram> getDiagrams(String doctorId, String createdTime);

}
