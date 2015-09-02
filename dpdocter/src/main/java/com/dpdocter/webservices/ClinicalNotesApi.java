package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    @PUT
    public Response<ClinicalNotes> editNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, ClinicalNotesEditRequest request) {
    	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId)) {
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Id Cannot Be Empty");
    	}	
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
    @DELETE
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
    
    @GET
    public Response<ClinicalNotes> getNotes(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
    		@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "patientId") String patientId, @QueryParam("updatedTime") String updatedTime,
    		@QueryParam(value = "isOTPVerified") Boolean isOTPVerified, @QueryParam(value = "discarded") Boolean discarded) {
	return getAllNotes(page, size, doctorId, locationId, hospitalId, patientId, updatedTime, isOTPVerified, discarded);
    }

    private Response<ClinicalNotes> getAllNotes(int page, int size, String doctorId, String locationId, String hospitalId, String patientId, String updatedTime,
	    Boolean isOTPVerified, Boolean discarded) {
	List<ClinicalNotes> clinicalNotes = null;
	
	if(isOTPVerified != null){
		if (isOTPVerified) {
		    clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithVerifiedOTP(page, size, patientId, updatedTime, discarded !=null ?discarded:true);
		} else {
		    clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithoutVerifiedOTP(page, size, patientId, doctorId, locationId, hospitalId, updatedTime, discarded !=null ?discarded:true);
		}
	}
	else{
		clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithoutVerifiedOTP(page, size, patientId, doctorId, locationId, hospitalId, updatedTime, discarded !=null ?discarded:true);
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
    @DELETE
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
    @DELETE
    public Response<Boolean> deleteObservation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId);
	solrClinicalNotesService.deleteObservations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
    @DELETE
    public Response<Boolean> deleteInvestigation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId);

	solrClinicalNotesService.deleteInvestigations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
    @DELETE
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
    @DELETE
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
    @DELETE
    public Response<Boolean> deleteDiagram(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId);

	solrClinicalNotesService.deleteDiagrams(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
    @GET
    public Response<Object> getClinicalItems(@PathParam("type") String type, @PathParam("range") String range,
    		@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId, 
	    @QueryParam(value = "updatedTime") String updatedTime, @QueryParam(value = "discarded") Boolean discarded) {
    	
    	if (DPDoctorUtils.anyStringEmpty(type, range)) {
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type or Range Cannot Be Empty");
    	}
    	List<Object> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded != null ? discarded:true);
    	Response<Object> response = new  Response<Object>();
    	response.setDataList(clinicalItems);
    	return  response;
    }

}
