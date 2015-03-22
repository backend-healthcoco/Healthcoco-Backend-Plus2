package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnosis;
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
	List<ClinicalNotes> getPatientsClinicalNotesWithVarifiedOTP(String patientId);
	List<ClinicalNotes> getPatientsClinicalNotesWithoutVarifiedOTP(String patientId,String doctorId);
	
	Complaint addEditComplaint(Complaint complaint);
	Observation addEditObservation(Observation observation);
	Investigation addEditInvestigation(Investigation investigation);
	Diagnosis addEditDiagnosis(Diagnosis diagnosis);
	Notes addEditNotes(Notes notes);
	Diagram addEditDiagram(Diagram diagram);
	
	
	void deleteComplaint(String id,String doctorId);
	void deleteObservation(String id,String doctorId);
	void deleteInvestigation(String id,String doctorId);
	void deleteDiagnosis(String id,String doctorId);
	void deleteNotes(String id,String doctorId);
	void deleteDiagram(String id,String doctorId);
	
	
	
}
