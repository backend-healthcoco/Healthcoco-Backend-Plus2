package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.PackageType;

public class PackageDetailObject {

	private String id;

	private PackageType packageName;

	private List<PackageAmountObject> packageAmount;

	private String advantages;

	private String noOfSms;

	private Boolean isDiscarded = Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}

	
	public List<PackageAmountObject> getPackageAmount() {
		return packageAmount;
	}

	public void setPackageAmount(List<PackageAmountObject> packageAmount) {
		this.packageAmount = packageAmount;
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

	

}
