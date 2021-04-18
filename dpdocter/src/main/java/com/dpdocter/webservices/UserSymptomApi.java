package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.UserSymptom;
import com.dpdocter.services.UserSymptomService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.SYMPTOM_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SYMPTOM_BASE_URL, description = "Endpoints for user Symptoms")

public class UserSymptomApi {

		private Logger logger = LogManager.getLogger(UserSymptomApi.class);	
		@Autowired
		private UserSymptomService userSymptomServices;
		
		
		@ApiOperation(value = PathProxy.SymptomUrls.GET_USER_SYMPTOM, notes = PathProxy.SymptomUrls.GET_USER_SYMPTOM)
		@GetMapping(value = PathProxy.SymptomUrls.GET_USER_SYMPTOM)
		public Response<UserSymptom> getUserSymptoms(@DefaultValue("0") @RequestParam(value ="size") int size, 
				@DefaultValue("0") @RequestParam( value ="page") int page,
				@DefaultValue("false") @RequestParam( value ="discarded" ) Boolean discarded, 
				@RequestParam(value ="searchTerm") String searchTerm) {
			Integer count = userSymptomServices.countUserSymptom(discarded, searchTerm);
			Response<UserSymptom> response = new Response<UserSymptom>();
			
			response.setDataList(userSymptomServices.getUserSymptoms(size, page, discarded, searchTerm));
			response.setCount(count);
			return response;
		}
		
		

	}


