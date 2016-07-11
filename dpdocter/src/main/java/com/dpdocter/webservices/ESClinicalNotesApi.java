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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_CLINICAL_NOTES_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CLINICAL_NOTES_BASEURL, description = "Endpoint for es clinical notes")
public class ESClinicalNotesApi {

	private static Logger logger = Logger.getLogger(ESClinicalNotesApi.class.getName());
	
    @Value(value = "${image.path}")
    private String imagePath;

    @Autowired
    private ESClinicalNotesService esClinicalNotesService;
    
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    public Response<ESComplaintsDocument> searchComplaints(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESComplaintsDocument> complaints = esClinicalNotesService.searchComplaints(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
	response.setDataList(complaints);
	return response;
    }
    
    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    public Response<ESDiagnosesDocument> searchDiagnoses(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESDiagnosesDocument> diagnoses = esClinicalNotesService.searchDiagnoses(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
	response.setDataList(diagnoses);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    public Response<ESNotesDocument> searchNotes(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESNotesDocument> notes = esClinicalNotesService.searchNotes(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<ESNotesDocument> response = new Response<ESNotesDocument>();
	response.setDataList(notes);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    public Response<ESDiagramsDocument> searchDiagrams(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESDiagramsDocument> diagrams = esClinicalNotesService.searchDiagrams(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

//    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
//    @GET
//    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
//    public Response<ESDiagramsDocument> searchDiagramsBySpeciality(@PathParam(value = "searchTerm") String searchTerm) {
//
//	List<ESDiagramsDocument> diagrams = esClinicalNotesService.searchDiagramsBySpeciality(searchTerm);
//	diagrams = getFinalDiagrams(diagrams);
//	Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
//	response.setDataList(diagrams);
//	return response;
//    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    public Response<ESInvestigationsDocument> searchInvestigations(@PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESInvestigationsDocument> investigations = esClinicalNotesService.searchInvestigations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
	Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
	response.setDataList(investigations);
	return response;
    }

    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    @GET
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    public Response<ESObservationsDocument> searchObservations(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESObservationsDocument> observations = esClinicalNotesService.searchObservations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
	Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
	response.setDataList(observations);
	return response;
    }

    private List<ESDiagramsDocument> getFinalDiagrams(List<ESDiagramsDocument> diagrams) {
	for (ESDiagramsDocument diagram : diagrams) {
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
