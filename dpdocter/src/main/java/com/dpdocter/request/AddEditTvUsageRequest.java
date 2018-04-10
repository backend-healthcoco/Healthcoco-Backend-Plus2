package com.dpdocter.request;

import java.util.ArrayList;
import java.util.Date;

public class AddEditTvUsageRequest {

	
	private Integer hoursperday=0;	
	private boolean tv_in_bedroom;
	private ArrayList<Date> WatchTo=new ArrayList<Date>();
	private ArrayList<Date> WatchFrom=new ArrayList<Date>();
	private String patientId;
	private String Id;
	
	
	public Integer getHoursperday() {
		return hoursperday;
	}
	public void setHoursperday(Integer hoursperday) {
		this.hoursperday = hoursperday;
	}
	public boolean isTv_in_bedroom() {
		return tv_in_bedroom;
	}
	public void setTv_in_bedroom(boolean tv_in_bedroom) {
		this.tv_in_bedroom = tv_in_bedroom;
	}
	public ArrayList<Date> getWatchTo() {
		return WatchTo;
	}
	public void setWatchTo(ArrayList<Date> watchTo) {
		WatchTo = watchTo;
	}
	public ArrayList<Date> getWatchFrom() {
		return WatchFrom;
	}
	public void setWatchFrom(ArrayList<Date> watchFrom) {
		WatchFrom = watchFrom;
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
		return "AddEditTvUsageRequest [hoursperday=" + hoursperday + ", tv_in_bedroom=" + tv_in_bedroom + ", WatchTo="
				+ WatchTo + ", WatchFrom=" + WatchFrom + ", patientId=" + patientId + ", Id=" + Id + "]";
	}
	
	

	
	
}
