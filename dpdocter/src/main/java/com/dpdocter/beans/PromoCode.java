package com.dpdocter.beans;

public class PromoCode {

	private String id;
	private String promodCode;
	private Float promoPercentage = 0f;
	private Integer offeruptoLimit; // Limit in rupees
	private Boolean isDeleted = false;
	private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPromodCode() {
		return promodCode;
	}

	public void setPromodCode(String promodCode) {
		this.promodCode = promodCode;
	}

	public Float getPromoPercentage() {
		return promoPercentage;
	}

	public void setPromoPercentage(Float promoPercentage) {
		this.promoPercentage = promoPercentage;
	}

	public Integer getOfferuptoLimit() {
		return offeruptoLimit;
	}

	public void setOfferuptoLimit(Integer offeruptoLimit) {
		this.offeruptoLimit = offeruptoLimit;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
