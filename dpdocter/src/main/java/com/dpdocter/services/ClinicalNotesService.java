package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;

public interface ClinicalNotesService {
	ClinicalNotes addNotes(ClinicalNotesAddRequest request);
	ClinicalNotes getNotesById(String id);
	ClinicalNotes editNotes(ClinicalNotesEditRequest request);
	void deleteNote(String id);
	List<ClinicalNotes> getPatientsClinicalNotesWithVarifiedOTP(String patientId);
	List<ClinicalNotes> getPatientsClinicalNotesWithoutVarifiedOTP(String patientId,String locationId);
	
	
}
