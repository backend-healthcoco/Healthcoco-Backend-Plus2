package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;
import com.dpdocter.services.FreeQuestionAnswerService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.FREE_QUE_ANS_BASE_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.FREE_QUE_ANS_BASE_URL, description = "")
public class FreeQuestionAnswerAPI {
	private static Logger logger = LogManager.getLogger(FreeQuestionAnswerAPI.class.getName());
	@Autowired
	private FreeQuestionAnswerService freeQuetionAnswerService;

	@PostMapping(value = PathProxy.FreeQueAnsUrls.ADD_ANSWER)
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.ADD_ANSWER, notes = PathProxy.FreeQueAnsUrls.ADD_ANSWER)
	@ResponseBody
	public Response<FreeAnswerResponse> addFreeQuestion(@RequestBody FreeAnswerRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		FreeAnswerResponse freeQuestionResponse = freeQuetionAnswerService.addFreeAnswer(request);
		Response<FreeAnswerResponse> response = new Response<FreeAnswerResponse>();
		response.setData(freeQuestionResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS)
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS, notes = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS)
	@ResponseBody
	public Response<FreeQuestionResponse> getFreeQuestionList(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm",defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") long updatedTime) {
		Response<FreeQuestionResponse> response = new Response<FreeQuestionResponse>();
		Integer count = freeQuetionAnswerService.countFreeQuestion(discarded);
		response.setDataList(freeQuetionAnswerService.getFreeQuestionList(size, page, searchTerm, discarded, doctorId,
				updatedTime));
		response.setCount(count);
		return response;
	}
	
	@GetMapping(value = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS)
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS, notes = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS)
	@ResponseBody
	public Response<FreeQuestionResponse> getAnsweredQuestionList(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm",defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") long updatedTime) {
		Response<FreeQuestionResponse> response = new Response<FreeQuestionResponse>();
		Integer count = freeQuetionAnswerService.countFreeQuestion(discarded);
		response.setDataList(freeQuetionAnswerService.getAnsweredQuestionList(size, page, searchTerm, discarded, doctorId,
				updatedTime));
		response.setCount(count);
		return response;
	}
}
