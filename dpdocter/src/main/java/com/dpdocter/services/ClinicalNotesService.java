package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.XRayDetails;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.response.MailResponse;

public interface ClinicalNotesService {
	ClinicalNotes addNotes(ClinicalNotesAddRequest request, Boolean isAppointmentAdd, String createdBy, Appointment appointment);

	ClinicalNotes getNotesById(String id, ObjectId visitId);

	ClinicalNotes editNotes(ClinicalNotesEditRequest request);

	ClinicalNotes deleteNote(String id, Boolean discarded);

	Complaint addEditComplaint(Complaint complaint);

	Observation addEditObservation(Observation observation);

	Investigation addEditInvestigation(Investigation investigation);

	Diagnoses addEditDiagnosis(Diagnoses diagnosis);

	Notes addEditNotes(Notes notes);

	Diagram addEditDiagram(Diagram diagram);

	Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Integer getClinicalNotesCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	List<?> getClinicalItems(String type, String range, long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId,
			String hospitalId);

	List<ClinicalNotes> getClinicalNotes(String patientId, long page, int size, String updatedTime, Boolean discarded);

	List<ClinicalNotes> getClinicalNotes(long page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded, Boolean inHistory);

	String getClinicalNotesFile(String clinicalNotesId, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA,
			Boolean showUSG, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD, Boolean showNoOfChildren);

	Boolean updateQuery();

	ProvisionalDiagnosis addEditProvisionalDiagnosis(ProvisionalDiagnosis provisionalDiagnosis);

	GeneralExam addEditGeneralExam(GeneralExam generalExam);

	SystemExam addEditSystemExam(SystemExam systemExam);

	MenstrualHistory addEditMenstrualHistory(MenstrualHistory menstrualHistory);

	PresentComplaint addEditPresentComplaint(PresentComplaint presentComplaint);

	PresentComplaintHistory addEditPresentComplaintHistory(PresentComplaintHistory presentComplaintHistory);

	ObstetricHistory addEditObstetricHistory(ObstetricHistory obstetricHistory);

	ProvisionalDiagnosis deleteProvisionalDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentComplaint deletePresentComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentComplaintHistory deletePresentComplaintHistory(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	GeneralExam deleteGeneralExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	SystemExam deleteSystemExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ObstetricHistory deleteObstetricHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	MenstrualHistory deleteMenstrualHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	IndicationOfUSG deleteIndicationOfUSG(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	IndicationOfUSG addEditIndicationOfUSG(IndicationOfUSG indicationOfUSG);

	PA addEditPA(PA pa);

	PA deletePA(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	PV deletePV(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	PV addEditPV(PV pv);

	PS addEditPS(PS ps);

	PS deletePS(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ECGDetails addEditECGDetails(ECGDetails ecgDetails);

	XRayDetails addEditXRayDetails(XRayDetails xRayDetails);

	Echo addEditEcho(Echo echo);

	Holter addEditHolter(Holter holter);

	XRayDetails deleteXRayDetails(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ECGDetails deleteECGDetails(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Echo deleteEcho(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Holter deleteHolter(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ProcedureNote addEditProcedureNote(ProcedureNote precedureNote);

	ProcedureNote deleteProcedureNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintNose addEditPCNose(PresentingComplaintNose presentingComplaintNotes);

	PresentingComplaintEars addEditPCEars(PresentingComplaintEars presentingComplaintEars);

	PresentingComplaintThroat addEditPCThroat(PresentingComplaintThroat presentingComplaintThroat);

	PresentingComplaintOralCavity addEditPCOralCavity(PresentingComplaintOralCavity presentingComplaintOralCavity);

	NoseExamination addEditNoseExam(NoseExamination noseExamination);

	EarsExamination addEditEarsExam(EarsExamination earsExamination);

	NeckExamination addEditNeckExam(NeckExamination neckExamination);

	OralCavityAndThroatExamination addEditOralCavityThroatExam(
			OralCavityAndThroatExamination oralCavityAndThroatExamination);

	IndirectLarygoscopyExamination addEditIndirectLarygoscopyExam(
			IndirectLarygoscopyExamination indirectLarygoscopyExamination);

	PresentingComplaintNose deletePCNose(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintEars deletePCEars(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintOralCavity deletePCOralCavity(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintThroat deletePCThroat(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	NeckExamination deleteNeckExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	NoseExamination deleteNoseExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	OralCavityAndThroatExamination deleteOralCavityThroatExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	EarsExamination deleteEarsExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	IndirectLarygoscopyExamination deleteIndirectLarygoscopyExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	//List<Diagnoses> getDiagnosesListBySpeciality(String speciality);

	List<Diagnoses> getDiagnosesListBySpeciality(String speciality, String searchTerm);

	String downloadMultipleClinicalNotes(List<String> ids);

	void emailMultipleClinicalNotes(List<String> ids, String emailAddress);

}
