package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "consultation_problem_detail_cl")
public class ConsultationProblemDetailsCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private ObjectId doctorId;
	
	@Field
	private ObjectId userId;
	
	@Field
	private ObjectId appointmentId;
	
	@Field
	private List<ObjectId>recordId;
	
	@Field
	private String problemDetail;
	
	@Field
	private Boolean discarded=false;

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

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(ObjectId appointmentId) {
		this.appointmentId = appointmentId;
	}

	public List<ObjectId> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<ObjectId> recordId) {
		this.recordId = recordId;
	}

	public String getProblemDetail() {
		return problemDetail;
	}

	public void setProblemDetail(String problemDetail) {
		this.problemDetail = problemDetail;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	
}
