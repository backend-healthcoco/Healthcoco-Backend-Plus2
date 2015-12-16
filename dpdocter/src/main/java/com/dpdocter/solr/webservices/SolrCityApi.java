package com.dpdocter.solr.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.solr.beans.SolrCityLandmarkLocalityResponse;
import com.dpdocter.solr.services.SolrCityService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_CITY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrCityApi {

    @Autowired
    private SolrCityService solrCityService;

    @Path(value = PathProxy.SolrCityUrls.SEARCH_LOCATION)
    @GET
    public Response<SolrCityLandmarkLocalityResponse> searchLocation(@QueryParam(value = "searchTerm") String searchTerm,
    		@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude) {
	
	List<SolrCityLandmarkLocalityResponse> searchResonse = solrCityService.searchCityLandmarkLocality(searchTerm, latitude, longitude);
	Response<SolrCityLandmarkLocalityResponse> response = new Response<SolrCityLandmarkLocalityResponse>();
	response.setDataList(searchResonse);
	return response;
    }

}
