package com.dpdocter.services;

import java.util.List;

import com.dpdocter.DoctorPatientReceiptRequest;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.webservices.DoctorPatientInvoiceAndReceiptRequest;

public interface BillingService {

	InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request);

	DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request);

	List<DoctorPatientInvoice> getInvoices(String type, int page, int size, String doctorId, String locationId, String hospitalId, String patientId, String updatedTime, Boolean discarded);

	DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded);

	DoctorPatientReceipt addEditReceipt(DoctorPatientReceiptRequest request);

	List<DoctorPatientReceipt> getReceipts(int page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, Boolean discarded);

	DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded);

	Double getAvailableAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId);

	DoctorPatientInvoiceAndReceiptResponse addInvoiceAndPay(DoctorPatientInvoiceAndReceiptRequest request);

	InvoiceAndReceiptInitials getInitials(String locationId);

}
