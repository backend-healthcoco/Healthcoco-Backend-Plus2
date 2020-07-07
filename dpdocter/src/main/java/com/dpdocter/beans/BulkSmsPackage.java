package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BulkSmsPackage extends GenericCollection{

	private String id;

	private String packageName;
	
	private Long price;
	
	private Long smsCredit;
	
	private int costPerSms;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	
	
	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getSmsCredit() {
		return smsCredit;
	}

	public void setSmsCredit(Long smsCredit) {
		this.smsCredit = smsCredit;
	}

	public int getCostPerSms() {
		return costPerSms;
	}

	public void setCostPerSms(int costPerSms) {
		this.costPerSms = costPerSms;
	}

	@Override
	public String toString() {
		return "BulkSmsPackage [id=" + id + ", packageName=" + packageName + ", price=" + price + ", smsCredit="
				+ smsCredit + ", costPerSms=" + costPerSms + "]";
	}
	
	

}
