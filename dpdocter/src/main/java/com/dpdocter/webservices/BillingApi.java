package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.BillingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BILLING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BILLING_BASE_URL, description = "")
public class BillingApi {

	private static Logger logger = Logger.getLogger(BillingApi.class.getName());
	
	@Autowired
	private BillingService billingService;

	@Path(value = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS, notes = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS)
	public Response<InvoiceAndReceiptInitials> updateInitials(InvoiceAndReceiptInitials request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		InvoiceAndReceiptInitials resume = billingService.updateInitials(request);
		
		Response<InvoiceAndReceiptInitials> response = new Response<InvoiceAndReceiptInitials>();
		response.setData(resume);
		return response;
	}

}
