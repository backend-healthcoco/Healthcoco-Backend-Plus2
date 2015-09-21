package com.dpdocter.solr.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_PRESCRIPTION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrPrescriptionApi {

    private static Logger logger = Logger.getLogger(SolrPrescriptionApi.class.getName());

    @Autowired
    private SolrPrescriptionService solrPrescriptionService;

    /*
     * @Path(value = PathProxy.SolrPrescriptionUrls.ADD_DRUG)
     * 
     * @POST public Response<Boolean> addDrug(SolrDrug request) { if (request ==
     * null) { throw new BusinessException(ServiceError.InvalidInput,
     * "Invalid Input"); } boolean addDrugResponse =
     * solrPrescriptionService.addDrug(request); Response<Boolean> response =
     * new Response<Boolean>(); response.setData(addDrugResponse); return
     * response; }
     * 
     * @Path(value = PathProxy.SolrPrescriptionUrls.EDIT_DRUG)
     * 
     * @POST public Response<Boolean> editDrug(SolrDrug request) { if (request
     * == null) { throw new BusinessException(ServiceError.InvalidInput,
     * "Invalid Input"); } boolean editDrugResponse =
     * solrPrescriptionService.editDrug(request); Response<Boolean> response =
     * new Response<Boolean>(); response.setData(editDrugResponse); return
     * response; }
     * 
     * @Path(value = PathProxy.SolrPrescriptionUrls.DELETE_DRUG)
     * 
     * @GET public Response<Boolean> deleteDrug(@PathParam(value = "id") String
     * id) { if (DPDoctorUtils.anyStringEmpty(id)) { throw new
     * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } boolean
     * deleteDrugResponse = solrPrescriptionService.deleteDrug(id);
     * Response<Boolean> response = new Response<Boolean>();
     * response.setData(deleteDrugResponse); return response; }
     */

    @Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
    @GET
    public Response<SolrDrugDocument> searchDrug(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrDrugDocument> complaints = solrPrescriptionService.searchDrug(searchTerm);
	Response<SolrDrugDocument> response = new Response<SolrDrugDocument>();
	response.setDataList(complaints);
	return response;
    }
}
