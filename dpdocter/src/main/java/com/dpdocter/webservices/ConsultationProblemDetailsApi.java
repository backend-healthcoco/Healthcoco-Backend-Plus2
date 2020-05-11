package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;


import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConsultationProblemDetailsRequest;
import com.dpdocter.services.ConsultationProblemDetailsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL, description = "Endpoint for consultationProblemDetails")
public class ConsultationProblemDetailsApi {
	
	private static Logger logger = LogManager.getLogger(ConsultationProblemDetailsApi.class.getName());
	
	private ConsultationProblemDetailsService consultationProblemDetailsService;

	@POST
	@Path(value = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS)
	@ApiOperation(value = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS, notes = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS)
	public Response<ConsultationProblemDetails> addEditConsultationProblemDetails(@RequestBody ConsultationProblemDetailsRequest request)
	{
		
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	} 
	Response<ConsultationProblemDetails> response = new Response<ConsultationProblemDetails>();
	response.setData(consultationProblemDetailsService.addEditProblemDetails(request));
	return response;
	}
	
	@Path(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS, notes = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	public Response<ConsultationProblemDetails> getConsultationDetails(@DefaultValue("0") @QueryParam(value ="size") int size, 
			@DefaultValue("0") @QueryParam( value ="page") int page,
			@DefaultValue("false") @QueryParam( value ="discarded" ) Boolean discarded, 
			@QueryParam(value ="searchTerm") String searchTerm) {
		Integer count = consultationProblemDetailsService.countConsultationProblemDetails(discarded, searchTerm);
		Response<ConsultationProblemDetails> response = new Response<ConsultationProblemDetails>();
		
			response.setDataList(consultationProblemDetailsService.getProblemDetails(page, size, searchTerm,discarded));
		response.setCount(count);
		return response;
	}
	

}
