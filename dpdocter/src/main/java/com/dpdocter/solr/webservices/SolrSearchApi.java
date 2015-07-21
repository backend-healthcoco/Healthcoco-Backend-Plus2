/*package com.dpdocter.solr.webservices;

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
import com.dpdocter.solr.document.DoctorCoreDemoDocument;
import com.dpdocter.solr.document.SearchDoctorSolrDocument;
import com.dpdocter.solr.services.DoctorSearchSolrService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrSearchApi {

	@Autowired
	private DoctorSearchSolrService doctorSearchSolrService;

	@Path(value = PathProxy.SolrTemp.ADD)
	@POST
	public Response<Boolean> add(SearchDoctorSolrDocument request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		doctorSearchSolrService.addToIndex(request);
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.SolrTemp.ADD1)
	@POST
	public Response<Boolean> add(DoctorCoreDemoDocument request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		doctorSearchSolrService.addToIndex(request);
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.SolrTemp.SEARCH)
	@GET
	public Response<SearchDoctorSolrDocument> getExistingPatients(@PathParam("text") String text) {
		if (text == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}

		Response<SearchDoctorSolrDocument> response = new Response<SearchDoctorSolrDocument>();

		List<SearchDoctorSolrDocument> searchDoctorSolrDocuments = doctorSearchSolrService.findByQueryName(text);
		response.setDataList(searchDoctorSolrDocuments);
		return response;
	}

	@Path(value = PathProxy.SolrTemp.SEARCH1)
	@GET
	public Response<DoctorCoreDemoDocument> getExistingPatients1(@PathParam("text") String text) {
		if (text == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}

		Response<DoctorCoreDemoDocument> response = new Response<DoctorCoreDemoDocument>();

		List<DoctorCoreDemoDocument> doctorCoreDemoDocuments = doctorSearchSolrService.findByQueryName1(text);
		response.setDataList(doctorCoreDemoDocuments);
		return response;
	}

}
*/