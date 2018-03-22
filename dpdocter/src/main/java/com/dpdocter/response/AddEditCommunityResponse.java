package com.response;

public class AddEditCommunityResponse {
	
	private String Community;
	private String patientId;
	private String Id;
	
	
	public String getCommunity() {
		return Community;
	}
	public void setCommunity(String community) {
		Community = community;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	@Override
	public String toString() {
		return "AddEditCommunityRequest [Community=" + Community + ", patientId=" + patientId + ", Id=" + Id + "]";
	}
	

}
