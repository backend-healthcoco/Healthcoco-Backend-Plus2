package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;

public interface ESClinicalNotesService {

	boolean addComplaints(ESComplaintsDocument request);

	boolean addDiagnoses(ESDiagnosesDocument request);

	boolean addNotes(ESNotesDocument request);

	boolean addDiagrams(ESDiagramsDocument request);

	// List<ESDiagramsDocument> searchDiagramsBySpeciality(String searchTerm);

	boolean addInvestigations(ESInvestigationsDocument request);

	boolean addObservations(ESObservationsDocument request);

	boolean addPresentComplaint(ESPresentComplaintDocument request);

	boolean addPresentComplaintHistory(ESPresentComplaintHistoryDocument request);

	boolean addProvisionalDiagnosis(ESProvisionalDiagnosisDocument request);

	boolean addSystemExam(ESSystemExamDocument request);

	boolean addGeneralExam(ESGeneralExamDocument request);

	boolean addMenstrualHistory(ESMenstrualHistoryDocument request);

	List<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESPresentComplaintDocument> searchPresentComplaints(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESPresentComplaintHistoryDocument> searchPresentComplaintsHistory(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm);

	List<ESProvisionalDiagnosisDocument> searchProvisionalDiagnosis(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESGeneralExamDocument> searchGeneralExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESSystemExamDocument> searchSystemExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESMenstrualHistoryDocument> searchMenstrualHistory(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addObstetricsHistory(ESObstetricHistoryDocument request);

	List<ESObstetricHistoryDocument> searchObstetricHistory(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESIndicationOfUSGDocument> searchIndicationOfUSG(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addIndicationOfUSG(ESIndicationOfUSGDocument request);

	boolean addPA(ESPADocument request);

	List<ESPADocument> searchPA(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	List<ESPVDocument> searchPV(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	List<ESPSDocument> searchPS(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	boolean addPV(ESPVDocument request);

	boolean addPS(ESPSDocument request);

	boolean addXRayDetails(ESXRayDetailsDocument request);

	boolean addECGDetails(ESECGDetailsDocument request);

	boolean addEcho(ESEchoDocument request);

	boolean addHolter(ESHolterDocument request);

	List<ESXRayDetailsDocument> searchXRayDetails(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESECGDetailsDocument> searchECGDetails(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESEchoDocument> searchEcho(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESHolterDocument> searchHolter(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addProcedureNote(ESProcedureNoteDocument request);

	List<ESProcedureNoteDocument> searchProcedureNote(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
