package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.City;
import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
import com.dpdocter.elasticsearch.services.ESCityService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_CITY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CITY_BASE_URL, description = "Endpoint for solr city")
public class ESCityApi {

	@Autowired
	private ESCityService esCityService;

	@Path(value = PathProxy.SolrCityUrls.SEARCH_LOCATION)
	@GET
	@ApiOperation(value = PathProxy.SolrCityUrls.SEARCH_LOCATION, notes = PathProxy.SolrCityUrls.SEARCH_LOCATION)
	public Response<ESCityLandmarkLocalityResponse> searchLocation(@QueryParam(value = "searchTerm") String searchTerm,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude) {

		List<ESCityLandmarkLocalityResponse> searchResonse = esCityService.searchCityLandmarkLocality(searchTerm,
				latitude, longitude);
		Response<ESCityLandmarkLocalityResponse> response = new Response<ESCityLandmarkLocalityResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB)
	@GET
	@ApiOperation(value = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB, notes = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB)
	public Response<ESCityLandmarkLocalityResponse> searchLocationForWeb(
			@QueryParam(value = "searchTerm") String searchTerm, @QueryParam(value = "latitude") String latitude,
			@QueryParam(value = "longitude") String longitude) {

		List<ESCityLandmarkLocalityResponse> searchResonse = esCityService.searchCityLandmarkLocalityForWeb(searchTerm,
				latitude, longitude);
		Response<ESCityLandmarkLocalityResponse> response = new Response<ESCityLandmarkLocalityResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	@GET
	@ApiOperation(value = "SEARCH_CITY", notes = "SEARCH_CITY")
	public Response<City> searchCity(@QueryParam(value = "searchTerm") String searchTerm,
			@QueryParam(value = "isActivated") Boolean isActivated) {
		List<City> searchResonse = esCityService.searchCity(searchTerm, isActivated);
		Response<City> response = new Response<City>();
		response.setDataList(searchResonse);
		return response;
	}
}
