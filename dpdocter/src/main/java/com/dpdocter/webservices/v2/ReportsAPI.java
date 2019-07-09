package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.response.v2.DeliveryReportsResponse;
import com.dpdocter.response.v2.IPDReportsResponse;
import com.dpdocter.response.v2.OTReportsResponse;
import com.dpdocter.services.v2.ReportsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "ReportsAPIV2")
@Path(PathProxy.REPORTS_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REPORTS_BASE_URL, description = "Endpoint for Medical Report Register")
public class ReportsAPI {

	private static Logger logger = Logger.getLogger(ReportsAPI.class.getName());

	@Autowired
	private ReportsService reportsService;

	@Path(value = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORTS, notes = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	public Response<IPDReportsResponse> getIPDReports(@QueryParam("locationId") String locationId,
			@QueryParam("doctorId") String doctorId, @QueryParam("patientId") String patientId,
			@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		IPDReportsResponse ipdReports = reportsService.getIPDReportsList(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, discarded);
		Response<IPDReportsResponse> response = new Response<IPDReportsResponse>();
		response.setData(ipdReports);
		return response;
	}

/*	@Path(value = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORTS, notes = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	public Response<OPDReportsResponse> getOPDReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		OPDReportsResponse opdReports = reportsService.getOPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OPDReportsResponse> response = new Response<OPDReportsResponse>();
		response.setData(opdReports);
		return response;
	}*/

	@Path(value = PathProxy.ReportsUrls.GET_OT_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORTS, notes = PathProxy.ReportsUrls.GET_OT_REPORTS)
	public Response<OTReportsResponse> getOTReports(@QueryParam("locationId") String locationId,
			@QueryParam("doctorId") String doctorId, @QueryParam("patientId") String patientId,
			@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		OTReportsResponse otReports = reportsService.getOTReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime, discarded);
		Response<OTReportsResponse> response = new Response<OTReportsResponse>();
		response.setData(otReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	public Response<DeliveryReportsResponse> getDeliveryReports(@QueryParam("locationId") String locationId,
			@QueryParam("doctorId") String doctorId, @QueryParam("patientId") String patientId,
			@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		DeliveryReportsResponse deliveryReports = reportsService.getDeliveryReportsList(locationId, doctorId, patientId,
				from, to, page, size, updatedTime, discarded);
		Response<DeliveryReportsResponse> response = new Response<DeliveryReportsResponse>();
		response.setData(deliveryReports);
		return response;
	}

	
}
