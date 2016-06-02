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
import org.springframework.stereotype.Component;

import com.dpdocter.beans.LabTest;
import com.dpdocter.solr.document.SolrDiagnosticTestDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_PRESCRIPTION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class SolrPrescriptionApi {

    @Autowired
    private SolrPrescriptionService solrPrescriptionService;
    
    @Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
    @GET
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
    public Response<SolrDrugDocument> searchDrug(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrDrugDocument> complaints = solrPrescriptionService.searchDrug(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<SolrDrugDocument> response = new Response<SolrDrugDocument>();
	response.setDataList(complaints);
	return response;
    }

    @Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
    @GET
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
    public Response<LabTest> searchLabTest(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
	    @QueryParam(value = "searchTerm") String searchTerm) {

	List<LabTest> labTests = solrPrescriptionService.searchLabTest(range, page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	Response<LabTest> response = new Response<LabTest>();
	response.setDataList(labTests);
	return response;
    }

    @Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
    @GET
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
    public Response<SolrDiagnosticTestDocument> searchDiagnosticTest(@PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
	    @QueryParam(value = "searchTerm") String searchTerm) {

	List<SolrDiagnosticTestDocument> diagnosticTests = solrPrescriptionService.searchDiagnosticTest(range, page, size, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<SolrDiagnosticTestDocument> response = new Response<SolrDiagnosticTestDocument>();
	response.setDataList(diagnosticTests);
	return response;
    }

}
