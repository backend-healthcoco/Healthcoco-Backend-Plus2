package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "internal_promo_code_cl")
public class InternalPromoCodeCollection extends GenericCollection {

	private ObjectId id;
	private String mobileNumber;
	private String promoCode;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
		return "InternalPromoCodeCollection [id=" + id + ", mobileNumber=" + mobileNumber + ", promoCode=" + promoCode
				+ "]";
	}

}
