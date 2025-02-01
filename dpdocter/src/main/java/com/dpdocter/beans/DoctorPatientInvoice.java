package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.InvoiceItemResponse;

public class DoctorPatientInvoice extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String uniqueInvoiceId;

	private List<InvoiceItemResponse> invoiceItems;

	private Discount totalDiscount;

	private double totalCost = 0.0;

	private Tax totalTax;

	private Double grandTotal = 0.0;

	private Double oldGrantTotal = this.grandTotal;

	private Double usedAdvanceAmount = 0.0;

	private Double refundAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Boolean discarded = false;

	private List<String> receiptIds;

	private Date invoiceDate;

	private String timeOfAdmission;

	private String timeOfDischarge;

	private Date admissionDate;

	private Date dischargeDate;
	private Boolean isCghsInvoice = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public List<InvoiceItemResponse> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItemResponse> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
		this.oldGrantTotal = grandTotal;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getReceiptIds() {
		return receiptIds;
	}

	public void setReceiptIds(List<String> receiptIds) {
		this.receiptIds = receiptIds;
	}

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Double getOldGrantTotal() {
		return oldGrantTotal;
	}

	public void setOldGrantTotal(Double oldGrantTotal) {
		this.oldGrantTotal = oldGrantTotal;
	}

	public String getTimeOfAdmission() {
		return timeOfAdmission;
	}

	public void setTimeOfAdmission(String timeOfAdmission) {
		this.timeOfAdmission = timeOfAdmission;
	}

	public String getTimeOfDischarge() {
		return timeOfDischarge;
	}

	public void setTimeOfDischarge(String timeOfDischarge) {
		this.timeOfDischarge = timeOfDischarge;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public Boolean getIsCghsInvoice() {
		return isCghsInvoice;
	}

	public void setIsCghsInvoice(Boolean isCghsInvoice) {
		this.isCghsInvoice = isCghsInvoice;
	}

	@Override
	public String toString() {
		return "DoctorPatientInvoice [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", invoiceItems=" + invoiceItems + ", totalDiscount=" + totalDiscount + ", totalCost=" + totalCost
				+ ", totalTax=" + totalTax + ", grandTotal=" + grandTotal + ", oldGrantTotal=" + oldGrantTotal
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount="
				+ balanceAmount + ", discarded=" + discarded + ", receiptIds=" + receiptIds + ", invoiceDate="
				+ invoiceDate + "]";
	}

}
