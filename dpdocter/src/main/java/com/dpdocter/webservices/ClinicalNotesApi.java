package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.PatientVisitService;
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

    private static Logger logger = Logger.getLogger(ClinicalNotesApi.class.getName());

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;

    @Autowired
    private PatientVisitService patientTrackService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
    @POST
    public Response<ClinicalNotes> addNotes(ClinicalNotesAddRequest request) {
	ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request);

	// patient track
	if (clinicalNotes != null) {
	    String visitId = patientTrackService.addRecord(clinicalNotes, VisitedFor.CLINICAL_NOTES, request.getVisitId());
	    clinicalNotes.setVisitId(visitId);
	}

	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
    @PUT
    public Response<ClinicalNotes> editNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, ClinicalNotesEditRequest request) {
	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId) || request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	request.setId(clinicalNotesId);
	ClinicalNotes clinicalNotes = clinicalNotesService.editNotes(request);

	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
    @DELETE
    public Response<Boolean> deleteNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteNote(clinicalNotesId, discarded);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
    @GET
    public Response<ClinicalNotes> getNotesById(@PathParam(value = "clinicalNotesId") String clinicalNotesId) {
	ClinicalNotes clinicalNotes = clinicalNotesService.getNotesById(clinicalNotesId);
	if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
	    clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
	}
	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @GET
    public Response<ClinicalNotes> getNotes(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @QueryParam(value = "patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
	    @DefaultValue("false") @QueryParam(value = "isOTPVerified") Boolean isOTPVerified, @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
	return getAllNotes(page, size, doctorId, locationId, hospitalId, patientId, updatedTime, isOTPVerified, discarded);
    }

    private Response<ClinicalNotes> getAllNotes(int page, int size, String doctorId, String locationId, String hospitalId, String patientId,
	    String updatedTime, Boolean isOTPVerified, Boolean discarded) {
	List<ClinicalNotes> clinicalNotes = null;

	    if (isOTPVerified) {
		clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithVerifiedOTP(page, size, patientId, updatedTime, discarded);
	    } else {
		clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithoutVerifiedOTP(page, size, patientId, doctorId, locationId, hospitalId,
			updatedTime, discarded);
	    }
	if (clinicalNotes != null && !clinicalNotes.isEmpty()) {
	    for (ClinicalNotes clinicalNote : clinicalNotes) {
		if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
		    clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
		}
	    }
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
	if (diagram.getDiagramUrl() != null) {
	    diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
	}
	Response<Diagram> response = new Response<Diagram>();
	response.setData(diagram);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
    @DELETE
    public Response<Boolean> deleteComplaint(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId, discarded);
	// Delete complaint in solr index.
	// solrClinicalNotesService.deleteComplaints(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
    @DELETE
    public Response<Boolean> deleteObservation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId, discarded);
	// solrClinicalNotesService.deleteObservations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
    @DELETE
    public Response<Boolean> deleteInvestigation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId, discarded);

	// solrClinicalNotesService.deleteInvestigations(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
    @DELETE
    public Response<Boolean> deleteDiagnosis(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId, discarded);

	// delete diagnosis in solr index.
	// solrClinicalNotesService.deleteDiagnoses(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
    @DELETE
    public Response<Boolean> deleteNote(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId, discarded);

	// delete notes in solr index.
	// solrClinicalNotesService.deleteNotes(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
    @DELETE
    public Response<Boolean> deleteDiagram(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);

	// solrClinicalNotesService.deleteDiagrams(id);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
    @GET
    public Response<Object> getClinicalItems(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(type, range)) {
	    logger.warn("Invalid Input. Type or Range Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type or Range Cannot Be Empty");
	}
	List<Object> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded != null ? discarded : true);
	if (clinicalItems != null && !clinicalItems.isEmpty() && ClinicalItems.DIAGRAMS.getType().equalsIgnoreCase(type)) {
	    for (Object clinicalItem : clinicalItems) {
		((DiagramsCollection) clinicalItem).setDiagramUrl(getFinalImageURL(((DiagramsCollection) clinicalItem).getDiagramUrl()));
	    }
	}
	Response<Object> response = new Response<Object>();
	response.setDataList(clinicalItems);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES)
    @GET
    public Response<Boolean> emailClinicalNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "emailAddress") String emailAddress) {

	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress)) {
	    logger.warn("Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput,
		    "Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
	}
	clinicalNotesService.emailClinicalNotes(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
	for (Diagram diagram : diagrams) {
	    if (diagram.getDiagramUrl() != null) {
		diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
	    }
	}
	return diagrams;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }

}
