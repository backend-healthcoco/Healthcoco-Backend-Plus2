package com.dpdocter.beans;

public class CareContextLink {

	private String accessToken;
	
	private CareContextPatient patient;
	private String hiType;
	private Integer count;

 
	public String getHiType() {
		return hiType;
	}

	public void setHiType(String hiType) {
		this.hiType = hiType;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public CareContextPatient getPatient() {
		return patient;
	}

	public void setPatient(CareContextPatient patient) {
		this.patient = patient;
	}
	
	
	
	
	
}
