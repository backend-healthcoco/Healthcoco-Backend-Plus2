package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.LabSearchResponse;
import com.dpdocter.services.DiagnosticTestOrderService;
import com.dpdocter.webservices.PathProxy.DiagnosticTestOrderUrls;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL, description = "Endpoint for diagnostic test order apis")
public class DiagnosticTestOrderApi {

	private static Logger logger = Logger.getLogger(DiagnosticTestOrderApi.class.getName());
	
	@Autowired
	private DiagnosticTestOrderService diagnosticTestOrderService;

	@Path(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_LABS)
	@GET
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_LABS, notes = DiagnosticTestOrderUrls.SEARCH_LABS)
	public Response<LabSearchResponse> searchLabs(@QueryParam("city") String city,
			@QueryParam("location") String location, @QueryParam(value = "latitude") String latitude,
			@QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm, 
			@MatrixParam(value = "test") List<String> testNames, @QueryParam("page") long page, @QueryParam("size") int size,
			@DefaultValue(value = "false") @QueryParam("havePackage") Boolean havePackage) {

		List<LabSearchResponse> labSearchResponses = diagnosticTestOrderService.searchLabs(city, location, latitude, longitude, searchTerm, testNames, page, size, havePackage);

		Response<LabSearchResponse> response = new Response<LabSearchResponse>();
		response.setDataList(labSearchResponses);
		return response;
	}
	
	@Path(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	@GET
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS, notes = DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	public Response<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots() {

		List<DiagnosticTestSamplePickUpSlot> labSearchResponses = diagnosticTestOrderService.getDiagnosticTestSamplePickUpTimeSlots();
		
		Response<DiagnosticTestSamplePickUpSlot> response = new Response<DiagnosticTestSamplePickUpSlot>();
		response.setDataList(labSearchResponses);
		return response;
	}
	
	@Path(value = PathProxy.DiagnosticTestOrderUrls.PLACE_ORDER)
	@POST
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.PLACE_ORDER, notes = DiagnosticTestOrderUrls.PLACE_ORDER)
	public Response<OrderDiagnosticTest> placeDiagnosticTestOrder(OrderDiagnosticTest request) {

		if(request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.placeDiagnosticTestOrder(request));
		return response;
	}
	
}
