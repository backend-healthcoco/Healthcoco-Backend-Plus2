package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OrthoEditProgressDatesRequest;
import com.dpdocter.request.OrthoEditRequest;
import com.dpdocter.response.OrthoProgressResponse;
import com.dpdocter.response.OrthoResponse;
import com.dpdocter.services.OrthoService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ORTHO_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ORTHO_BASE_URL, description = "Endpoint for Ortho")
public class OrthoAPI {
	private static Logger logger = Logger.getLogger(OrthoAPI.class.getName());

	@Autowired
	private OrthoService orthoService;

	@Path(value = PathProxy.OrthoUrls.ADD_EDIT_PLANNING_DETAILS)
	@POST
	@ApiOperation(value = PathProxy.OrthoUrls.ADD_EDIT_PLANNING_DETAILS, notes = PathProxy.OrthoUrls.ADD_EDIT_PLANNING_DETAILS)
	public Response<OrthoResponse> editOrthoPlanningDetails(OrthoEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		OrthoResponse orthoResponse = orthoService.editOrthoPlanningDetails(request);

		Response<OrthoResponse> response = new Response<OrthoResponse>();
		response.setData(orthoResponse);
		return response;
	}

	@Path(value = PathProxy.OrthoUrls.DELETE_PLANNING_DETAILS)
	@DELETE
	@ApiOperation(value = PathProxy.OrthoUrls.DELETE_PLANNING_DETAILS, notes = PathProxy.OrthoUrls.DELETE_PLANNING_DETAILS)
	public Response<Boolean> deleteOrthoPlanningDetails(@PathParam(value = "id") String id,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

		Boolean clinicalNotes = orthoService.deleteOrthoPlanningDetails(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(clinicalNotes);
		return response;
	}

	@Path(value = PathProxy.OrthoUrls.GET_PLANNING_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.OrthoUrls.GET_PLANNING_DETAILS, notes = PathProxy.OrthoUrls.GET_PLANNING_DETAILS)
	public Response<OrthoResponse> getOrthoPlanningDetails(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
		List<OrthoResponse> clinicalNotes = orthoService.getOrthoPlanningDetails(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime, discarded, false);
		Response<OrthoResponse> response = new Response<OrthoResponse>();
		response.setDataList(clinicalNotes);
		return response;
	}

	@Path(value = PathProxy.OrthoUrls.GET_PROGRESS_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.OrthoUrls.GET_PROGRESS_DETAILS, notes = PathProxy.OrthoUrls.GET_PROGRESS_DETAILS)
	public Response<OrthoProgressResponse> getOrthoProgressById(@PathParam(value = "planId") String planId) {
		OrthoProgressResponse clinicalNotes = orthoService.getOrthoProgressById(planId);
		Response<OrthoProgressResponse> response = new Response<OrthoProgressResponse>();
		response.setData(clinicalNotes);
		return response;
	}

	@Path(value = PathProxy.OrthoUrls.EDIT_PROGRESS_DETAILS_CHANGE_DATES)
	@PUT
	@ApiOperation(value = PathProxy.OrthoUrls.EDIT_PROGRESS_DETAILS_CHANGE_DATES, notes = PathProxy.OrthoUrls.EDIT_PROGRESS_DETAILS_CHANGE_DATES)
	public Response<Boolean> editOrthoProgressDetailsDates(OrthoEditProgressDatesRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getProgressId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean orthoResponse = orthoService.editOrthoProgressDetailsDates(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(orthoResponse);
		return response;
	}
}
