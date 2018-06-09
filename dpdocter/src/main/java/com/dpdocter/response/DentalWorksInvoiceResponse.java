package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceTax;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Tax;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;

public class DentalWorksInvoiceResponse extends GenericCollection {

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

	private User doctor;

	private Location clinic;

	private Location dentalLab;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDentalWorksId() {
		return dentalWorksId;
	}

	public void setDentalWorksId(String dentalWorksId) {
		this.dentalWorksId = dentalWorksId;
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

	public List<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
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

	public Boolean getIsTaxNotApplicable() {
		return isTaxNotApplicable;
	}

	public void setIsTaxNotApplicable(Boolean isTaxNotApplicable) {
		this.isTaxNotApplicable = isTaxNotApplicable;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public List<DentalWorksInvoiceItemResponse> getDentalWorksInvoiceItems() {
		return dentalWorksInvoiceItems;
	}

	public void setDentalWorksInvoiceItems(List<DentalWorksInvoiceItemResponse> dentalWorksInvoiceItems) {
		this.dentalWorksInvoiceItems = dentalWorksInvoiceItems;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getDentalLab() {
		return dentalLab;
	}

	public void setDentalLab(Location dentalLab) {
		this.dentalLab = dentalLab;
	}

	public Location getClinic() {
		return clinic;
	}

	public void setClinic(Location clinic) {
		this.clinic = clinic;
	}

}
