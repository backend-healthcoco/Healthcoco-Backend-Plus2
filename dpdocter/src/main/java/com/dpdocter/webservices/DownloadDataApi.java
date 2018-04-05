package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.services.DownloadDateServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DOWNLOAD_DATA_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOWNLOAD_DATA_BASE_URL, description = "Endpoint for upload data")
public class DownloadDataApi {

	private static Logger logger = Logger.getLogger(DownloadDataApi.class.getName());

	@Autowired
	private DownloadDateServices downloadDataServices;

	@Path(value = PathProxy.DownloadDataUrls.DATA)
	@POST
	@ApiOperation(value = PathProxy.DownloadDataUrls.DATA, notes = PathProxy.DownloadDataUrls.DATA)
	public Response<Boolean> uploadPatientData(ExportRequest request) {
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataServices.downlaodData(request));
		return response;
	}
	
	
	@Path(value = "sendData")
	@GET
	public Response<Boolean> sendData() {
		downloadDataServices.sendDataToDoctor();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
