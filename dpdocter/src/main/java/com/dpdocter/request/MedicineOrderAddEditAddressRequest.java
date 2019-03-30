package com.dpdocter.request;

import com.dpdocter.beans.UserAddress;

public class MedicineOrderAddEditAddressRequest {

	private String id;
	private UserAddress shippingAddress;
	private UserAddress billingAddress;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(UserAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public UserAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(UserAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	
}
