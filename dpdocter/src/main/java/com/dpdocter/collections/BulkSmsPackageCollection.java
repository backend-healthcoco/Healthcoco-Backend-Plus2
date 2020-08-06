package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bulk_sms_package_cl")
public class BulkSmsPackageCollection extends GenericCollection {

	@Id
	private ObjectId id;
	
	@Field
	private String packageName;
	@Field
	private Long price=0L;
	@Field
	private Long smsCredit=0L;
	@Field
	private Double costPerSms=0.0;
	

	@Field
	private Boolean discarded=false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
	
	
	
	
}
