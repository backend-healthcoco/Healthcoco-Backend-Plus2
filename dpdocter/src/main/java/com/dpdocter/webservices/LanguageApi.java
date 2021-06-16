package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Language;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LanguageService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Api(value = PathProxy.LANGUAGE_BASE_URL, description = "Endpoint for Language Api")
@Path(value=PathProxy.LANGUAGE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LanguageApi {
	
	private static Logger logger = LogManager.getLogger(LanguageApi.class.getName());
	 
	 @Autowired
	 private LanguageService languageService;
	 
	 	@GET
	 	@Path(value=PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID)
		@ApiOperation(value = PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID, notes = PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID)
		public Response<Language> getLanguage(@PathParam("id")String id)
		{
			
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(languageService.getLanguage(id));
		return response;
		}
		
	 	@GET
		@Path(value = PathProxy.LanguageUrls.GET_LANGUAGES)
		@ApiOperation(value = PathProxy.LanguageUrls.GET_LANGUAGES, notes = PathProxy.LanguageUrls.GET_LANGUAGES)
		public Response<Language> getLanguages(@DefaultValue("0")@QueryParam(value ="size") int size, 
				@DefaultValue("0")	@QueryParam( value ="page") int page,
				@QueryParam(value ="discarded") Boolean discarded, 
				@QueryParam(value ="searchTerm") String searchTerm) {
			Integer count = languageService.countLanguage(discarded, searchTerm);
			Response<Language> response = new Response<Language>();
			
				response.setDataList(languageService.getLanguages(size, page, discarded, searchTerm));
			response.setCount(count);
			return response;
		}

}
