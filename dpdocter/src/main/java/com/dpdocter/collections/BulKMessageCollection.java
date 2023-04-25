package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class BulKMessageCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String requestId;

	@Field
	private Integer status;

	@Field
	private String desc;

	@Field
	private String mobileNumber;

	@Field
	private Date date;

	@Field
	private ObjectId userId;
	@Field
	private ObjectId doctorId;

	@Field
	private String senderId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "BulKMessageCollection [id=" + id + ", requestId=" + requestId + ", status=" + status + ", desc=" + desc
				+ ", mobileNumber=" + mobileNumber + ", date=" + date + ", senderId=" + senderId + "]";
	}

}
