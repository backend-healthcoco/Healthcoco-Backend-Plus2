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

import com.dpdocter.beans.DoctorExpense;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.ExpenseType;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.beans.VendorExpense;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorAmountRequest;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.request.InvoiceItemChangeStatusRequest;
import com.dpdocter.request.ListIdrequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;
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

	@Path(value = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS, notes = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS)
	public Response<InvoiceAndReceiptInitials> getInitials(@PathParam("locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		InvoiceAndReceiptInitials invoiceAndReceiptInitials = billingService.getInitials(locationId);

		Response<InvoiceAndReceiptInitials> response = new Response<InvoiceAndReceiptInitials>();
		response.setData(invoiceAndReceiptInitials);
		return response;
	}

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
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
						request.getPatientId())
				|| request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
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
	public Response<DoctorPatientInvoice> getInvoices(@PathParam("type") String type, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientInvoice> doctorPatientInvoices = billingService.getInvoices(type, page, size, doctorId,
				locationId, hospitalId, patientId, updatedTime, from, to, discarded);

		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setDataList(doctorPatientInvoices);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_INVOICE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICE, notes = PathProxy.BillingUrls.GET_INVOICE)
	public Response<DoctorPatientInvoice> getInvoices(@PathParam("invoiceId") String invoiceId) {
		if (DPDoctorUtils.anyStringEmpty(invoiceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoice doctorPatientInvoice = billingService.getInvoice(invoiceId);

		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT, notes = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	public Response<Double> getAvailableAdvanceAmount(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Double availableAdvanceAmount = billingService.getAvailableAdvanceAmount(doctorId, locationId, hospitalId,
				patientId);

		Response<Double> response = new Response<Double>();
		response.setData(availableAdvanceAmount);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_INVOICE)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_INVOICE, notes = PathProxy.BillingUrls.DELETE_INVOICE)
	public Response<DoctorPatientInvoice> deleteInvoice(@PathParam("invoiceId") String invoiceId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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
	public Response<DoctorPatientReceiptAddEditResponse> addEditReceipt(DoctorPatientReceiptRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientReceiptAddEditResponse doctorPatientReceipt = billingService.addEditReceipt(request);

		Response<DoctorPatientReceiptAddEditResponse> response = new Response<DoctorPatientReceiptAddEditResponse>();
		response.setData(doctorPatientReceipt);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_RECEIPTS)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_RECEIPTS, notes = PathProxy.BillingUrls.GET_RECEIPTS)
	public Response<DoctorPatientReceipt> getReceipts(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @QueryParam("from") String from,
			@QueryParam("to") String to, @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientReceipt> doctorPatientReceipts = billingService.getReceipts(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime, from, to, discarded);

		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setDataList(doctorPatientReceipts);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_RECEIPT)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_RECEIPT, notes = PathProxy.BillingUrls.DELETE_RECEIPT)
	public Response<DoctorPatientReceipt> deleteReceipt(@PathParam("receiptId") String receiptId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(receiptId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientReceipt doctorPatientInvoice = billingService.deleteReceipt(receiptId, discarded);

		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY, notes = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY)
	public Response<DoctorPatientInvoiceAndReceiptResponse> addInvoiceAndPay(
			DoctorPatientInvoiceAndReceiptRequest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
						request.getPatientId())
				|| request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoiceAndReceiptResponse doctorPatientInvoice = billingService.addInvoiceAndPay(request);

		Response<DoctorPatientInvoiceAndReceiptResponse> response = new Response<DoctorPatientInvoiceAndReceiptResponse>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT, notes = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT)
	public Response<Double> getTotalDueAmount(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Double availableAdvanceAmount = billingService.getTotalDueAmount(doctorId, locationId, hospitalId, patientId);

		Response<Double> response = new Response<Double>();
		response.setData(availableAdvanceAmount);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_LEDGER)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_LEDGER, notes = PathProxy.BillingUrls.GET_LEDGER)
	public Response<DoctorPatientLedgerResponse> getLedger(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId, @QueryParam(value = "from") String from,
			@QueryParam(value = "to") String to, @QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @QueryParam(value = "type") String type,
			@DefaultValue(value = "0") @QueryParam(value = "updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientLedgerResponse doctorPatientLedgers = billingService.getLedger(doctorId, locationId, hospitalId,
				patientId, from, to, page, size, updatedTime, type);

		Response<DoctorPatientLedgerResponse> response = new Response<DoctorPatientLedgerResponse>();
		response.setData(doctorPatientLedgers);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT, notes = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT)
	public Response<AmountResponse> getTotalDueAndAdvanceAmount(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		AmountResponse amountResponse = billingService.getTotalDueAndAdvanceAmount(doctorId, locationId, hospitalId,
				patientId);

		Response<AmountResponse> response = new Response<AmountResponse>();
		response.setData(amountResponse);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DOWNLOAD_INVOICE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_INVOICE, notes = PathProxy.BillingUrls.DOWNLOAD_INVOICE)
	public Response<String> downloadInvoice(@PathParam("invoiceId") String invoiceId) {
		Response<String> response = new Response<String>();
		response.setData(billingService.downloadInvoice(invoiceId));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DOWNLOAD_RECEIPT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_RECEIPT, notes = PathProxy.BillingUrls.DOWNLOAD_RECEIPT)
	public Response<String> downloadReceipt(@PathParam("receiptId") String receiptId) {
		Response<String> response = new Response<String>();
		response.setData(billingService.downloadReceipt(receiptId));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.EMAIL_INVOICE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_INVOICE, notes = PathProxy.BillingUrls.EMAIL_INVOICE)
	public Response<Boolean> emailInvoice(@PathParam("invoiceId") String invoiceId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam(value = "emailAddress") String emailAddress) {
		billingService.emailInvoice(invoiceId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.SMS_INVOICE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.SMS_INVOICE, notes = PathProxy.BillingUrls.SMS_INVOICE)
	public Response<Boolean> sendInvoice(@PathParam("invoiceId") String invoiceId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("mobileNumber") String mobileNumber) {
		billingService.sendInvoiceToPatient(doctorId, locationId, hospitalId, invoiceId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.SMS_RECEIPT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.SMS_RECEIPT, notes = PathProxy.BillingUrls.SMS_RECEIPT)
	public Response<Boolean> sendReceipt(@PathParam("receiptId") String receiptId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("mobileNumber") String mobileNumber) {
		billingService.sendReceiptToPatient(doctorId, locationId, hospitalId, receiptId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.EMAIL_RECEIPT)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_RECEIPT, notes = PathProxy.BillingUrls.EMAIL_RECEIPT)
	public Response<Boolean> emailReceipt(@PathParam("receiptId") String receiptId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam(value = "emailAddress") String emailAddress) {
		billingService.emailReceipt(receiptId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER, notes = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER)
	public Response<Boolean> sendDueAmountRemainder(@PathParam("patientId") String patientId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("mobileNumber") String mobileNumber) {
		billingService.sendDueRemainderToPatient(doctorId, locationId, hospitalId, patientId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT, notes = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT)
	public Response<String> downloadMultipleReceipt(ListIdrequest request) {

		if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(billingService.downloadMultipleReceipt(request.getIds()));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT, notes = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT)
	public Response<Boolean> emailMultipleReceipt(ListIdrequest request) {

		if (request == null || request.getIds() == null || request.getIds().isEmpty()
				|| DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		billingService.emailMultipleReceipt(request.getIds(), request.getEmailAddress());
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS, notes = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS)
	public Response<Boolean> changeInvoiceItemTreatmentStatus(InvoiceItemChangeStatusRequest request) {

		if (request == null || request.getInvoiceId() == null || request.getItemId().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(billingService.changeInvoiceTreatmentStatus(request));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_EDIT_EXPENSE)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_EXPENSE, notes = PathProxy.BillingUrls.ADD_EDIT_EXPENSE)
	public Response<DoctorExpense> addEditExpense(DoctorExpense request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorExpense expense = billingService.addEditDoctorExpense(request);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setData(expense);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_EXPENSES)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSES, notes = PathProxy.BillingUrls.GET_EXPENSES)
	public Response<DoctorExpense> getExpenses(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String from,
			@QueryParam("toDate") String to, @QueryParam("searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded,
			@QueryParam("expenseType") String expenseType, @QueryParam("paymentMode") String paymentMode,
			@QueryParam("vendor") String vendor) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorExpense> expenses = billingService.getDoctorExpenses(expenseType, page, size, doctorId, locationId,
				hospitalId, updatedTime, from, to, searchTerm, discarded, paymentMode, vendor);
		int count = billingService.getDoctorExpensesCount(expenseType, doctorId, locationId, hospitalId, updatedTime, from,
				to, searchTerm, discarded, paymentMode, vendor);
		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setDataList(expenses);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.TOTAL_EXPENSES_COST)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.TOTAL_EXPENSES_COST, notes = PathProxy.BillingUrls.TOTAL_EXPENSES_COST)
	public Response<Double> countExpenses(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded,
			@QueryParam("expenseType") String expenseType, @QueryParam("paymentMode") String paymentMode) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Double count = billingService.countDoctorExpenses(expenseType, doctorId, locationId, hospitalId, updatedTime,
				discarded, paymentMode);

		Response<Double> response = new Response<Double>();
		response.setData(count);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_EXPENSE)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_EXPENSE, notes = PathProxy.BillingUrls.DELETE_EXPENSE)
	public Response<DoctorExpense> deleteExpense(@PathParam("expenseId") String expenseId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(expenseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorExpense expense = billingService.deleteDoctorExpense(expenseId, discarded);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setData(expense);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_EXPENSE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE, notes = PathProxy.BillingUrls.GET_EXPENSE)
	public Response<DoctorExpense> getExpense(@PathParam("expenseId") String expenseId) {
		if (DPDoctorUtils.anyStringEmpty(expenseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorExpense expense = billingService.getDoctorExpense(expenseId);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setData(expense);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_EXPENSE_TYPE)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EXPENSE_TYPE, notes = PathProxy.BillingUrls.ADD_EXPENSE_TYPE)
	public Response<ExpenseType> addExpenseType(ExpenseType request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getName())) {
			throw new BusinessException(ServiceError.InvalidInput, "doctorId, locationId, hospitalId should not null");
		}

		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setData(billingService.addEditExpenseType(request));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID, notes = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID)
	public Response<ExpenseType> getExpenseById(@QueryParam("expenseTypeId") String expenseTypeId) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setData(billingService.getExpenseType(expenseTypeId));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE, notes = PathProxy.BillingUrls.GET_EXPENSE_TYPE)
	public Response<ExpenseType> getExpenses(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "doctorId, locationId, hospitalId should not null");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setDataList(
				billingService.getExpenseType(page, size, doctorId, locationId, hospitalId, searchTerm, discarded));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE, notes = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE)
	public Response<Boolean> discardExpenseType(@PathParam("expenseTypeId") String expenseTypeId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(billingService.deleteExpenseType(expenseTypeId, discarded));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE)
	public Response<VendorExpense> addEditVendorExpense(VendorExpense request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		VendorExpense vendorExpense = billingService.addEditVendor(request);

		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(vendorExpense);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.GET_VENDOR_EXPENSE)
	public Response<VendorExpense> getVendorExpense(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

		List<VendorExpense> doctorPatientInvoices = billingService.getVendors(size, page, searchTerm, discarded,
				doctorId, locationId, hospitalId);
		Integer count = billingService.countVendorExpense(discarded, searchTerm);
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setDataList(doctorPatientInvoices);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE)
	@DELETE
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE)
	public Response<VendorExpense> deleteVendorExpense(@PathParam("vendorExpenseId") String vendorExpenseId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(vendorExpenseId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(billingService.discardVendor(vendorExpenseId, discarded));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID, notes = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID)
	public Response<VendorExpense> getVendorExpenseById(@QueryParam("vendorExpenseId") String vendorExpenseId) {
		if (DPDoctorUtils.anyStringEmpty(vendorExpenseId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(billingService.getVendorExpenseById(vendorExpenseId));
		return response;
	}

	@Path(value = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT)
	@POST
	@ApiOperation(value = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT, notes = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT)
	public Response<Boolean> updateTotalDueAmount(DoctorAmountRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean dueAmount = billingService.updateTotalDueAmount(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dueAmount);
		return response;
	}

}
