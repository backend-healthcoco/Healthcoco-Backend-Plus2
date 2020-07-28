
package com.dpdocter.request;

import com.dpdocter.beans.Discount;

public class OrderRequest {

	private Double amount = 0.0;

	private String currency = "INR";

	private Boolean paymentCapture = true;

	private String bulkSmsPackageId;
	
	private String doctorId;
	
	private String locationId;

	private Discount discount;

	private Double discountAmount = 0.0;

	private Double transferAmount = 0.0;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Boolean getPaymentCapture() {
		return paymentCapture;
	}

	public void setPaymentCapture(Boolean paymentCapture) {
		this.paymentCapture = paymentCapture;
	}

	

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Double getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(Double transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getBulkSmsPackageId() {
		return bulkSmsPackageId;
	}

	public void setBulkSmsPackageId(String bulkSmsPackageId) {
		this.bulkSmsPackageId = bulkSmsPackageId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	
	
	

}
