package com.dpdocter.webservices;

import java.util.Date;
import java.util.List;

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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DischargeSummary;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
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
	public Response<DischargeSummary> addEditDischargeSummary(DischargeSummary request) {
		Response<DischargeSummary> response = null;
		DischargeSummary dischargeSummary = null;

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		dischargeSummary = dischargeSummaryService.addEditDischargeSummary(request);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummary>();
			response.setData(dischargeSummary);
		}

		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	public Response<DischargeSummary> getDischargeSummary(@QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		Response<DischargeSummary> response = null;
		List<DischargeSummary> dischargeSummaries = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		dischargeSummaries = dischargeSummaryService.getDischargeSummary(doctorId, locationId, hospitalId, patientId,
				page, size, updatedTime);
		response = new Response<DischargeSummary>();
		response.setDataList(dischargeSummaries);

		return response;

	}

	@Path(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	public Response<DischargeSummary> viewDischargeSummary(@PathParam("dischargeSummeryId") String dischargeSummeryId) {
		Response<DischargeSummary> response = null;
		DischargeSummary dischargeSummary = null;

		if (dischargeSummeryId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		// dischargeSummary = new DischargeSummary();
		dischargeSummary = dischargeSummaryService.viewDischargeSummary(dischargeSummeryId);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummary>();
			response.setData(dischargeSummary);

		}
		return response;
	}

}
