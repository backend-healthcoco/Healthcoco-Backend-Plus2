package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.GenderType;

public class ConsultationProblemDetails extends GenericCollection{

	private String id;
	
	private String doctorId;
	
	private String userId;
	
	private List<String>recordId;
	
	
	
	private String problemDetail;
	
	private Boolean discarded=false;
	
	private String transactionId;
	
	private GenderType gender;
	
	private Integer age;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<String> recordId) {
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
	
	

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "ConsultationProblemDetails [id=" + id + ", doctorId=" + doctorId + ", userId=" + userId + ", recordId="
				+ recordId + ", problemDetail=" + problemDetail + "]";
	}
	
	
	
}
