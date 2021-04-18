package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.response.v2.DeliveryReportsResponse;
import com.dpdocter.response.v2.IPDReportsResponse;
import com.dpdocter.response.v2.OTReportsResponse;
import com.dpdocter.services.v2.ReportsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "ReportsAPIV2")
@RequestMapping(value=PathProxy.REPORTS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)

@Api(value = PathProxy.REPORTS_BASE_URL, description = "Endpoint for Medical Report Register")
public class ReportsAPI {

	private static Logger logger = LogManager.getLogger(ReportsAPI.class.getName());

	@Autowired
	private ReportsService reportsService;

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORTS, notes = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	public Response<IPDReportsResponse> getIPDReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		IPDReportsResponse ipdReports = reportsService.getIPDReportsList(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, discarded);
		Response<IPDReportsResponse> response = new Response<IPDReportsResponse>();
		response.setData(ipdReports);
		return response;
	}

/*	(value = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	@GetMapping
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORTS, notes = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	public Response<OPDReportsResponse> getOPDReports(@RequestParam("locationId") String locationId,@RequestParam("doctorId") String doctorId,@RequestParam("patientId") String patientId,@RequestParam("from") String from,
			@RequestParam("to")String to,@RequestParam("page")  int page, @RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime) {
		OPDReportsResponse opdReports = reportsService.getOPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OPDReportsResponse> response = new Response<OPDReportsResponse>();
		response.setData(opdReports);
		return response;
	}*/

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_OT_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORTS, notes = PathProxy.ReportsUrls.GET_OT_REPORTS)
	public Response<OTReportsResponse> getOTReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		OTReportsResponse otReports = reportsService.getOTReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime, discarded);
		Response<OTReportsResponse> response = new Response<OTReportsResponse>();
		response.setData(otReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	public Response<DeliveryReportsResponse> getDeliveryReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		DeliveryReportsResponse deliveryReports = reportsService.getDeliveryReportsList(locationId, doctorId, patientId,
				from, to, page, size, updatedTime, discarded);
		Response<DeliveryReportsResponse> response = new Response<DeliveryReportsResponse>();
		response.setData(deliveryReports);
		return response;
	}

	
}
