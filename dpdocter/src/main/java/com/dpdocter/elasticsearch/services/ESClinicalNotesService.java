package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;

import common.util.web.Response;

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

	Response<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPresentComplaintDocument> searchPresentComplaints(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPresentComplaintHistoryDocument> searchPresentComplaintsHistory(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm);

	Response<ESProvisionalDiagnosisDocument> searchProvisionalDiagnosis(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESGeneralExamDocument> searchGeneralExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESSystemExamDocument> searchSystemExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESMenstrualHistoryDocument> searchMenstrualHistory(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addObstetricsHistory(ESObstetricHistoryDocument request);

	Response<ESObstetricHistoryDocument> searchObstetricHistory(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESIndicationOfUSGDocument> searchIndicationOfUSG(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addIndicationOfUSG(ESIndicationOfUSGDocument request);

	boolean addPA(ESPADocument request);

	Response<ESPADocument> searchPA(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPVDocument> searchPV(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPSDocument> searchPS(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

	boolean addPV(ESPVDocument request);

	boolean addPS(ESPSDocument request);

	boolean addXRayDetails(ESXRayDetailsDocument request);

	boolean addECGDetails(ESECGDetailsDocument request);

	boolean addEcho(ESEchoDocument request);

	boolean addHolter(ESHolterDocument request);

	Response<ESXRayDetailsDocument> searchXRayDetails(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESECGDetailsDocument> searchECGDetails(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESEchoDocument> searchEcho(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESHolterDocument> searchHolter(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addProcedureNote(ESProcedureNoteDocument request);

	Response<ESProcedureNoteDocument> searchProcedureNote(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addPCNose(ESPresentingComplaintNoseDocument request);

	boolean addPCEars(ESPresentingComplaintEarsDocument request);

	boolean addPCThroat(ESPresentingComplaintThroatDocument request);

	boolean addPCOralCavity(ESPresentingComplaintOralCavityDocument request);

	Response<ESPresentingComplaintNoseDocument> searchPCNose(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPresentingComplaintEarsDocument> searchPCEars(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPresentingComplaintOralCavityDocument> searchPCOralCavity(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESPresentingComplaintThroatDocument> searchPCThroat(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addNeckExam(ESNeckExaminationDocument request);

	boolean addNoseExam(ESNoseExaminationDocument request);

	boolean addEarsExam(ESEarsExaminationDocument request);

	boolean addOralCavityThroatExam(ESOralCavityAndThroatExaminationDocument request);

	boolean addIndirectLarygoscopyExam(ESIndirectLarygoscopyExaminationDocument request);

	Response<ESNeckExaminationDocument> searchNeckExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESNoseExaminationDocument> searchNoseExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	Response<ESIndirectLarygoscopyExaminationDocument> searchIndirectLarygoscopyExam(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm);

	Response<ESOralCavityAndThroatExaminationDocument> searchOralCavityThroatExam(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm);

	Response<ESEarsExaminationDocument> searchEarsExam(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
