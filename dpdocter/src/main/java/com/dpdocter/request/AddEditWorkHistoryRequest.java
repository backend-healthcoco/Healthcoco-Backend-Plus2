package com.dpdocter.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AddEditWorkHistoryRequest {

	private String patientId;
	private String Id;
	private String Profession;
	private String OffDays[][];
	private ArrayList<Date> WorkTo=new ArrayList<Date>();
	private ArrayList<Date> WorkFrom=new ArrayList<Date>();
	
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
	public String getProfession() {
		return Profession;
	}
	public void setProfession(String profession) {
		Profession = profession;
	}
	public String[][] getOffDays() {
		return OffDays;
	}
	public void setOffDays(String[][] offDays) {
		OffDays = offDays;
	}
	public ArrayList<Date> getWorkTo() {
		return WorkTo;
	}
	public void setWorkTo(ArrayList<Date> workTo) {
		WorkTo = workTo;
	}
	public ArrayList<Date> getWorkFrom() {
		return WorkFrom;
	}
	public void setWorkFrom(ArrayList<Date> workFrom) {
		WorkFrom = workFrom;
	}
	
	@Override
	public String toString() {
		return "AddEditWorkHistoryRequest [patientId=" + patientId + ", Id=" + Id + ", Profession=" + Profession
				+ ", OffDays=" + Arrays.toString(OffDays) + ", WorkTo=" + WorkTo + ", WorkFrom=" + WorkFrom + "]";
	}
	
	
	
	
}
