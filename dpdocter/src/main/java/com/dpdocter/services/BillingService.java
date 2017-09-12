package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;

public interface BillingService {

	InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request);

	DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request);

	List<DoctorPatientInvoice> getInvoices(String type, int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded);

	DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded);

	DoctorPatientReceiptAddEditResponse addEditReceipt(DoctorPatientReceiptRequest request);

	List<DoctorPatientReceipt> getReceipts(int page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, Boolean discarded);

	DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded);

	Double getAvailableAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientInvoiceAndReceiptResponse addInvoiceAndPay(DoctorPatientInvoiceAndReceiptRequest request);

	InvoiceAndReceiptInitials getInitials(String locationId);

	Double getTotalDueAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientLedgerResponse getLedger(String doctorId, String locationId, String hospitalId, String patientId,
			String from, String to, int page, int size, String updatedTime);

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

}
