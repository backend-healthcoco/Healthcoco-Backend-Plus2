package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ComponentType;

@Document(collection = "download_data_request_cl")
public class DownloadDataRequestCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private String emailAddress;

	@Field
	private List<ComponentType> dataType;

	@Field
	private String specialComments;

	@Field
	private Boolean isMailSend = false;

	@Field
	private Date mailSendTime;

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

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<ComponentType> getDataType() {
		return dataType;
	}

	public void setDataType(List<ComponentType> dataType) {
		this.dataType = dataType;
	}

	public String getSpecialComments() {
		return specialComments;
	}

	public void setSpecialComments(String specialComments) {
		this.specialComments = specialComments;
	}

	public Boolean getIsMailSend() {
		return isMailSend;
	}

	public void setIsMailSend(Boolean isMailSend) {
		this.isMailSend = isMailSend;
	}

	public Date getMailSendTime() {
		return mailSendTime;
	}

	public void setMailSendTime(Date mailSendTime) {
		this.mailSendTime = mailSendTime;
	}

	@Override
	public String toString() {
		return "DownloadDataRequestCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", emailAddress=" + emailAddress + ", dataType=" + dataType
				+ ", specialComments=" + specialComments + ", isMailSend=" + isMailSend + ", mailSendTime="
				+ mailSendTime + "]";
	}

}
