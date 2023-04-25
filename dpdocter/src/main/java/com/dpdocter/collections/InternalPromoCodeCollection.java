package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "internal_promo_code_cl")
public class InternalPromoCodeCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String mobileNumber;
	@Field
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
