package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
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
}
