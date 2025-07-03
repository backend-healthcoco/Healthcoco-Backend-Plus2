package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DoctorExpense;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.ExpenseType;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.beans.VendorExpense;
import com.dpdocter.request.DoctorAmountRequest;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.request.InvoiceItemChangeStatusRequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;

public interface BillingService {

	InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request);

	DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request);

	List<DoctorPatientInvoice> getInvoices(String type, long page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, String from, String to, Boolean discarded);

	DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded);

	DoctorPatientReceiptAddEditResponse addEditReceipt(DoctorPatientReceiptRequest request);

	List<DoctorPatientReceipt> getReceipts(long page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, String from, String to, Boolean discarded);

	DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded);

	Double getAvailableAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientInvoiceAndReceiptResponse addInvoiceAndPay(DoctorPatientInvoiceAndReceiptRequest request);

	InvoiceAndReceiptInitials getInitials(String locationId);

	Double getTotalDueAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientLedgerResponse getLedger(String doctorId, String locationId, String hospitalId, String patientId,
			String from, String to, long page, int size, String updatedTime, String type);

	AmountResponse getTotalDueAndAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientInvoice getInvoice(String invoiceId);

	String downloadInvoice(String invoiceId);

	String downloadReceipt(String receiptId);

	int getReceiptCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean isOTPVerified);

	int getInvoiceCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean isOTPVerified);

	public void emailReceipt(String receiptId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	public void emailInvoice(String invoiceId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	public Boolean sendDueRemainderToPatient(String doctorId, String locationId, String hospitalId, String patientId,
			String mobileNumber);

	public Boolean sendInvoiceToPatient(String doctorId, String locationId, String hospitalId, String invoiceId,
			String mobileNumber);

	public Boolean sendReceiptToPatient(String doctorId, String locationId, String hospitalId, String receiptId,
			String mobileNumber);

	String downloadMultipleReceipt(List<String> ids);

	void emailMultipleReceipt(List<String> ids, String emailAddress);

	Boolean changeInvoiceTreatmentStatus(InvoiceItemChangeStatusRequest request);

	DoctorExpense addEditDoctorExpense(DoctorExpense request);

	List<DoctorExpense> getDoctorExpenses(String expenseType, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, String from, String to, String searchTerm, Boolean discarded,
			String paymentMode, String vendor);

	DoctorExpense deleteDoctorExpense(String expenseId, Boolean discarded);

	Double countDoctorExpenses(String expenseType, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String paymentMode);

	DoctorExpense getDoctorExpense(String expenseId);

	public ExpenseType addEditExpenseType(ExpenseType request);

	public ExpenseType getExpenseType(String expenseTypeId);

	public Boolean deleteExpenseType(String expenseTypeId, Boolean discarded);

	public List<ExpenseType> getExpenseType(int page, int size, String doctorId, String locationId, String hospitalId,
			String searchTerm, Boolean discarded);

	VendorExpense addEditVendor(VendorExpense request);

	VendorExpense discardVendor(String id, Boolean discarded);

	Integer countVendorExpense(Boolean discarded, String searchTerm);

	VendorExpense getVendorExpenseById(String id);

	List<VendorExpense> getVendors(int size, int page, String searchTerm, Boolean discarded, String doctorId,
			String locationId, String hospitalId);

	Boolean updateTotalDueAmount(DoctorAmountRequest request);

	int getDoctorExpensesCount(String expenseType, String doctorId, String locationId, String hospitalId,
			String updatedTime, String from, String to, String searchTerm, Boolean discarded, String paymentMode,
			String vendor);

}
