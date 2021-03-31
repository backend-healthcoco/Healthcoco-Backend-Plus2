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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping(value=PathProxy.BILLING_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BILLING_BASE_URL, description = "")
public class BillingApi {

	private static Logger logger = LogManager.getLogger(BillingApi.class.getName());

	@Autowired
	private BillingService billingService;

	@GetMapping(value = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS)
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS, notes = PathProxy.BillingUrls.GET_INVOICE_RECEIPT_INITIALS)
	public Response<InvoiceAndReceiptInitials> getInitials(@PathVariable("locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		InvoiceAndReceiptInitials invoiceAndReceiptInitials = billingService.getInitials(locationId);

		Response<InvoiceAndReceiptInitials> response = new Response<InvoiceAndReceiptInitials>();
		response.setData(invoiceAndReceiptInitials);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS)
	@ApiOperation(value = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS, notes = PathProxy.BillingUrls.UPDATE_INVOICE_RECEIPT_INITIALS)
	public Response<InvoiceAndReceiptInitials> updateInitials(@RequestBody InvoiceAndReceiptInitials request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		InvoiceAndReceiptInitials invoiceAndReceiptInitials = billingService.updateInitials(request);

		Response<InvoiceAndReceiptInitials> response = new Response<InvoiceAndReceiptInitials>();
		response.setData(invoiceAndReceiptInitials);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.ADD_EDIT_INVOICE)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_INVOICE, notes = PathProxy.BillingUrls.ADD_EDIT_INVOICE)
	public Response<DoctorPatientInvoice> addEditInvoice(@RequestBody DoctorPatientInvoice request) {
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

	@GetMapping(value = PathProxy.BillingUrls.GET_INVOICES)
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICES, notes = PathProxy.BillingUrls.GET_INVOICES)
	public Response<DoctorPatientInvoice> getInvoices(@PathVariable("type") String type, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("patientId") String patientId, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("from") String from,@RequestParam("to") String to,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientInvoice> doctorPatientInvoices = billingService.getInvoices(type, page, size, doctorId,
				locationId, hospitalId, patientId, updatedTime,from,to, discarded);

		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setDataList(doctorPatientInvoices);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_INVOICE)
	@ApiOperation(value = PathProxy.BillingUrls.GET_INVOICE, notes = PathProxy.BillingUrls.GET_INVOICE)
	public Response<DoctorPatientInvoice> getInvoices(@PathVariable("invoiceId") String invoiceId) {
		if (DPDoctorUtils.anyStringEmpty(invoiceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoice doctorPatientInvoice = billingService.getInvoice(invoiceId);

		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	@ApiOperation(value = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT, notes = PathProxy.BillingUrls.GET_AVAILABLE_ADVANCE_AMOUNT)
	public Response<Double> getAvailableAdvanceAmount(@RequestParam("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId) {
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

	@DeleteMapping(value = PathProxy.BillingUrls.DELETE_INVOICE)
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_INVOICE, notes = PathProxy.BillingUrls.DELETE_INVOICE)
	public Response<DoctorPatientInvoice> deleteInvoice(@PathVariable("invoiceId") String invoiceId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(invoiceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientInvoice doctorPatientInvoice = billingService.deleteInvoice(invoiceId, discarded);

		Response<DoctorPatientInvoice> response = new Response<DoctorPatientInvoice>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.ADD_EDIT_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_RECEIPT, notes = PathProxy.BillingUrls.ADD_EDIT_RECEIPT)
	public Response<DoctorPatientReceiptAddEditResponse> addEditReceipt(@RequestBody DoctorPatientReceiptRequest request) {
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

	@GetMapping(value = PathProxy.BillingUrls.GET_RECEIPTS)
	@ApiOperation(value = PathProxy.BillingUrls.GET_RECEIPTS, notes = PathProxy.BillingUrls.GET_RECEIPTS)
	public Response<DoctorPatientReceipt> getReceipts(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("from") String from,@RequestParam("to") String to,
		    @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorPatientReceipt> doctorPatientReceipts = billingService.getReceipts(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime,from,to, discarded);

		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setDataList(doctorPatientReceipts);
		return response;
	}

	@DeleteMapping(value = PathProxy.BillingUrls.DELETE_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_RECEIPT, notes = PathProxy.BillingUrls.DELETE_RECEIPT)
	public Response<DoctorPatientReceipt> deleteReceipt(@PathVariable("receiptId") String receiptId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(receiptId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientReceipt doctorPatientInvoice = billingService.deleteReceipt(receiptId, discarded);

		Response<DoctorPatientReceipt> response = new Response<DoctorPatientReceipt>();
		response.setData(doctorPatientInvoice);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY, notes = PathProxy.BillingUrls.ADD_INVOICE_AND_PAY)
	public Response<DoctorPatientInvoiceAndReceiptResponse> addInvoiceAndPay(
			@RequestBody DoctorPatientInvoiceAndReceiptRequest request) {
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

	@GetMapping(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT)
	@ApiOperation(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT, notes = PathProxy.BillingUrls.GET_TOTAL_DUE_AMOUNT)
	public Response<Double> getTotalDueAmount(@RequestParam("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Double availableAdvanceAmount = billingService.getTotalDueAmount(doctorId, locationId, hospitalId, patientId);

		Response<Double> response = new Response<Double>();
		response.setData(availableAdvanceAmount);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_LEDGER)
	@ApiOperation(value = PathProxy.BillingUrls.GET_LEDGER, notes = PathProxy.BillingUrls.GET_LEDGER)
	public Response<DoctorPatientLedgerResponse> getLedger(@RequestParam("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId, @RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to, @RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size,
			@DefaultValue(value = "0") @RequestParam(value = "updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorPatientLedgerResponse doctorPatientLedgers = billingService.getLedger(doctorId, locationId, hospitalId,
				patientId, from, to, page, size, updatedTime);

		Response<DoctorPatientLedgerResponse> response = new Response<DoctorPatientLedgerResponse>();
		response.setData(doctorPatientLedgers);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT)
	@ApiOperation(value = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT, notes = PathProxy.BillingUrls.GET_TOTAL_DUE_AND_ADVANCE_AMOUNT)
	public Response<AmountResponse> getTotalDueAndAdvanceAmount(@RequestParam("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId) {
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

	@GetMapping(value = PathProxy.BillingUrls.DOWNLOAD_INVOICE)
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_INVOICE, notes = PathProxy.BillingUrls.DOWNLOAD_INVOICE)
	public Response<String> downloadInvoice(@PathVariable("invoiceId") String invoiceId) {
		Response<String> response = new Response<String>();
		response.setData(billingService.downloadInvoice(invoiceId));
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.DOWNLOAD_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_RECEIPT, notes = PathProxy.BillingUrls.DOWNLOAD_RECEIPT)
	public Response<String> downloadReceipt(@PathVariable("receiptId") String receiptId) {
		Response<String> response = new Response<String>();
		response.setData(billingService.downloadReceipt(receiptId));
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.EMAIL_INVOICE)
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_INVOICE, notes = PathProxy.BillingUrls.EMAIL_INVOICE)
	public Response<Boolean> emailInvoice(@PathVariable("invoiceId") String invoiceId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable(value = "emailAddress") String emailAddress) {
		billingService.emailInvoice(invoiceId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
		@GetMapping(value = PathProxy.BillingUrls.SMS_INVOICE)
		@ApiOperation(value = PathProxy.BillingUrls.SMS_INVOICE, notes = PathProxy.BillingUrls.SMS_INVOICE)
		public Response<Boolean> sendInvoice(@PathVariable("invoiceId") String invoiceId,
				@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
				@PathVariable("hospitalId") String hospitalId, @PathVariable("mobileNumber") String mobileNumber) {
			billingService.sendInvoiceToPatient(doctorId, locationId, hospitalId, invoiceId, mobileNumber);
			Response<Boolean> response = new Response<Boolean>();
			response.setData(true);
			return response;
		}

	
	@GetMapping(value = PathProxy.BillingUrls.SMS_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.SMS_RECEIPT, notes = PathProxy.BillingUrls.SMS_RECEIPT)
	public Response<Boolean> sendReceipt(@PathVariable("receiptId") String receiptId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable("mobileNumber") String mobileNumber) {
		billingService.sendReceiptToPatient(doctorId, locationId, hospitalId, receiptId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}


	@GetMapping(value = PathProxy.BillingUrls.EMAIL_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_RECEIPT, notes = PathProxy.BillingUrls.EMAIL_RECEIPT)
	public Response<Boolean> emailReceipt(@PathVariable("receiptId") String receiptId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable(value = "emailAddress") String emailAddress) {
		billingService.emailReceipt(receiptId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER)
	@ApiOperation(value = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER, notes = PathProxy.BillingUrls.DUE_AMOUNT_REMAINDER)
	public Response<Boolean> sendDueAmountRemainder(@PathVariable("patientId") String patientId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable("mobileNumber") String mobileNumber) {
		billingService.sendDueRemainderToPatient(doctorId, locationId, hospitalId, patientId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT, notes = PathProxy.BillingUrls.DOWNLOAD_MULTIPLE_RECEIPT)
	public Response<String> downloadMultipleReceipt(@RequestBody ListIdrequest request) {

		if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(billingService.downloadMultipleReceipt(request.getIds()));
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT)
	@ApiOperation(value = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT, notes = PathProxy.BillingUrls.EMAIL_MULTIPLE_RECEIPT)
	public Response<Boolean> emailMultipleReceipt(@RequestBody ListIdrequest request) {

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

	@PostMapping(value = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS)
	@ApiOperation(value = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS, notes = PathProxy.BillingUrls.CHANGE_INVOICE_ITEM_TREATMENT_STATUS)
	public Response<Boolean> changeInvoiceItemTreatmentStatus(@RequestBody InvoiceItemChangeStatusRequest request) {

		if (request == null || request.getInvoiceId() == null || request.getItemId().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(billingService.changeInvoiceTreatmentStatus(request));
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.ADD_EDIT_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_EXPENSE, notes = PathProxy.BillingUrls.ADD_EDIT_EXPENSE)
	public Response<DoctorExpense> addEditExpense(@RequestBody DoctorExpense request) {
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

	@GetMapping(value = PathProxy.BillingUrls.GET_EXPENSES)
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSES, notes = PathProxy.BillingUrls.GET_EXPENSES)
	public Response<DoctorExpense> getExpenses(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("searchTerm") String searchTerm,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("expenseType") String expenseType, @RequestParam("paymentMode") String paymentMode) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<DoctorExpense> expenses = billingService.getDoctorExpenses(expenseType, page, size, doctorId, locationId,
				hospitalId, updatedTime,from,to,searchTerm,discarded, paymentMode);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setDataList(expenses);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.TOTAL_EXPENSES_COST)
	@ApiOperation(value = PathProxy.BillingUrls.TOTAL_EXPENSES_COST, notes = PathProxy.BillingUrls.TOTAL_EXPENSES_COST)
	public Response<Double> countExpenses(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("expenseType") String expenseType, @RequestParam("paymentMode") String paymentMode) {
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

	@DeleteMapping(value = PathProxy.BillingUrls.DELETE_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_EXPENSE, notes = PathProxy.BillingUrls.DELETE_EXPENSE)
	public Response<DoctorExpense> deleteExpense(@PathVariable("expenseId") String expenseId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(expenseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorExpense expense = billingService.deleteDoctorExpense(expenseId, discarded);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setData(expense);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE, notes = PathProxy.BillingUrls.GET_EXPENSE)
	public Response<DoctorExpense> getExpense(@PathVariable("expenseId") String expenseId) {
		if (DPDoctorUtils.anyStringEmpty(expenseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DoctorExpense expense = billingService.getDoctorExpense(expenseId);

		Response<DoctorExpense> response = new Response<DoctorExpense>();
		response.setData(expense);
		return response;
	}

	@PostMapping(value = PathProxy.BillingUrls.ADD_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EXPENSE_TYPE, notes = PathProxy.BillingUrls.ADD_EXPENSE_TYPE)
	public Response<ExpenseType> addExpenseType(@RequestBody ExpenseType request) {
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

	@GetMapping(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID)
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID, notes = PathProxy.BillingUrls.GET_EXPENSE_TYPE_BY_ID)
	public Response<ExpenseType> getExpenseById(@RequestParam("expenseTypeId") String expenseTypeId) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setData(billingService.getExpenseType(expenseTypeId));
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.BillingUrls.GET_EXPENSE_TYPE, notes = PathProxy.BillingUrls.GET_EXPENSE_TYPE)
	public Response<ExpenseType> getExpenses(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "doctorId, locationId, hospitalId should not null");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setDataList(
				billingService.getExpenseType(page, size, doctorId, locationId, hospitalId, searchTerm, discarded));
		return response;
	}

	@DeleteMapping(value = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE, notes = PathProxy.BillingUrls.DELETE_EXPENSE_TYPE)
	public Response<Boolean> discardExpenseType(@PathVariable("expenseTypeId") String expenseTypeId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(billingService.deleteExpenseType(expenseTypeId, discarded));
		return response;
	}
	
	@PostMapping(value = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.ADD_EDIT_VENDOR_EXPENSE)
	public Response<VendorExpense> addEditVendorExpense(@RequestBody VendorExpense request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		VendorExpense vendorExpense = billingService.addEditVendor(request);

		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(vendorExpense);
		return response;
	}

	@GetMapping(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.GET_VENDOR_EXPENSE)
	public Response<VendorExpense> getVendorExpense(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm,	
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		

		List<VendorExpense> doctorPatientInvoices = billingService.getVendors(size, page, searchTerm, discarded,doctorId,locationId,hospitalId);
		Integer count=billingService.countVendorExpense(discarded, searchTerm);
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setDataList(doctorPatientInvoices);
		response.setCount(count);
		return response;
	}
	
	@DeleteMapping(value = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE)
	@ApiOperation(value = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE, notes = PathProxy.BillingUrls.DELETE_VENDOR_EXPENSE)
	public Response<VendorExpense> deleteVendorExpense(@PathVariable("vendorExpenseId") String vendorExpenseId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(vendorExpenseId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(billingService.discardVendor(vendorExpenseId, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID)
	@ApiOperation(value = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID, notes = PathProxy.BillingUrls.GET_VENDOR_EXPENSE_BY_ID)
	public Response<VendorExpense> getVendorExpenseById(@RequestParam("vendorExpenseId") String vendorExpenseId) {
		if (DPDoctorUtils.anyStringEmpty(vendorExpenseId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<VendorExpense> response = new Response<VendorExpense>();
		response.setData(billingService.getVendorExpenseById(vendorExpenseId));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT)
	@ApiOperation(value = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT, notes = PathProxy.BillingUrls.EDIT_TOTAL_DUE_AMOUNT)
	public Response<Boolean> updateTotalDueAmount(@RequestBody DoctorAmountRequest request) {
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
