package com.dpdocter.request;

import com.dpdocter.beans.Address;

public class MedicinOrderAddEditAddressRequest {

	private String id;
	private Address shippingAddress;
	private Address billingAddress;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

}
