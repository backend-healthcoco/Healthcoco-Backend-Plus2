package com.dpdocter.beans;

import java.util.Date;
import java.util.Set;

import com.dpdocter.collections.GenericCollection;

public class SubscriptionDetail extends GenericCollection {
	private String id;

	private Date fromDate = new Date();

	private Date toDate;

	private String doctorId;

	private Integer noOfsms = 0;

	private Set<String> locationIds;

	private Boolean isDemo = true;

	private Date smsFromDate = new Date();

	private Date smsToDate;

	private Integer monthsforSubscrption = 0;

	private Integer monthsforSms = 0;

	private Boolean isExpired = true;

	private String licenseId;

	public Integer getMonthsforSuscrption() {
		return monthsforSubscrption;
	}

	public void setMonthsforSuscrption(Integer monthsforSuscrption) {
		this.monthsforSubscrption = monthsforSuscrption;
	}

	public Integer getMonthsforSms() {
		return monthsforSms;
	}

	public void setMonthsforSms(Integer monthsforSms) {
		this.monthsforSms = monthsforSms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getNoOfsms() {
		return noOfsms;
	}

	public void setNoOfsms(Integer noOfsms) {
		this.noOfsms = noOfsms;
	}

	public Set<String> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(Set<String> locationIds) {
		this.locationIds = locationIds;
	}

	public Boolean getIsDemo() {
		return isDemo;
	}

	public void setIsDemo(Boolean isDemo) {
		this.isDemo = isDemo;
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

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public Integer getMonthsforSubscrption() {
		return monthsforSubscrption;
	}

	public void setMonthsforSubscrption(Integer monthsforSubscrption) {
		this.monthsforSubscrption = monthsforSubscrption;
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

}
