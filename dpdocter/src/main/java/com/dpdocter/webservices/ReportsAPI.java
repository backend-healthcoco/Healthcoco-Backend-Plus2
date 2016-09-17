package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.ReportsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.REPORTS_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REPORTS_BASE_URL, description = "Endpoint for Medical Report Register")
public class ReportsAPI {

	private static Logger logger = Logger.getLogger(ReportsAPI.class.getName());

	@Autowired
	private ReportsService reportsService;

	@Path(value = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORTS, notes = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	public Response<IPDReports> getIPDReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		List<IPDReports> ipdReports = reportsService.getIPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<IPDReports> response = new Response<IPDReports>();
		response.setDataList(ipdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORTS, notes = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	public Response<OPDReports> getOPDReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		List<OPDReports> opdReports = reportsService.getOPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OPDReports> response = new Response<OPDReports>();
		response.setDataList(opdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OT_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORTS, notes = PathProxy.ReportsUrls.GET_OT_REPORTS)
	public Response<OTReports> getOTReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		List<OTReports> otReports = reportsService.getOTReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OTReports> response = new Response<OTReports>();
		response.setDataList(otReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	public Response<DeliveryReports> getDeliveryReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		List<DeliveryReports> deliveryReports = reportsService.getDeliveryReportsList(locationId, doctorId, patientId, from, to, page, size, updatedTime);
		Response<DeliveryReports> response = new Response<DeliveryReports>();
		response.setDataList(deliveryReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS)
	@POST
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS)
	public Response<IPDReports> submitIPDReports(IPDReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		IPDReports ipdReports = reportsService.submitIPDReport(request);
		Response<IPDReports> response = new Response<IPDReports>();
		response.setData(ipdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS)
	@POST
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS)
	public Response<OPDReports> submitOPDReports(OPDReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		OPDReports opdReports = reportsService.submitOPDReport(request);
		Response<OPDReports> response = new Response<OPDReports>();
		response.setData(opdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS)
	@POST
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS)
	public Response<OTReports> submitOTReports(OTReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		OTReports otReports = reportsService.submitOTReport(request);
		Response<OTReports> response = new Response<OTReports>();
		response.setData(otReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS)
	@POST
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS)
	public Response<DeliveryReports> submitDeliveryReports(DeliveryReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		DeliveryReports deliveryReports = reportsService.submitDeliveryReport(request);
		Response<DeliveryReports> response = new Response<DeliveryReports>();
		response.setData(deliveryReports);
		return response;
	}

}
