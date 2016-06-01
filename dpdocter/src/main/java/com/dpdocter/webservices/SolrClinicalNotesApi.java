package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import com.dpdocter.webservices.PathProxy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_CLINICAL_NOTES_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CLINICAL_NOTES_BASEURL, description = "Endpoint for solr clinical notes")
public class SolrClinicalNotesApi {

    // private static Logger logger =
    // Logger.getLogger(SolrClinicalNotesApi.class.getName());

    @Value(value = "${image.path}")
    private String imagePath;

    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;
    
    @Autowired
    private SolrTemplate solrTemplate;

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    public Response<SolrComplaintsDocument> searchComplaints(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrComplaintsDocument> complaints = solrClinicalNotesService.searchComplaints(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<SolrComplaintsDocument> response = new Response<SolrComplaintsDocument>();
	response.setDataList(complaints);
	return response;
    }
    
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    public Response<SolrDiagnosesDocument> searchDiagnoses(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrDiagnosesDocument> diagnoses = solrClinicalNotesService.searchDiagnoses(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<SolrDiagnosesDocument> response = new Response<SolrDiagnosesDocument>();
	response.setDataList(diagnoses);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    public Response<SolrNotesDocument> searchNotes(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrNotesDocument> notes = solrClinicalNotesService.searchNotes(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<SolrNotesDocument> response = new Response<SolrNotesDocument>();
	response.setDataList(notes);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    public Response<SolrDiagramsDocument> searchDiagrams(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrDiagramsDocument> diagrams = solrClinicalNotesService.searchDiagrams(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<SolrDiagramsDocument> response = new Response<SolrDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
    public Response<SolrDiagramsDocument> searchDiagramsBySpeciality(@PathParam(value = "searchTerm") String searchTerm) {

	List<SolrDiagramsDocument> diagrams = solrClinicalNotesService.searchDiagramsBySpeciality(searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<SolrDiagramsDocument> response = new Response<SolrDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    public Response<SolrInvestigationsDocument> searchInvestigations(@PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrInvestigationsDocument> investigations = solrClinicalNotesService.searchInvestigations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
	Response<SolrInvestigationsDocument> response = new Response<SolrInvestigationsDocument>();
	response.setDataList(investigations);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    public Response<SolrObservationsDocument> searchObservations(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrObservationsDocument> observations = solrClinicalNotesService.searchObservations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
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
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;
    }
}
