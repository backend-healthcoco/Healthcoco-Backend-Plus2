package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.beans.TransactionalSmsReport;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.TransactionSmsServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Component
@Path(PathProxy.TRANSACTION_SMS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.TRANSACTION_SMS_BASE_URL, description = "Endpoint for records")
public class TransactionalSmsApi {

	
	private static Logger logger = LogManager.getLogger(TransactionalSmsApi.class.getName());

	@Autowired
	private TransactionSmsServices transactionSmsServices;
	
	@Path(value = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT)
	@ApiOperation(value = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT, notes = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT)
	@GET
	public Response<MessageResponse> getTransactionSmsReport(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam(value ="page") int page,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam(value ="doctorId") String doctorId,
			@QueryParam(value ="locationIdId") String locationId) {

		Response<MessageResponse> response = new Response<MessageResponse>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(transactionSmsServices.getSmsReport(page, size, doctorId, locationId,fromDate,toDate));
	
		return response;
	}
}
