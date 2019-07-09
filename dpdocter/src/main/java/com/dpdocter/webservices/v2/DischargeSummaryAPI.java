package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.DischargeSummaryResponse;
import com.dpdocter.services.v2.DischargeSummaryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "DischargeSummaryAPIV2")
@Path(PathProxy.DISCHARGE_SUMMARY_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DISCHARGE_SUMMARY_BASE_URL)
public class DischargeSummaryAPI {

	private Logger logger = Logger.getLogger(DischargeSummaryAPI.class);

	@Autowired
	DischargeSummaryService dischargeSummaryService;


	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> getDischargeSummary(@QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		Response<DischargeSummaryResponse> response = null;
		List<DischargeSummaryResponse> dischargeSummaries = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		dischargeSummaries = dischargeSummaryService.getDischargeSummary(doctorId, locationId, hospitalId, patientId,
				page, size, updatedTime, discarded);
		response = new Response<DischargeSummaryResponse>();
		response.setDataList(dischargeSummaries);

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
