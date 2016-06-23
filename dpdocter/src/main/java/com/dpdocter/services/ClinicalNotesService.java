package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;

public interface ClinicalNotesService {
    ClinicalNotes addNotes(ClinicalNotesAddRequest request);

    ClinicalNotes getNotesById(String id);

    ClinicalNotes editNotes(ClinicalNotesEditRequest request);

    ClinicalNotes deleteNote(String id, Boolean discarded);

//    List<ClinicalNotes> getPatientsClinicalNotesWithVerifiedOTP(int page, int size, String patientId, String updatedTime, boolean discarded, boolean inHistory);
//
//    List<ClinicalNotes> getPatientsClinicalNotesWithoutVerifiedOTP(int page, int size, String patientId, String doctorId, String locationId, String hospitalId,
//	    String updatedTime, boolean discarded, boolean inHistory);

    Complaint addEditComplaint(Complaint complaint);

    Observation addEditObservation(Observation observation);

    Investigation addEditInvestigation(Investigation investigation);

    Diagnoses addEditDiagnosis(Diagnoses diagnosis);

    Notes addEditNotes(Notes notes);

    Diagram addEditDiagram(Diagram diagram);

    Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

    Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    List<Object> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded);

    void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId, String emailAddress);

    MailAttachment getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId);

    List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime, Boolean discarded);

	List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId, String hospitalId,	String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded, Boolean inHistory);

	String getClinicalNotesFile(String clinicalNotesId);

}
