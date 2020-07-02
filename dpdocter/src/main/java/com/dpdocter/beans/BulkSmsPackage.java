package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BulkSmsPackage extends GenericCollection{

	private String id;

	private String packageName;
	
	private String price;
	
	private String smsCredit;
	
	private String costPerSms;

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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSmsCredit() {
		return smsCredit;
	}

	public void setSmsCredit(String smsCredit) {
		this.smsCredit = smsCredit;
	}

	public String getCostPerSms() {
		return costPerSms;
	}

	public void setCostPerSms(String costPerSms) {
		this.costPerSms = costPerSms;
	}

	@Override
	public String toString() {
		return "BulkSmsPackage [id=" + id + ", packageName=" + packageName + ", price=" + price + ", smsCredit="
				+ smsCredit + ", costPerSms=" + costPerSms + "]";
	}
	
	

}
