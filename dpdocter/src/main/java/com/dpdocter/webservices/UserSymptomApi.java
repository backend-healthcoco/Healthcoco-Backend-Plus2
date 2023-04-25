package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.UserSymptom;
import com.dpdocter.services.UserSymptomService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.SYMPTOM_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SYMPTOM_BASE_URL, description = "Endpoints for user Symptoms")

public class UserSymptomApi {

	private Logger logger = LogManager.getLogger(UserSymptomApi.class);
	@Autowired
	private UserSymptomService userSymptomServices;

	@Path(value = PathProxy.SymptomUrls.GET_USER_SYMPTOM)
	@ApiOperation(value = PathProxy.SymptomUrls.GET_USER_SYMPTOM, notes = PathProxy.SymptomUrls.GET_USER_SYMPTOM)
	@GET
	public Response<UserSymptom> getUserSymptoms(@DefaultValue("0") @QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("false") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		Integer count = userSymptomServices.countUserSymptom(discarded, searchTerm);
		Response<UserSymptom> response = new Response<UserSymptom>();

		response.setDataList(userSymptomServices.getUserSymptoms(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

}
