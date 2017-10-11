package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.response.LabSearchResponse;
import com.dpdocter.services.SearchService;
import com.dpdocter.webservices.PathProxy.SearchUrls;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SEARCH_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SEARCH_BASE_URL, description = "Endpoint for search apis")
public class SearchApi {

	@Autowired
	private SearchService searchService;

	@Path(value = PathProxy.SearchUrls.SEARCH_LABS_BY_TEST)
	@GET
	@ApiOperation(value = PathProxy.SearchUrls.SEARCH_LABS_BY_TEST, notes = SearchUrls.SEARCH_LABS_BY_TEST)
	public Response<LabSearchResponse> searchLabsByTest(@QueryParam("city") String city,
			@QueryParam("location") String location, @QueryParam(value = "latitude") String latitude,
			@QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm, 
			@MatrixParam(value = "test") List<String> testNames) {

		List<LabSearchResponse> labSearchResponses = searchService.searchLabsByTest(city, location, latitude, longitude, searchTerm, testNames);

		Response<LabSearchResponse> response = new Response<LabSearchResponse>();
		response.setDataList(labSearchResponses);
		return response;
	}
}
