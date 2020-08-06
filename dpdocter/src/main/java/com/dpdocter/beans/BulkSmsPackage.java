package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BulkSmsPackage extends GenericCollection {

	private String id;

	private String packageName;
	
	private Long price=0L;
	
	private Long smsCredit=0L;
	

	private Double costPerSms=0.0;

	
	private Boolean discarded=false;

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

	
	

	

	public Double getCostPerSms() {
		return costPerSms;
	}

	public void setCostPerSms(Double costPerSms) {
		this.costPerSms = costPerSms;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "BulkSmsPackage [id=" + id + ", packageName=" + packageName + ", price="
				+ price + ", smsCredit=" + smsCredit + ", costPerSms=" + costPerSms + "]";
	}
	
	
	
}
