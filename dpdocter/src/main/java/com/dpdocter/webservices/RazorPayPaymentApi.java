package com.dpdocter.webservices;

import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.SettlementResponse;
import com.dpdocter.services.PaymentServices;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.RAZORPAY_BASE_URL)
@Api(value = PathProxy.RAZORPAY_BASE_URL, description = "Endpoint for razorPay settlement api")
public class RazorPayPaymentApi {

	private static Logger logger = Logger.getLogger(BulkMessageApi.class);

	@Autowired
	private PaymentServices smsServices;

	@Path(value = PathProxy.RazorPayUrls.GET_SETTLEMENT)
	@POST
	@ApiOperation(value = PathProxy.RazorPayUrls.GET_SETTLEMENT, notes = PathProxy.RazorPayUrls.GET_SETTLEMENT)
	public String updateDeliveryReports(String request) {

		try {
			request = request.replaceFirst("data=", "");
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("deprecation")
			SettlementResponse list = mapper.readValue(request, SettlementResponse.class);
			smsServices.updateSettlementReport(list);
		} catch (JsonParseException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
		} catch (IOException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
		}
		return "true";
	}
}
