package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.DischargeSummaryResponse;
import com.dpdocter.services.v2.DischargeSummaryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "DischargeSummaryAPIV2")
@RequestMapping(value=PathProxy.DISCHARGE_SUMMARY_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DISCHARGE_SUMMARY_BASE_URL)
public class DischargeSummaryAPI {

	private Logger logger = LogManager.getLogger(DischargeSummaryAPI.class);

	@Autowired
	DischargeSummaryService dischargeSummaryService;


	@Value(value = "${image.path}")
	private String imagePath;

	
	@GetMapping(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> getDischargeSummary(@RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@RequestParam(value = "patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
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
