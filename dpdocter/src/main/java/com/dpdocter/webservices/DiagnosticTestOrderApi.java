package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

import common.util.web.DPDoctorUtils;
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
	public Response<LabSearchResponse> searchLabsByTest(@QueryParam("city") String city,
			@QueryParam("location") String location, @QueryParam(value = "latitude") String latitude,
			@QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm, 
			@MatrixParam(value = "test") List<String> testNames, @QueryParam("page") int page, @QueryParam("size") int size) {

		List<LabSearchResponse> labSearchResponses = diagnosticTestOrderService.searchLabsByTest(city, location, latitude, longitude, searchTerm, testNames, page, size);

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

	@Path(value = PathProxy.DiagnosticTestOrderUrls.GET_PATIENT_ORDERS)
	@GET
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_PATIENT_ORDERS, notes = DiagnosticTestOrderUrls.GET_PATIENT_ORDERS)
	public Response<OrderDiagnosticTest> getPatientOrders(@PathParam("userId") String userId, @QueryParam("page") int page, @QueryParam("size") int size) {

		List<OrderDiagnosticTest> orderDiagnosticTests = diagnosticTestOrderService.getPatientOrders(userId, page, size);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setDataList(orderDiagnosticTests);
		return response;
	}
	
	@Path(value = PathProxy.DiagnosticTestOrderUrls.GET_LAB_ORDERS)
	@GET
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_LAB_ORDERS, notes = DiagnosticTestOrderUrls.GET_LAB_ORDERS)
	public Response<OrderDiagnosticTest> getLabOrders(@PathParam("locationId") String locationId, @QueryParam("page") int page, @QueryParam("size") int size) {

		List<OrderDiagnosticTest> orderDiagnosticTests = diagnosticTestOrderService.getLabOrders(locationId, page, size);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setDataList(orderDiagnosticTests);
		return response;
	}
	
	@GET
	@Path(PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST, notes = PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST)
	public Response<OrderDiagnosticTest> cancelOrderDiagnosticTest(@PathParam("orderId") String orderId, @PathParam("userId") String userId) {
		 if (DPDoctorUtils.anyStringEmpty(orderId, userId)) {
				throw new BusinessException(ServiceError.InvalidInput, "OderId or UserId cannot be null");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.cancelOrderDiagnosticTest(orderId, userId));

		return response;
	}
	
	@GET
	@Path(PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID, notes = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	public Response<OrderDiagnosticTest> getDiagnosticTestOrderById(@PathParam("orderId") String orderId, 
			@DefaultValue(value="false") @QueryParam("isLab") Boolean isLab, @DefaultValue(value="false") @QueryParam("isUser") Boolean isUser) {
		 if (DPDoctorUtils.anyStringEmpty(orderId)) {
				throw new BusinessException(ServiceError.InvalidInput, "OderId cannot be null");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.getDiagnosticTestOrderById(orderId, isLab, isUser));

		return response;
	}
}
