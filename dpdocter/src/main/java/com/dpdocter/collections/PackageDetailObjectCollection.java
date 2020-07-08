package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PackageType;

@Document(collection = "package_detail_cl")
public class PackageDetailObjectCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private PackageType packageName;

	@Field
	private int discount = 0;

	@Field
	private int amount = 0;

	@Field
	private String duration;

	@Field
	private String advantages;

	@Field
	private String noOfSms;

	@Field
	private Boolean isDiscarded = Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getAdvantages() {
		return advantages;
	}

	public void setAdvantages(String advantages) {
		this.advantages = advantages;
	}

	public String getNoOfSms() {
		return noOfSms;
	}

	public void setNoOfSms(String noOfSms) {
		this.noOfSms = noOfSms;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	@Override
	public String toString() {
		return "PackageDetailObjectCollection [id=" + id + ", packageName=" + packageName + ", discount=" + discount
				+ ", amount=" + amount + ", duration=" + duration + ", advantages=" + advantages + ", noOfSms="
				+ noOfSms + ", isDiscarded=" + isDiscarded + "]";
	}

}
