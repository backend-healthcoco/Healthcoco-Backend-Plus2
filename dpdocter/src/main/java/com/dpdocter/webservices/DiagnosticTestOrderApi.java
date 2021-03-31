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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.DiagnosticTestPackage;
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

@RestController
(PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL, description = "Endpoint for diagnostic test order apis")
public class DiagnosticTestOrderApi {

	private static Logger logger = LogManager.getLogger(DiagnosticTestOrderApi.class.getName());
	
	@Autowired
	private DiagnosticTestOrderService diagnosticTestOrderService;

	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_LABS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_LABS, notes = DiagnosticTestOrderUrls.SEARCH_LABS)
	public Response<LabSearchResponse> searchLabs(@RequestParam("city") String city,
			@RequestParam("location") String location, @RequestParam(value = "latitude") String latitude,
			@RequestParam(value = "longitude") String longitude, @RequestParam("searchTerm") String searchTerm, 
			@MatrixParam(value = "test") List<String> testNames, @RequestParam("page") int page, @RequestParam("size") int size,
			@DefaultValue(value = "false") @RequestParam("havePackage") Boolean havePackage) {

		List<LabSearchResponse> labSearchResponses = diagnosticTestOrderService.searchLabs(city, location, latitude, longitude, searchTerm, testNames, page, size, havePackage);

		Response<LabSearchResponse> response = new Response<LabSearchResponse>();
		response.setDataList(labSearchResponses);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS, notes = DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	public Response<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots(@RequestParam("date") String date) {

		List<DiagnosticTestSamplePickUpSlot> labSearchResponses = diagnosticTestOrderService.getDiagnosticTestSamplePickUpTimeSlots(date);
		
		Response<DiagnosticTestSamplePickUpSlot> response = new Response<DiagnosticTestSamplePickUpSlot>();
		response.setDataList(labSearchResponses);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.DiagnosticTestOrderUrls.PLACE_ORDER)
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

	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.GET_PATIENT_ORDERS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_PATIENT_ORDERS, notes = DiagnosticTestOrderUrls.GET_PATIENT_ORDERS)
	public Response<OrderDiagnosticTest> getPatientOrders(@PathVariable("userId") String userId, @RequestParam("page") int page, @RequestParam("size") int size) {

		List<OrderDiagnosticTest> orderDiagnosticTests = diagnosticTestOrderService.getPatientOrders(userId, page, size);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setDataList(orderDiagnosticTests);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.GET_LAB_ORDERS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_LAB_ORDERS, notes = DiagnosticTestOrderUrls.GET_LAB_ORDERS)
	public Response<OrderDiagnosticTest> getLabOrders(@PathVariable("locationId") String locationId, @RequestParam("page") int page, @RequestParam("size") int size) {

		List<OrderDiagnosticTest> orderDiagnosticTests = diagnosticTestOrderService.getLabOrders(locationId, page, size);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setDataList(orderDiagnosticTests);
		return response;
	}
	
	@GetMapping
	(PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST, notes = PathProxy.DiagnosticTestOrderUrls.CANCEL_ORDER_DIAGNOSTIC_TEST)
	public Response<OrderDiagnosticTest> cancelOrderDiagnosticTest(@PathVariable("orderId") String orderId, @PathVariable("userId") String userId) {
		 if (DPDoctorUtils.anyStringEmpty(orderId, userId)) {
				throw new BusinessException(ServiceError.InvalidInput, "OderId or UserId cannot be null");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.cancelOrderDiagnosticTest(orderId, userId));

		return response;
	}
	
	@GetMapping
	(PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID, notes = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	public Response<OrderDiagnosticTest> getDiagnosticTestOrderById(@PathVariable("orderId") String orderId, 
			@DefaultValue(value="false") @RequestParam("isLab") Boolean isLab, @DefaultValue(value="false") @RequestParam("isUser") Boolean isUser) {
		 if (DPDoctorUtils.anyStringEmpty(orderId)) {
				throw new BusinessException(ServiceError.InvalidInput, "OderId cannot be null");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.getDiagnosticTestOrderById(orderId, isLab, isUser));

		return response;
	}
	
	@GetMapping
	(PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES, notes = PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES)
	public Response<DiagnosticTestPackage> getDiagnosticTestPackages(@PathVariable("locationId") String locationId, 
			@PathVariable("hospitalId") String hospitalId, @DefaultValue(value="true") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("page") int page, @RequestParam("size") int size) {
		 if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				throw new BusinessException(ServiceError.InvalidInput, "LocationId or HospitalId cannot be null");
		}
		
		Response<DiagnosticTestPackage> response = new Response<DiagnosticTestPackage>();
		response.setDataList(diagnosticTestOrderService.getDiagnosticTestPackages(locationId, hospitalId, discarded, page, size));

		return response;
	}

	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.SEARCH_DIAGNOSTIC_TEST, notes = PathProxy.DiagnosticTestOrderUrls.SEARCH_DIAGNOSTIC_TEST)
	public Response<Object> searchDiagnosticTest(@RequestParam("page") int page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		
		List<DiagnosticTest> diagnosticTests = diagnosticTestOrderService.searchDiagnosticTest(page, size, updatedTime, discarded, searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(diagnosticTests);
		return response;
	}
}
