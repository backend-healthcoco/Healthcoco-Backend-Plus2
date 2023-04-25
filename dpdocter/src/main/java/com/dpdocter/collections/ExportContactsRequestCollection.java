package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ExportRequestData;

@Document(collection = "export_contacts_request_cl")
public class ExportContactsRequestCollection {
	@Field
	private ObjectId doctorId;

	@Field
	private String emailAddress;

	@Field
	private List<ExportRequestData> dataType;

	@Field
	private String specialComments;

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<ExportRequestData> getDataType() {
		return dataType;
	}

	public void setDataType(List<ExportRequestData> dataType) {
		this.dataType = dataType;
	}

	public String getSpecialComments() {
		return specialComments;
	}

	public void setSpecialComments(String specialComments) {
		this.specialComments = specialComments;
	}

	@Override
	public String toString() {
		return "ExportContactsRequestCollection [doctorId=" + doctorId + ", emailAddress=" + emailAddress
				+ ", dataType=" + dataType + ", specialComments=" + specialComments + "]";
	}

}
