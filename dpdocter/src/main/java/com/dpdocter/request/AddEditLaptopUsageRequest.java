package com.dpdocter.request;

public class AddEditLaptopUsageRequest {
	
	private Integer hoursperday;
	private Boolean LaptopInBedroom;
	private String patientId;
	private String Id;
	
	public Integer getHoursperday() {
		return hoursperday;
	}
	public void setHoursperday(Integer hoursperday) {
		this.hoursperday = hoursperday;
	}
	public Boolean getLaptopInBedroom() {
		return LaptopInBedroom;
	}
	public void setLaptopInBedroom(Boolean laptopInBedroom) {
		LaptopInBedroom = laptopInBedroom;
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
		return "AddEditLaptopUsageRequest [hoursperday=" + hoursperday + ", LaptopInBedroom=" + LaptopInBedroom
				+ ", patientId=" + patientId + ", Id=" + Id + "]";
	}
	
	
	
	

}
