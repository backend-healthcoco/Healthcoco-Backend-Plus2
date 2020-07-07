package com.dpdocter.beans;

import com.dpdocter.enums.PackageType;

public class PackageDetailObject {

	private String id;

	private PackageType packageName;

	private Discount discount;

	private Double amount = 0.0;
	
	private String duration;
	
	private String advantages;
	
	private String noOfSms;

	private Boolean isDiscarded=Boolean.FALSE;

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

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
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
		return "PackageDetailObject [id=" + id + ", packageName=" + packageName + ", discount=" + discount + ", amount="
				+ amount + ", duration=" + duration + ", advantages=" + advantages + ", noOfSms=" + noOfSms
				+ ", isDiscarded=" + isDiscarded + "]";
	}

}
