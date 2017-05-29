package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.services.DischargeSummaryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DISCHARGE_SUMMARY_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DISCHARGE_SUMMARY_BASE_URL)
public class DischargeSummaryAPI {

	private Logger logger = Logger.getLogger(DischargeSummaryAPI.class);

	@Autowired
	DischargeSummaryService dischargeSummaryService;

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> addEditDischargeSummary(DischargeSummaryRequest request) {
		Response<DischargeSummaryResponse> response = null;
		DischargeSummaryResponse dischargeSummary = null;
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		dischargeSummary = dischargeSummaryService.addEditDischargeSummary(request);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummaryResponse>();
			response.setData(dischargeSummary);
		}

		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> getDischargeSummary(@QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		Response<DischargeSummaryResponse> response = null;
		List<DischargeSummaryResponse> dischargeSummaries = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		dischargeSummaries = dischargeSummaryService.getDischargeSummary(doctorId, locationId, hospitalId, patientId,
				page, size, updatedTime);
		response = new Response<DischargeSummaryResponse>();
		response.setDataList(dischargeSummaries);

		return response;

	}

	@Path(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> viewDischargeSummary(
			@PathParam("dischargeSummeryId") String dischargeSummeryId) {
		Response<DischargeSummaryResponse> response = null;
		DischargeSummaryResponse dischargeSummary = null;

		if (dischargeSummeryId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		// dischargeSummary = new DischargeSummary();
		dischargeSummary = dischargeSummaryService.viewDischargeSummary(dischargeSummeryId);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummaryResponse>();
			response.setData(dischargeSummary);

		}
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> deleteDischargeSummary(
			@PathParam(value = "dischargeSummeryId") String dischargeSummeryId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(dischargeSummeryId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		DischargeSummaryResponse dischargeSummaryResponse = dischargeSummaryService
				.deleteDischargeSummary(dischargeSummeryId, doctorId, hospitalId, locationId, discarded);
		Response<DischargeSummaryResponse> response = new Response<DischargeSummaryResponse>();
		response.setData(dischargeSummaryResponse);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY)
	public Response<String> downloadDischargeSummary(@PathParam("dischargeSummeryId") String dischargeSummeryId) {
		Response<String> response = new Response<String>();
		response.setData(dischargeSummaryService.downloadDischargeSummary(dischargeSummeryId));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY)
	public Response<Boolean> emailDischargeSummary(@PathParam(value = "dischargeSummeryId") String dischargeSummeryId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(dischargeSummeryId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. dischargeSummeryId , Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. dischargeSummeryId, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		dischargeSummaryService.emailDischargeSummary(dischargeSummeryId, doctorId, locationId, hospitalId,
				emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT)
	public Response<DischargeSummaryResponse> addMultiVisit(@MatrixParam("visitIds") List<String> visitIds) {

		if (visitIds == null || visitIds.isEmpty()) {
			logger.warn("Invalid Input Visit Ids  Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input Visit Ids  Cannot Be Empty");

		}
		Response<DischargeSummaryResponse> response = new Response<DischargeSummaryResponse>();
		response.setData(dischargeSummaryService.addMultiVisit(visitIds));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA, notes = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA)
	public Response<Integer> updateData() {

		Response<Integer> response = new Response<Integer>();
		response.setData(dischargeSummaryService.upadateDischargeSummaryData());

		return response;
	}

}
