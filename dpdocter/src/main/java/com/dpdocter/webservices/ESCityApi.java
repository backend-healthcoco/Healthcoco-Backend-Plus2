package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.City;
import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
import com.dpdocter.elasticsearch.services.ESCityService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_CITY_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_CITY_BASE_URL, description = "Endpoint for solr city")
public class ESCityApi {

	@Autowired
	private ESCityService esCityService;

	
	@GetMapping(value = PathProxy.SolrCityUrls.SEARCH_LOCATION)
	@ApiOperation(value = PathProxy.SolrCityUrls.SEARCH_LOCATION, notes = PathProxy.SolrCityUrls.SEARCH_LOCATION)
	public Response<ESCityLandmarkLocalityResponse> searchLocation(@RequestParam(value = "searchTerm") String searchTerm,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude) {

		List<ESCityLandmarkLocalityResponse> searchResonse = esCityService.searchCityLandmarkLocality(searchTerm,
				latitude, longitude);
		Response<ESCityLandmarkLocalityResponse> response = new Response<ESCityLandmarkLocalityResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB)
	@ApiOperation(value = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB, notes = PathProxy.SolrCityUrls.SEARCH_LOCATION_WEB)
	public Response<ESCityLandmarkLocalityResponse> searchLocationForWeb(
			@RequestParam(value = "searchTerm") String searchTerm, @RequestParam(value = "latitude") String latitude,
			@RequestParam(value = "longitude") String longitude) {

		List<ESCityLandmarkLocalityResponse> searchResonse = esCityService.searchCityLandmarkLocalityForWeb(searchTerm,
				latitude, longitude);
		Response<ESCityLandmarkLocalityResponse> response = new Response<ESCityLandmarkLocalityResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping
	@ApiOperation(value = "SEARCH_CITY", notes = "SEARCH_CITY")
	public Response<City> searchCity(@RequestParam(value = "searchTerm") String searchTerm,
			@RequestParam(value = "isActivated") Boolean isActivated) {
		List<City> searchResonse = esCityService.searchCity(searchTerm, isActivated);
		Response<City> response = new Response<City>();
		response.setDataList(searchResonse);
		return response;
	}
}
