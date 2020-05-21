package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FitnessAssessment;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.FitnessAssessmentService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.FITNESS_ASSESSMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FITNESS_ASSESSMENT_BASE_URL, description = "Endpoint for fitness Assessment")
public class FitnessAssessmentAPI {
	private static Logger logger = Logger.getLogger(FitnessAssessmentAPI.class.getName());
	@Autowired
	private FitnessAssessmentService fitnessAssessmentService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Path(value = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT)
	@DELETE
	@ApiOperation(value = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT)
	public Response<FitnessAssessment> deleteFitness(@PathParam("fitnessId") String fitnessId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(fitnessId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		response.setData(fitnessAssessmentService.discardFitnessAssessment(fitnessId, discarded));
		return response;
	}

	@Path(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT)
	@GET
	@ApiOperation(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT)
	public Response<FitnessAssessment> getFitnessAssessment(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("patientId") String patientId,@QueryParam("updatedTime") long updatedTime) {

		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		response.setDataList(fitnessAssessmentService.getFitnessAssessmentList(size, page, discarded, searchTerm,
				doctorId, locationId, hospitalId, patientId,updatedTime));
		return response;
	}

	@Path(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID, notes = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID)
	public Response<FitnessAssessment> getFitnessAssessmentById(@PathParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		response.setData(fitnessAssessmentService.getFitnessAssessmentById(patientId));
		return response;
	}

	@Path(value = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT)
	@POST
	@ApiOperation(value = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT)
	public Response<FitnessAssessment> addEditFitnessAssessment(FitnessAssessment request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"name,doctorId,locationId or hospitalId should not be null or empty");

		}
		FitnessAssessment fitnessAssessment = fitnessAssessmentService.addEditFitnessAssessment(request);
		Response<FitnessAssessment> response = new Response<FitnessAssessment>();

		if (fitnessAssessment != null) {
			transnationalService.addResource(new ObjectId(request.getId()), Resource.FITNESS_ASSESMENT, true);

		}
		response.setData(fitnessAssessment);

		return response;
	}

	@Path(value = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT)
	@GET
	@ApiOperation(value = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT)
	public Response<String> downloadFitnessAssessment(@PathParam("fitnessId") String fitnessId) {
		if (DPDoctorUtils.anyStringEmpty(fitnessId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(fitnessAssessmentService.getFitnessAssessmentFile(fitnessId));
		return response;
	}
}
