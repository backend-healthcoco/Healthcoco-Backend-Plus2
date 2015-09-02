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

    List<ClinicalNotes> getPatientsClinicalNotesWithVerifiedOTP(int page, int size, String patientId, String updatedTime, boolean discarded);

    List<ClinicalNotes> getPatientsClinicalNotesWithoutVerifiedOTP(int page, int size, String patientId, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded);

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

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'locationId': ?2, 'hospitalId': ?3}", count = true)
    Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId);

	List<Object> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded);

}
