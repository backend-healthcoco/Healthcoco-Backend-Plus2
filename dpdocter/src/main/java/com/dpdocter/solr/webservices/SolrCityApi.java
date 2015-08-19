package com.dpdocter.solr.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.services.SolrCityService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_CITY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrCityApi {

	@Autowired
	private SolrCityService solrCityService;
	
	@Path(value = PathProxy.SolrCityUrls.SEARCH_CITY)
    @GET
    public Response<SolrCityDocument> searchCity(@PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrCityDocument> complaints = solrCityService.searchCity(searchTerm);
	Response<SolrCityDocument> response = new Response<SolrCityDocument>();
	response.setDataList(complaints);
	return response;
    }

	@Path(value = PathProxy.SolrCityUrls.SEARCH_LANDMARK_LOCALITY)
    @GET
    public Response<SolrLocalityLandmarkDocument> searchLandmarkLocality(@PathParam(value = "cityId") String cityId, @PathParam(value = "searchTerm") String searchTerm,
    		@QueryParam(value = "type") String type
    		) {
	if (DPDoctorUtils.anyStringEmpty(cityId) || DPDoctorUtils.anyStringEmpty(searchTerm) ) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<SolrLocalityLandmarkDocument> complaints = solrCityService.searchLandmarkLocality(cityId, type, searchTerm);
	Response<SolrLocalityLandmarkDocument> response = new Response<SolrLocalityLandmarkDocument>();
	response.setDataList(complaints);
	return response;
    }
}
