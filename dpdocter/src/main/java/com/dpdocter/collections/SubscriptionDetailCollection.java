package com.dpdocter.collections;

import java.util.Date;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "subscription_detail_cl")
public class SubscriptionDetailCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private Date fromDate = new Date();

	@Field
	private Date toDate;

	@Field
	private ObjectId doctorId;

	@Field
	private Integer noOfsms = 0;

	@Field
	private Set<ObjectId> locationIds;

	@Field
	private Boolean isDemo = true;

	@Field
	private Date smsFromDate = new Date();

	@Field
	private Date smsToDate;

	@Field
	private Boolean isExpired = true;

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public ObjectId getId() {
		return id;
	}

	public Date getSmsFromDate() {
		return smsFromDate;
	}

	public void setSmsFromDate(Date smsFromDate) {
		this.smsFromDate = smsFromDate;
	}

	public Date getSmsToDate() {
		return smsToDate;
	}

	public void setSmsToDate(Date smsToDate) {
		this.smsToDate = smsToDate;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getNoOfsms() {
		return noOfsms;
	}

	public void setNoOfsms(Integer noOfsms) {
		this.noOfsms = noOfsms;
	}

	public Set<ObjectId> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(Set<ObjectId> locationIds) {
		this.locationIds = locationIds;
	}

	public Boolean getIsDemo() {
		return isDemo;
	}

	public void setIsDemo(Boolean isDemo) {
		this.isDemo = isDemo;
	}

}
