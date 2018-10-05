package com.dpdocter.response;

import org.bson.types.ObjectId;

import com.dpdocter.beans.User;

public class PatientGroupLookupResponse {

	private ObjectId id;

	private ObjectId groupId;

	private ObjectId patientId;

	private User user;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getGroupId() {
		return groupId;
	}

	public void setGroupId(ObjectId groupId) {
		this.groupId = groupId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "PatientGroupLookupResponse [id=" + id + ", groupId=" + groupId + ", patientId=" + patientId + ", user="
				+ user + "]";
	}

}
