package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.PatientTrackService;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.CLINICAL_NOTES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalNotesApi {

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;

    @Autowired
    private PatientTrackService patientTrackService;

    @Path(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
    @POST
    public Response<ClinicalNotes> addNotes(ClinicalNotesAddRequest request) {
	ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request);

	// patient track
	if (clinicalNotes != null) {
	    patientTrackService.addRecord(request, VisitedFor.CLINICAL_NOTES);
	}

	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
    @POST
    public Response<ClinicalNotes> editNotes(ClinicalNotesEditRequest request) {
	ClinicalNotes clinicalNotes = clinicalNotesService.editNotes(request);

	// patient track
	if (clinicalNotes != null) {
	    patientTrackService.addRecord(request, VisitedFor.CLINICAL_NOTES);
	}

	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
    @GET
    public Response<Boolean> deleteNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId) {
	clinicalNotesService.deleteNote(clinicalNotesId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
    @GET
    public Response<ClinicalNotes> getNotesById(@PathParam(value = "clinicalNotesId") String clinicalNotesId) {
	ClinicalNotes clinicalNotes = clinicalNotesService.getNotesById(clinicalNotesId);
	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_DOCTOR_ID)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId,
	    @PathParam("isOTPVerified") boolean isOTPVerified) {
	return getAllNotes(doctorId, null, null, patientId, null, isOTPVerified, true);
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_DOCTOR_ID_CT)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId,
	    @PathParam("createdTime") String createdTime, @PathParam("isOTPVerified") boolean isOTPVerified) {
	return getAllNotes(doctorId, null, null, patientId, createdTime, isOTPVerified, true);
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_DOCTOR_ID_CT_ISDELETED)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId,
	    @PathParam("createdTime") String createdTime, @PathParam("isOTPVerified") boolean isOTPVerified, @PathParam(value = "isDeleted") boolean isDeleted) {
	return getAllNotes(doctorId, null, null, patientId, createdTime, isOTPVerified, isDeleted);
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "isOTPVerified") boolean isOTPVerified) {
	return getAllNotes(doctorId, locationId, hospitalId, patientId, null, isOTPVerified, true);
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_CT)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "patientId") String patientId, @PathParam("createdTime") String createdTime,
	    @PathParam(value = "isOTPVerified") boolean isOTPVerified) {
	return getAllNotes(doctorId, locationId, hospitalId, patientId, createdTime, isOTPVerified, true);
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_CT_ISDELETED)
    @GET
    public Response<ClinicalNotes> getNotes(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "patientId") String patientId, @PathParam("createdTime") String createdTime,
	    @PathParam(value = "isOTPVerified") boolean isOTPVerified, @PathParam(value = "isDeleted") boolean isDeleted) {
	return getAllNotes(doctorId, locationId, hospitalId, patientId, createdTime, isOTPVerified, isDeleted);
    }

    private Response<ClinicalNotes> getAllNotes(String doctorId, String locationId, String hospitalId, String patientId, String createdTime,
	    boolean isOTPVerified, boolean isDeleted) {
	List<ClinicalNotes> clinicalNotes = null;
	if (isOTPVerified) {
	    clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithVerifiedOTP(patientId, createdTime, isDeleted);
	} else {
	    clinicalNotes = clinicalNotesService
		    .getPatientsClinicalNotesWithoutVerifiedOTP(patientId, doctorId, locationId, hospitalId, createdTime, isDeleted);
	}

	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setDataList(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINIC_NOTES_COUNT)
    @GET
    public Response<Integer> getClinicalNotesCount(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	Integer clinicalNotesCount = clinicalNotesService.getClinicalNotesCount(doctorId, patientId, locationId, hospitalId);
	Response<Integer> response = new Response<Integer>();
	response.setData(clinicalNotesCount);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
    @POST
    public Response<Complaint> addComplaint(Complaint request) {
	Complaint complaint = clinicalNotesService.addEditComplaint(request);

	// Below service call will add or edit complaint in solr index.
	SolrComplaintsDocument solrComplaints = new SolrComplaintsDocument();
	BeanUtil.map(complaint, solrComplaints);
	if (request.getId() == null) {
	    solrClinicalNotesService.addComplaints(solrComplaints);
	} else {
	    solrClinicalNotesService.editComplaints(solrComplaints);
	}

	Response<Complaint> response = new Response<Complaint>();
	response.setData(complaint);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CUSTOM_COMPLAINTS)
    @GET
    public Response<Complaint> getCustomComplaints(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, or Hospital Id Cannot Be Empty");
	}
	List<Complaint> customComplaints = clinicalNotesService.getCustomComplaints(doctorId, locationId, hospitalId, page, size);
	Response<Complaint> response = new Response<Complaint>();
	response.setDataList(customComplaints);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
    @POST
    public Response<Observation> addObservation(Observation request) {
	Observation observation = clinicalNotesService.addEditObservation(request);

	SolrObservationsDocument solrObservations = new SolrObservationsDocument();
	BeanUtil.map(observation, solrObservations);
	if (request.getId() == null) {
	    solrClinicalNotesService.addObservations(solrObservations);
	} else {
	    solrClinicalNotesService.editObservations(solrObservations);
	}
	Response<Observation> response = new Response<Observation>();
	response.setData(observation);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CUSTOM_OBSERVATIONS)
    @GET
    public Response<Observation> getCustomObservations(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, or Hospital Id Cannot Be Empty");
	}
	List<Observation> customObservations = clinicalNotesService.getCustomObservations(doctorId, locationId, hospitalId, page, size);
	Response<Observation> response = new Response<Observation>();
	response.setDataList(customObservations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
    @POST
    public Response<Investigation> addInvestigation(Investigation request) {
	Investigation investigation = clinicalNotesService.addEditInvestigation(request);

	SolrInvestigationsDocument solrInvestigations = new SolrInvestigationsDocument();
	BeanUtil.map(investigation, solrInvestigations);
	if (request.getId() == null) {
	    solrClinicalNotesService.addInvestigations(solrInvestigations);
	} else {
	    solrClinicalNotesService.editInvestigations(solrInvestigations);
	}
	Response<Investigation> response = new Response<Investigation>();
	response.setData(investigation);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CUSTOM_INVESTIGATIONS)
    @GET
    public Response<Investigation> getCustomInvestigations(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, or Hospital Id Cannot Be Empty");
	}
	List<Investigation> customInvestigations = clinicalNotesService.getCustomInvestigations(doctorId, locationId, hospitalId, page, size);
	Response<Investigation> response = new Response<Investigation>();
	response.setDataList(customInvestigations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
    @POST
    public Response<Diagnoses> addDiagnosis(Diagnoses request) {
	Diagnoses diagnosis = clinicalNotesService.addEditDiagnosis(request);

	// Add diagnosis in solr index.
	SolrDiagnosesDocument solrDiagnoses = new SolrDiagnosesDocument();
	BeanUtil.map(diagnosis, solrDiagnoses);
	if (request.getId() == null) {
	    solrClinicalNotesService.addDiagnoses(solrDiagnoses);
	} else {
	    solrClinicalNotesService.editDiagnoses(solrDiagnoses);
	}

	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setData(diagnosis);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CUSTOM_DIAGNOSIS)
    @GET
    public Response<Diagnoses> getCustomDiagnosis(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, or Hospital Id Cannot Be Empty");
	}
	List<Diagnoses> customDiagnosis = clinicalNotesService.getCustomDiagnosis(doctorId, locationId, hospitalId, page, size);
	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setDataList(customDiagnosis);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_NOTES)
    @POST
    public Response<Notes> addNotes(Notes request) {
	Notes notes = clinicalNotesService.addEditNotes(request);

	// add notes in solr index.
	SolrNotesDocument solrNotes = new SolrNotesDocument();
	BeanUtil.map(notes, solrNotes);
	if (request.getId() == null) {
	    solrClinicalNotesService.addNotes(solrNotes);
	} else {
	    solrClinicalNotesService.editNotes(solrNotes);
	}

	Response<Notes> response = new Response<Notes>();
	response.setData(notes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
    @POST
    public Response<Diagram> addDiagram(Diagram request) {
	Diagram diagram = clinicalNotesService.addEditDiagram(request);

	SolrDiagramsDocument solrDiagrams = new SolrDiagramsDocument();
	BeanUtil.map(diagram, solrDiagrams);
	if (request.getId() == null) {
	    solrClinicalNotesService.addDiagrams(solrDiagrams);
	} else {
	    solrClinicalNotesService.editDiagrams(solrDiagrams);
	}

	Response<Diagram> response = new Response<Diagram>();
	response.setData(diagram);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
    @GET
    public Response<Boolean> deleteComplaint(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId);
	// Delete complaint in solr index.
	solrClinicalNotesService.deleteComplaints(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
    @GET
    public Response<Boolean> deleteObservation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId);
	solrClinicalNotesService.deleteObservations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
    @GET
    public Response<Boolean> deleteInvestigation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId);

	solrClinicalNotesService.deleteInvestigations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
    @GET
    public Response<Boolean> deleteDiagnosis(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId);

	// delete diagnosis in solr index.
	solrClinicalNotesService.deleteDiagnoses(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
    @GET
    public Response<Boolean> deleteNote(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId);

	// delete notes in solr index.
	solrClinicalNotesService.deleteNotes(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
    @GET
    public Response<Boolean> deleteDiagram(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId);

	solrClinicalNotesService.deleteDiagrams(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_COMPLAINTS)
    @GET
    public Response<Complaint> getComplaints(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Complaint> complaints = clinicalNotesService.getComplaints(doctorId, createdTime, true);
	Response<Complaint> response = new Response<Complaint>();
	response.setDataList(complaints);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_COMPLAINTS_ISDELETED)
    @GET
    public Response<Complaint> getComplaints(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Complaint> complaints = clinicalNotesService.getComplaints(doctorId, createdTime, isDeleted);
	Response<Complaint> response = new Response<Complaint>();
	response.setDataList(complaints);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_INVESTIGATIONS)
    @GET
    public Response<Investigation> getInvestigations(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Investigation> investigations = clinicalNotesService.getInvestigations(doctorId, createdTime, true);
	Response<Investigation> response = new Response<Investigation>();
	response.setDataList(investigations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_INVESTIGATIONS_ISDELETED)
    @GET
    public Response<Investigation> getInvestigations(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Investigation> investigations = clinicalNotesService.getInvestigations(doctorId, createdTime, isDeleted);
	Response<Investigation> response = new Response<Investigation>();
	response.setDataList(investigations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_OBSERVATIONS)
    @GET
    public Response<Observation> getObservations(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Observation> observations = clinicalNotesService.getObservations(doctorId, createdTime, true);
	Response<Observation> response = new Response<Observation>();
	response.setDataList(observations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_OBSERVATIONS_ISDELETED)
    @GET
    public Response<Observation> getObservations(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Observation> observations = clinicalNotesService.getObservations(doctorId, createdTime, isDeleted);
	Response<Observation> response = new Response<Observation>();
	response.setDataList(observations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_DIAGNOSIS)
    @GET
    public Response<Diagnoses> getDiagnosis(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Diagnoses> diagnosis = clinicalNotesService.getDiagnosis(doctorId, createdTime, true);
	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setDataList(diagnosis);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_DIAGNOSIS_ISDELETED)
    @GET
    public Response<Diagnoses> getDiagnosis(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Diagnoses> diagnosis = clinicalNotesService.getDiagnosis(doctorId, createdTime, isDeleted);
	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setDataList(diagnosis);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_NOTES)
    @GET
    public Response<Notes> getNotes(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Notes> notes = clinicalNotesService.getNotes(doctorId, createdTime, true);
	Response<Notes> response = new Response<Notes>();
	response.setDataList(notes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_NOTES_ISDELETED)
    @GET
    public Response<Notes> getCompleteNotes(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Notes> notes = clinicalNotesService.getNotes(doctorId, createdTime, isDeleted);
	Response<Notes> response = new Response<Notes>();
	response.setDataList(notes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_DIAGRAMS)
    @GET
    public Response<Diagram> getDiagrams(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	List<Diagram> diagrams = clinicalNotesService.getDiagrams(doctorId, createdTime, true);
	Response<Diagram> response = new Response<Diagram>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_DIAGRAMS_ISDELETED)
    @GET
    public Response<Diagram> getDiagrams(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
	    @PathParam("isDeleted") boolean isDeleted) {
	List<Diagram> diagrams = clinicalNotesService.getDiagrams(doctorId, createdTime, isDeleted);
	Response<Diagram> response = new Response<Diagram>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_DIAGRAMS)
    @GET
    public Response<Diagram> getGlobalDiagrams(@PathParam("createdTime") String createdTime) {
	List<Diagram> diagrams = clinicalNotesService.getGlobalDiagrams(createdTime);
	Response<Diagram> response = new Response<Diagram>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_COMPLAINTS)
    @GET
    public Response<Complaint> getComplaints(@PathParam("createdTime") String createdTime) {
	List<Complaint> complaints = clinicalNotesService.getComplaints(null, createdTime, true);
	Response<Complaint> response = new Response<Complaint>();
	response.setDataList(complaints);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_INVESTIGATIONS)
    @GET
    public Response<Investigation> getInvestigations(@PathParam("createdTime") String createdTime) {
	List<Investigation> investigations = clinicalNotesService.getInvestigations(null, createdTime, true);
	Response<Investigation> response = new Response<Investigation>();
	response.setDataList(investigations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_OBSERVATIONS)
    @GET
    public Response<Observation> getObservations(@PathParam("createdTime") String createdTime) {
	List<Observation> observations = clinicalNotesService.getObservations(null, createdTime, true);
	Response<Observation> response = new Response<Observation>();
	response.setDataList(observations);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_DIAGNOSIS)
    @GET
    public Response<Diagnoses> getDiagnosis(@PathParam("createdTime") String createdTime, @PathParam("isDeleted") boolean isDeleted) {
	List<Diagnoses> diagnosis = clinicalNotesService.getDiagnosis(null, createdTime, isDeleted);
	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setDataList(diagnosis);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_GLOBAL_NOTES)
    @GET
    public Response<Notes> getNotes(@PathParam("createdTime") String createdTime) {
	List<Notes> notes = clinicalNotesService.getNotes(null, createdTime, true);
	Response<Notes> response = new Response<Notes>();
	response.setDataList(notes);
	return response;
    }

}
