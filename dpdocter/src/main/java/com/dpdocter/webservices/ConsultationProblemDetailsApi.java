package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConsultationProblemDetailsRequest;
import com.dpdocter.services.ConsultationProblemDetailsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL, description = "Endpoint for consultationProblemDetails")
public class ConsultationProblemDetailsApi {
	
	private static Logger logger = LogManager.getLogger(ConsultationProblemDetailsApi.class.getName());
	
	@Autowired
	private ConsultationProblemDetailsService consultationProblemDetailsService;

	@PostMapping(value = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS)
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
	
	
	@GetMapping(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	@ApiOperation(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS, notes = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	public Response<ConsultationProblemDetails> getConsultationDetails( @RequestParam(value ="problemDetailsId") String problemDetailsId) {
		//Integer count = consultationProblemDetailsService.countConsultationProblemDetails(discarded, searchTerm);
		Response<ConsultationProblemDetails> response = new Response<ConsultationProblemDetails>();
		
			response.setData(consultationProblemDetailsService.getProblemDetails(problemDetailsId));
		
		return response;
	}
	

}
