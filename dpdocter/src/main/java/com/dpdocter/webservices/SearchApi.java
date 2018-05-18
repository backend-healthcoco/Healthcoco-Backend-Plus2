package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.services.SearchService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SEARCH_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_APPOINTMENT_BASE_URL, description = "Endpoint for search")
public class SearchApi {

	private static Logger logger = Logger.getLogger(SearchApi.class.getName());
	
	@Autowired
	private SearchService searchService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.SearchUrls.SEARCH_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.SearchUrls.SEARCH_DOCTORS, notes = PathProxy.SearchUrls.SEARCH_DOCTORS)
	public Response<SearchDoctorResponse> getDoctors(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom,
			@DefaultValue("false") @QueryParam("booking") Boolean booking,
			@DefaultValue("false") @QueryParam("calling") Boolean calling, @QueryParam("minFee") int minFee,
			@QueryParam("maxFee") int maxFee, @QueryParam("minTime") int minTime, @QueryParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @QueryParam("gender") String gender,
			@QueryParam("minExperience") int minExperience, @QueryParam("maxExperience") int maxExperience,
			@QueryParam("service") String service, @QueryParam("locality") String locality,
			@DefaultValue(value = "false") @QueryParam("otherArea") Boolean otherArea) {

		SearchDoctorResponse doctors = searchService.searchDoctors(page, size, city, location, latitude, longitude,
				speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender,
				minExperience, maxExperience, service, locality, otherArea);

		Response<SearchDoctorResponse> response = new Response<SearchDoctorResponse>();
		response.setData(doctors);
		return response;
	}
	
	@Path(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	@GET
	@ApiOperation(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY, notes = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	public Response<ResourcesCountResponse> getResourcesCountByCity(@PathParam("city") String city, @MatrixParam("type") List<String> type) {

		if(city == null) {
			logger.warn("Invalid Input");
		    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ResourcesCountResponse> resourcesCountResponses = searchService.getResourcesCountByCity(city, type);

		Response<ResourcesCountResponse> response = new Response<ResourcesCountResponse>();
		response.setDataList(resourcesCountResponses);
		return response;
	}
}
