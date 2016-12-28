package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import com.dpdocter.DoctorPatientReceiptRequest;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;
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

		InvoiceAndReceiptInitials invoiceAndReceiptInitials = billingService.updateInitials(request);
		
		Response<InvoiceAndReceiptInitials> response = new Response<InvoiceAndReceiptInitials>();
		response.setData(invoiceAndReceiptInitials);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_EDIT_INVOICE)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_INVOICE, notes = PathProxy.BillingUrls.ADD_EDIT_INVOICE)
	public Response<DoctorPatientInvoice> addEditInvoice(DoctorPatientInvoice request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId()) ||
				request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoice doctorPatientInvoice = billingService.addEditInvoice(request);
		
		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setData(doctorPatientInvoice);
		return response;
	}
	
	@Path(value = PathProxy.BillingUrls.GET_INVOICES)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICES, notes = PathProxy.BillingUrls.GET_INVOICES)
	public Response<DoctorPatientInvoice> getInvoices(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientInvoice> doctorPatientInvoices = billingService.getInvoices(page, size, doctorId, locationId, hospitalId, patientId, updatedTime, discarded);
		
		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setDataList(doctorPatientInvoices);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT, notes = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	public Response<Double> getAvailableAdvanceAmount(@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Double availableAdvanceAmount = billingService.getAvailableAdvanceAmount(doctorId, locationId, hospitalId, patientId);
		
		Response<Double> response = new Response<Double>();
		response.setData(availableAdvanceAmount);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_INVOICE)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_INVOICE, notes = PathProxy.BillingUrls.DELETE_INVOICE)
	public Response<DoctorPatientInvoice> deleteInvoice(@PathParam("invoiceId") String invoiceId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(invoiceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoice doctorPatientInvoice = billingService.deleteInvoice(invoiceId, discarded);
		
		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_EDIT_RECEIPT)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_RECEIPT, notes = PathProxy.BillingUrls.ADD_EDIT_RECEIPT)
	public Response<DoctorPatientReceipt> addEditReceipt(DoctorPatientReceiptRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientReceipt doctorPatientReceipt = billingService.addEditReceipt(request);
		
		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setData(doctorPatientReceipt);
		return response;
	}
	
	@Path(value = PathProxy.BillingUrls.GET_RECEIPTS)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_RECEIPTS, notes = PathProxy.BillingUrls.GET_RECEIPTS)
	public Response<DoctorPatientReceipt> getReceipts(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientReceipt> doctorPatientReceipts = billingService.getReceipts(page, size, doctorId, locationId, hospitalId, patientId, updatedTime, discarded);
		
		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setDataList(doctorPatientReceipts);
		return response;
	}
	
	@Path(value = PathProxy.BillingUrls.DELETE_RECEIPT)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_RECEIPT, notes = PathProxy.BillingUrls.DELETE_RECEIPT)
	public Response<DoctorPatientReceipt> deleteReceipt(@PathParam("receiptId") String receiptId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(receiptId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientReceipt doctorPatientInvoice = billingService.deleteReceipt(receiptId, discarded);
		
		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setData(doctorPatientInvoice);
		return response;
	}
}
