package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.DentalWorksInvoiceItemResponse;

public class DentalWorksInvoice extends GenericCollection{

	private String id;

	private String dentalWorksId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String patientName;

	private String mobileNumber;

	private String dentalLabLocationId;

	private String dentalLabHospitalId;

	private String uniqueInvoiceId;

	private Discount totalDiscount;

	private Double totalCost = 0.0;

	private Tax totalTax;

	private List<InvoiceTax> invoiceTaxes;

	private Double grandTotal = 0.0;

	private Double oldGrantTotal = this.grandTotal;

	private Double usedAdvanceAmount = 0.0;

	private Double refundAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Boolean discarded = false;

	private List<String> receiptIds;

	private Boolean isTaxNotApplicable = false;

	private Date invoiceDate;

	private List<DentalWorksInvoiceItemResponse> dentalWorksInvoiceItems;

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Double getOldGrantTotal() {
		return oldGrantTotal;
	}

	public void setOldGrantTotal(Double oldGrantTotal) {
		this.oldGrantTotal = oldGrantTotal;
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

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public List<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
	}

	public Boolean getIsTaxNotApplicable() {
		return isTaxNotApplicable;
	}

	public void setIsTaxNotApplicable(Boolean isTaxNotApplicable) {
		this.isTaxNotApplicable = isTaxNotApplicable;
	}

	public List<DentalWorksInvoiceItemResponse> getDentalWorksInvoiceItems() {
		return dentalWorksInvoiceItems;
	}

	public void setDentalWorksInvoiceItems(List<DentalWorksInvoiceItemResponse> dentalWorksInvoiceItems) {
		this.dentalWorksInvoiceItems = dentalWorksInvoiceItems;
	}

	public String getDentalWorksId() {
		return dentalWorksId;
	}

	public void setDentalWorksId(String dentalWorksId) {
		this.dentalWorksId = dentalWorksId;
	}

	@Override
	public String toString() {
		return "DentalWorksInvoice [id=" + id + ", dentalWorksId=" + dentalWorksId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", dentalLabLocationId="
				+ dentalLabLocationId + ", dentalLabHospitalId=" + dentalLabHospitalId + ", uniqueInvoiceId="
				+ uniqueInvoiceId + ", totalDiscount=" + totalDiscount + ", totalCost=" + totalCost + ", totalTax="
				+ totalTax + ", invoiceTaxes=" + invoiceTaxes + ", grandTotal=" + grandTotal + ", oldGrantTotal="
				+ oldGrantTotal + ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount
				+ ", balanceAmount=" + balanceAmount + ", discarded=" + discarded + ", receiptIds=" + receiptIds
				+ ", isTaxNotApplicable=" + isTaxNotApplicable + ", invoiceDate=" + invoiceDate
				+ ", dentalWorksInvoiceItems=" + dentalWorksInvoiceItems + "]";
	}

}
