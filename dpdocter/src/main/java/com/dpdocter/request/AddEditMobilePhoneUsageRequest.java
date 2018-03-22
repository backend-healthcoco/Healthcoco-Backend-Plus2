package com.request;

import java.util.ArrayList;
import java.util.Date;

public class AddEditMobilePhoneUsageRequest {
	
	private String patientId;
	private String Id;
    private Integer hoursperday=0;
	private ArrayList<Date> TalkTo=new ArrayList<Date>();
	private ArrayList<Date> TalkFrom=new ArrayList<Date>();
	
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
	public Integer getHoursperday() {
		return hoursperday;
	}
	public void setHoursperday(Integer hoursperday) {
		this.hoursperday = hoursperday;
	}
	public ArrayList<Date> getTalkTo() {
		return TalkTo;
	}
	public void setTalkTo(ArrayList<Date> talkTo) {
		TalkTo = talkTo;
	}
	public ArrayList<Date> getTalkFrom() {
		return TalkFrom;
	}
	public void setTalkFrom(ArrayList<Date> talkFrom) {
		TalkFrom = talkFrom;
	}
	
	@Override
	public String toString() {
		return "AddEditMobilePhoneUsageRequest [patientId=" + patientId + ", Id=" + Id + ", hoursperday=" + hoursperday
				+ ", TalkTo=" + TalkTo + ", TalkFrom=" + TalkFrom + "]";
	}
		

}
