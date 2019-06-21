package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceTax;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Tax;
import com.dpdocter.beans.User;
import com.dpdocter.response.DentalImagingInvoiceItemResponse;

public class DentalImagingInvoiceResponse extends GenericCollection {

	private String id;

	private String dentalImagingId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String patientName;

	private String mobileNumber;

	private String dentalImagingDoctorId;

	private String dentalImagingLocationId;

	private String dentalImagingHospitalId;

	private String uniqueInvoiceId;

	private String referringDoctor;

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

	private List<DentalImagingInvoiceItemResponse> invoiceItems;

	private Location dentalImagingLab;

	private User doctor;

	private Location location;

	private Boolean isPaid = false;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDentalImagingId() {
		return dentalImagingId;
	}

	public void setDentalImagingId(String dentalImagingId) {
		this.dentalImagingId = dentalImagingId;
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

	public String getDentalImagingLocationId() {
		return dentalImagingLocationId;
	}

	public void setDentalImagingLocationId(String dentalImagingLocationId) {
		this.dentalImagingLocationId = dentalImagingLocationId;
	}

	public String getDentalImagingHospitalId() {
		return dentalImagingHospitalId;
	}

	public void setDentalImagingHospitalId(String dentalImagingHospitalId) {
		this.dentalImagingHospitalId = dentalImagingHospitalId;
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

	public List<DentalImagingInvoiceItemResponse> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<DentalImagingInvoiceItemResponse> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Location getDentalImagingLab() {
		return dentalImagingLab;
	}

	public void setDentalImagingLab(Location dentalImagingLab) {
		this.dentalImagingLab = dentalImagingLab;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public String getDentalImagingDoctorId() {
		return dentalImagingDoctorId;
	}

	public void setDentalImagingDoctorId(String dentalImagingDoctorId) {
		this.dentalImagingDoctorId = dentalImagingDoctorId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DentalImagingInvoiceResponse [id=" + id + ", dentalImagingId=" + dentalImagingId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", dentalImagingDoctorId="
				+ dentalImagingDoctorId + ", dentalImagingLocationId=" + dentalImagingLocationId
				+ ", dentalImagingHospitalId=" + dentalImagingHospitalId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", referringDoctor=" + referringDoctor + ", totalDiscount=" + totalDiscount + ", totalCost="
				+ totalCost + ", totalTax=" + totalTax + ", invoiceTaxes=" + invoiceTaxes + ", grandTotal=" + grandTotal
				+ ", oldGrantTotal=" + oldGrantTotal + ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount="
				+ refundAmount + ", balanceAmount=" + balanceAmount + ", discarded=" + discarded + ", receiptIds="
				+ receiptIds + ", isTaxNotApplicable=" + isTaxNotApplicable + ", invoiceDate=" + invoiceDate
				+ ", invoiceItems=" + invoiceItems + ", dentalImagingLab=" + dentalImagingLab + ", doctor=" + doctor
				+ ", location=" + location + ", isPaid=" + isPaid + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
