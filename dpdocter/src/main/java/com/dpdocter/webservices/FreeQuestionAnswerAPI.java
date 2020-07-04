package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;
import com.dpdocter.services.FreeQuestionAnswerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.FREE_QUE_ANS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FREE_QUE_ANS_BASE_URL, description = "")
public class FreeQuestionAnswerAPI {
	private static Logger logger = Logger.getLogger(FreeQuestionAnswerAPI.class.getName());
	@Autowired
	private FreeQuestionAnswerService freeQuetionAnswerService;

	@Path(value = PathProxy.FreeQueAnsUrls.ADD_ANSWER)
	@POST
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.ADD_ANSWER, notes = PathProxy.FreeQueAnsUrls.ADD_ANSWER)
	public Response<FreeAnswerResponse> addFreeAnswer(@RequestBody FreeAnswerRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		FreeAnswerResponse freeQuestionResponse = freeQuetionAnswerService.addFreeAnswer(request);
		Response<FreeAnswerResponse> response = new Response<FreeAnswerResponse>();
		response.setData(freeQuestionResponse);
		return response;
	}

	@Path(value = PathProxy.FreeQueAnsUrls.ADD_VIEWS)
	@POST
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.ADD_VIEWS, notes = PathProxy.FreeQueAnsUrls.ADD_VIEWS)
	public Response<Boolean> addQueView(@PathVariable("questionId") String questionId) {
		if (!DPDoctorUtils.anyStringEmpty(questionId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(freeQuetionAnswerService.addQueView(questionId));
		return response;
	}

	@Path(value = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS)
	@GET
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS, notes = PathProxy.FreeQueAnsUrls.GET_UNANSWERED_QUESTIONS)
	public Response<FreeQuestionResponse> getFreeQuestionList(@PathParam("doctorId") String doctorId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("updatedTime") long updatedTime) {
		Response<FreeQuestionResponse> response = new Response<FreeQuestionResponse>();
		Integer count = freeQuetionAnswerService.countFreeQuestion(discarded);
		response.setDataList(
				freeQuetionAnswerService.getFreeQuestionList(size, page, searchTerm, discarded, doctorId, updatedTime));
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS)
	@GET
	@ApiOperation(value = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS, notes = PathProxy.FreeQueAnsUrls.GET_ANSWERED_QUESTIONS)
	public Response<FreeQuestionResponse> getAnsweredQuestionList(@PathParam("doctorId") String doctorId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("updatedTime") long updatedTime) {
		Response<FreeQuestionResponse> response = new Response<FreeQuestionResponse>();
		Integer count = freeQuetionAnswerService.countFreeQuestion(discarded);
		response.setDataList(freeQuetionAnswerService.getAnsweredQuestionList(size, page, searchTerm, discarded,
				doctorId, updatedTime));
		response.setCount(count);
		return response;
	}
}
