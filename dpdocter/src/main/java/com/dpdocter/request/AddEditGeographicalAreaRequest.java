package com.dpdocter.request;

public class AddEditGeographicalAreaRequest {
	
	private String GeographicalArea;
	private String patientId;
	private String Id;
	
	
	public String getGeographicalArea() {
		return GeographicalArea;
	}
	public void setGeographicalArea(String geographicalArea) {
		GeographicalArea = geographicalArea;
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
		return "AddEditGeographicalAreaRequest [GeographicalArea=" + GeographicalArea + ", patientId=" + patientId
				+ ", Id=" + Id + "]";
	}
	
	
	
	
	
	

}
