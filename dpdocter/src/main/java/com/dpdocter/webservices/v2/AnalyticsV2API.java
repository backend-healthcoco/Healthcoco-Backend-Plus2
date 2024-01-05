package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.v2.PrescriptionAnalyticsV2Service;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.ANALYTICS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ANALYTICS_BASE_URL, description = "")
public class AnalyticsV2API {

	@Autowired
	private PrescriptionAnalyticsV2Service prescriptionAnalyticService;

	@Path(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS, notes = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	public Response<Object> getMostPrescribedPrescriptionItems(@PathParam("type") String type,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(type, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}

		int count = prescriptionAnalyticService.countPrescripedItems(doctorId, locationId, hospitalId, fromDate, toDate,
				type);
		List<?> objects = null;
		if (count > 0) {
			objects = prescriptionAnalyticService.getMostPrescripedPrescriptionItems(type, doctorId, locationId,
					hospitalId, fromDate, toDate, queryType, searchType, page, size);
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
		response.setCount(count);
		return response;
	}

}
