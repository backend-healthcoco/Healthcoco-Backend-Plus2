package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bulk_sms_package_cl")
public class BulkSmsPackageCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private String packageName;
	@Field
	private String price;
	@Field
	private String smsCredit;
	@Field
	private String costPerSms;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ObjectId getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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
	
	

}
