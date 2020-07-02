package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;




import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.BulkSmsServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BULK_SMS_PACKAGE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BULK_SMS_PACKAGE_BASE_URL, description = "Endpoint for records")
public class BulkSmsApi {
	
	private static Logger logger = LogManager.getLogger(BulkSmsApi.class.getName());


	@Autowired
	private BulkSmsServices bulkSmsServices;
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@GET
	public Response<BulkSmsPackage> getBulkSmsPackages(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam( value ="page") int page,
			@QueryParam(value ="discarded") Boolean discarded, 
			@QueryParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
			response.setCount(bulkSmsServices.CountBulkSmsPackage(searchTerm, discarded));
			response.setDataList(bulkSmsServices.getBulkSmsPackage(page, size, searchTerm, discarded));
	
		return response;
	}
	
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS, notes = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@GET	
	public Response<BulkSmsCredits> getBulkSmsCredits(@QueryParam(value ="doctorId") String doctorId) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null) {
			logger.warn("doctorId send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setData(bulkSmsServices.getCreditsByDoctorId(doctorId));
	
		return response;
	}
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@GET
	public Response<BulkSmsCredits> getBulkSmsHistory(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam(value ="page") int page,
			@QueryParam(value ="doctorId") String doctorId, 
			@QueryParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null) {
			logger.warn("doctorId send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getBulkSmsHistory(page, size, searchTerm, doctorId));
	
		return response;
	}
	
	

}
