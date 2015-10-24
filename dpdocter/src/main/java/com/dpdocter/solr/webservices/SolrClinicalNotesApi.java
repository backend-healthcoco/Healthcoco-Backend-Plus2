package com.dpdocter.solr.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_CLINICAL_NOTES_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrClinicalNotesApi {

    private static Logger logger = Logger.getLogger(SolrClinicalNotesApi.class.getName());

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_COMPLAINTS)
     * 
     * @POST public Response<Boolean> addComplaints(SolrComplaints request) { if
     * (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * addComplaintsResponse = solrClinicalNotesService.addComplaints(request);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(addComplaintsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_COMPLAINTS)
     * 
     * @POST public Response<Boolean> editComplaints(SolrComplaints request) {
     * if (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * editComplaintsResponse =
     * solrClinicalNotesService.editComplaints(request); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(editComplaintsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_COMPLAINTS)
     * 
     * @GET public Response<Boolean> deleteComplaints(@PathParam(value = "id")
     * String id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteComplaintsResponse = solrClinicalNotesService.deleteComplaints(id);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(deleteComplaintsResponse); return response; }
     */

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    @GET
    public Response<SolrComplaintsDocument> searchComplaints(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrComplaintsDocument> complaints = solrClinicalNotesService.searchComplaints(searchTerm);
	Response<SolrComplaintsDocument> response = new Response<SolrComplaintsDocument>();
	response.setDataList(complaints);
	return response;
    }

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_DIAGNOSES)
     * 
     * @POST public Response<Boolean> addDiagnoses(SolrDiagnoses request) { if
     * (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * addDiagnosesResponse = solrClinicalNotesService.addDiagnoses(request);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(addDiagnosesResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_DIAGNOSES)
     * 
     * @POST public Response<Boolean> editDiagnoses(SolrDiagnoses request) { if
     * (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * editDiagnosesResponse = solrClinicalNotesService.editDiagnoses(request);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(editDiagnosesResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_DIAGNOSES)
     * 
     * @GET public Response<Boolean> deleteDiagnoses(@PathParam(value = "id")
     * String id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteDiagnosesResponse = solrClinicalNotesService.deleteDiagnoses(id);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(deleteDiagnosesResponse); return response; }
     */

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    @GET
    public Response<SolrDiagnosesDocument> searchDiagnoses(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrDiagnosesDocument> diagnoses = solrClinicalNotesService.searchDiagnoses(searchTerm);
	Response<SolrDiagnosesDocument> response = new Response<SolrDiagnosesDocument>();
	response.setDataList(diagnoses);
	return response;
    }

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_NOTES)
     * 
     * @POST public Response<Boolean> addNotes(SolrNotes request) { if (request
     * == null) { throw new BusinessException(ServiceError.InvalidInput,
     * "Invalid Input"); } boolean addNotesResponse =
     * solrClinicalNotesService.addNotes(request); Response<Boolean> response =
     * new Response<Boolean>(); response.setData(addNotesResponse); return
     * response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_NOTES)
     * 
     * @POST public Response<Boolean> editNotes(SolrNotes request) { if (request
     * == null) { throw new BusinessException(ServiceError.InvalidInput,
     * "Invalid Input"); } boolean editNotesResponse =
     * solrClinicalNotesService.editNotes(request); Response<Boolean> response =
     * new Response<Boolean>(); response.setData(editNotesResponse); return
     * response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_NOTES)
     * 
     * @GET public Response<Boolean> deleteNotes(@PathParam(value = "id") String
     * id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteNotesResponse = solrClinicalNotesService.deleteNotes(id);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(deleteNotesResponse); return response; }
     */
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    @GET
    public Response<SolrNotesDocument> searchNotes(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrNotesDocument> notes = solrClinicalNotesService.searchNotes(searchTerm);
	Response<SolrNotesDocument> response = new Response<SolrNotesDocument>();
	response.setDataList(notes);
	return response;
    }

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_DIAGRAMS)
     * 
     * @POST public Response<Boolean> addDiagrams(SolrDiagrams request) { if
     * (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * addDiagramsResponse = solrClinicalNotesService.addDiagrams(request);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(addDiagramsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_DIAGRAMS)
     * 
     * @POST public Response<Boolean> editDiagrams(SolrDiagrams request) { if
     * (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * editDiagramsResponse = solrClinicalNotesService.editDiagrams(request);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(editDiagramsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_DIAGRAMS)
     * 
     * @GET public Response<Boolean> deleteDiagrams(@PathParam(value = "id")
     * String id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteDiagramsResponse = solrClinicalNotesService.deleteDiagrams(id);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(deleteDiagramsResponse); return response; }
     */
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    @GET
    public Response<SolrDiagramsDocument> searchDiagrams(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrDiagramsDocument> diagrams = solrClinicalNotesService.searchDiagrams(searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<SolrDiagramsDocument> response = new Response<SolrDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
    @GET
    public Response<SolrDiagramsDocument> searchDiagramsBySpeciality(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrDiagramsDocument> diagrams = solrClinicalNotesService.searchDiagramsBySpeciality(searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<SolrDiagramsDocument> response = new Response<SolrDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_INVESTIGATIONS)
     * 
     * @POST public Response<Boolean> addInvestigations(SolrInvestigations
     * request) { if (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * addInvestigationsResponse =
     * solrClinicalNotesService.addInvestigations(request); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(addInvestigationsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_INVESTIGATIONS)
     * 
     * @POST public Response<Boolean> editInvestigations(SolrInvestigations
     * request) { if (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * editInvestigationsResponse =
     * solrClinicalNotesService.editInvestigations(request); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(editInvestigationsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_INVESTIGATIONS)
     * 
     * @GET public Response<Boolean> deleteInvestigations(@PathParam(value =
     * "id") String id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteInvestigationsResponse =
     * solrClinicalNotesService.deleteInvestigations(id); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(deleteInvestigationsResponse); return response; }
     */
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    @GET
    public Response<SolrInvestigationsDocument> searchInvestigations(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrInvestigationsDocument> investigations = solrClinicalNotesService.searchInvestigations(searchTerm);
	Response<SolrInvestigationsDocument> response = new Response<SolrInvestigationsDocument>();
	response.setDataList(investigations);
	return response;
    }

    /*
     * @Path(value = PathProxy.SolrClinicalNotesUrls.ADD_INVESTIGATIONS)
     * 
     * @POST public Response<Boolean> addInvestigations(SolrObservations
     * request) { if (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * addObservationsResponse =
     * solrClinicalNotesService.addObservations(request); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(addObservationsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_OBSERVATIONS)
     * 
     * @POST public Response<Boolean> editObservations(SolrObservations request)
     * { if (request == null) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * editObservationsResponse =
     * solrClinicalNotesService.editObservations(request); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(editObservationsResponse); return response; }
     * 
     * @Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_OBSERVATIONS)
     * 
     * @GET public Response<Boolean> deleteObservations(@PathParam(value = "id")
     * String id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteObservationsResponse =
     * solrClinicalNotesService.deleteObservations(id); Response<Boolean>
     * response = new Response<Boolean>();
     * response.setData(deleteObservationsResponse); return response; }
     */
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    @GET
    public Response<SolrObservationsDocument> searchObservations(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrObservationsDocument> observations = solrClinicalNotesService.searchObservations(searchTerm);
	Response<SolrObservationsDocument> response = new Response<SolrObservationsDocument>();
	response.setDataList(observations);
	return response;
    }

    private List<SolrDiagramsDocument> getFinalDiagrams(List<SolrDiagramsDocument> diagrams) {
	for (SolrDiagramsDocument diagram : diagrams) {
	    if (diagram.getDiagramUrl() != null) {
		diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
	    }
	}
	return diagrams;
    }

    private String getFinalImageURL(String imageURL) {
	String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	return finalImageURL + imageURL;
    }

}
