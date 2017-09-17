package com.dpdocter.beans;

public class InternalPromoCode {

	private String id;
	private String mobileNumber;
	private String promoCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	@Override
	public String toString() {
		return "InternalPromoCode [id=" + id + ", mobileNumber=" + mobileNumber + ", promoCode=" + promoCode + "]";
	}

}
