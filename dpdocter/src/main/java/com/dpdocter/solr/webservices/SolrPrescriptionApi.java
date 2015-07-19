package com.dpdocter.solr.webservices;

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

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrDrug;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrPrescriptionApi {
	@Autowired
	private SolrPrescriptionService solrPrescriptionService;

	@Path(value = PathProxy.SolrPrescriptionUrls.ADD_DRUG)
	@POST
	public Response<Boolean> addDrug(SolrDrug request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addDrugResponse = solrPrescriptionService.addDrug(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addDrugResponse);
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.EDIT_DRUG)
	@POST
	public Response<Boolean> editDrug(SolrDrug request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editDrugResponse = solrPrescriptionService.editDrug(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editDrugResponse);
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.DELETE_DRUG)
	@GET
	public Response<Boolean> deleteDrug(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteDrugResponse = solrPrescriptionService.deleteDrug(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteDrugResponse);
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	@GET
	public Response<SolrDrug> searchDrug(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrDrug> complaints = solrPrescriptionService.searchDrug(searchTerm);
		Response<SolrDrug> response = new Response<SolrDrug>();
		response.setDataList(complaints);
		return response;
	}
}
