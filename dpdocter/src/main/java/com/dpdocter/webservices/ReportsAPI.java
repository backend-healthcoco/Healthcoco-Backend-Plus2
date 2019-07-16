package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.OPDReportCustomResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsResponse;
import com.dpdocter.services.ReportsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
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
	public Response<IPDReportsResponse> getIPDReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		IPDReportsResponse ipdReports = reportsService.getIPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<IPDReportsResponse> response = new Response<IPDReportsResponse>();
		response.setData(ipdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORTS, notes = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	public Response<OPDReportsResponse> getOPDReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		OPDReportsResponse opdReports = reportsService.getOPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OPDReportsResponse> response = new Response<OPDReportsResponse>();
		response.setData(opdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OT_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORTS, notes = PathProxy.ReportsUrls.GET_OT_REPORTS)
	public Response<OTReportsResponse> getOTReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		OTReportsResponse otReports = reportsService.getOTReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OTReportsResponse> response = new Response<OTReportsResponse>();
		response.setData(otReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	public Response<DeliveryReportsResponse> getDeliveryReports(@QueryParam("locationId") String locationId,@QueryParam("doctorId") String doctorId,@QueryParam("patientId") String patientId,@QueryParam("from") String from,
			@QueryParam("to")String to,@QueryParam("page")  int page, @QueryParam("size") int size, @QueryParam("updatedTime") String updatedTime) {
		DeliveryReportsResponse deliveryReports = reportsService.getDeliveryReportsList(locationId, doctorId, patientId, from, to, page, size, updatedTime);
		Response<DeliveryReportsResponse> response = new Response<DeliveryReportsResponse>();
		response.setData(deliveryReports);
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

	@Path(value = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS, notes = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS)
	public Response<Boolean> addPrescriptionOPDReports() {
		Boolean save = reportsService.addPrescriptionOPDReports();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(save);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS, notes = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS)
	public Response<String> downloadOTReports(@PathParam("otId") String otId) {
		if (DPDoctorUtils.allStringsEmpty(otId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(reportsService.getOTReportsFile(otId));
		return response;
	}
	
	@Path(value = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT, notes = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT)
	public Response<String> downloadDeliveryReports(@PathParam("reportId") String reportId) {
		if (DPDoctorUtils.allStringsEmpty(reportId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(reportsService.getDeliveryReportsFile(reportId));
		return response;
	}
	
	@Path(value = PathProxy.ReportsUrls.UPDATE_OT_REPORTS)
	@POST
	@ApiOperation(value = PathProxy.ReportsUrls.UPDATE_OT_REPORTS, notes = PathProxy.ReportsUrls.UPDATE_OT_REPORTS)
	public Response<Boolean> updateOTReports() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(reportsService.updateOTReports());
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_IPD_REPORT)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORT, notes = PathProxy.ReportsUrls.GET_IPD_REPORT)
	public Response<IPDReports> getIPDReportById(@PathParam("id") String reportId) {
		IPDReports ipdReports = reportsService.getIPDReportById(reportId);
		Response<IPDReports> response = new Response<IPDReports>();
		response.setData(ipdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OPD_REPORT)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORT, notes = PathProxy.ReportsUrls.GET_OPD_REPORT)
	public Response<OPDReportCustomResponse> getOPDReportById(@PathParam("id") String reportId) {
		OPDReportCustomResponse opdReports = reportsService.getOPDReportById(reportId);
		Response<OPDReportCustomResponse> response = new Response<OPDReportCustomResponse>();
		response.setData(opdReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_OT_REPORT)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORT, notes = PathProxy.ReportsUrls.GET_OT_REPORT)
	public Response<OTReports> getOTReportById(@PathParam("id") String reportId) {
		OTReports otReports = reportsService.getOTReportById(reportId);
		Response<OTReports> response = new Response<OTReports>();
		response.setData(otReports);
		return response;
	}

	@Path(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORT)
	@GET
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORT, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORT)
	public Response<DeliveryReports> getDeliveryReportById(@PathParam("id") String reportId) {
		DeliveryReports deliveryReports = reportsService.getDeliveryReportById(reportId);
		Response<DeliveryReports> response = new Response<DeliveryReports>();
		response.setData(deliveryReports);
		return response;
	}

}
