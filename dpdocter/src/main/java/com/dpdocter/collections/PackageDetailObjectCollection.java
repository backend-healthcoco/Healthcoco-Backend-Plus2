package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PackageAmountObject;
import com.dpdocter.enums.PackageType;

@Document(collection = "package_detail_cl")
public class PackageDetailObjectCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private PackageType packageName;

	@Field
	private List<PackageAmountObject> packageAmount;

	@Field
	private int amount ;

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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public List<PackageAmountObject> getPackageAmount() {
		return packageAmount;
	}

	public void setPackageAmount(List<PackageAmountObject> packageAmount) {
		this.packageAmount = packageAmount;
	}

	

}
