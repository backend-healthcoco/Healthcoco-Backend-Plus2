package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class WorkHistory {
	
	private String Profession;
	private OffDays offDays; 
	private ArrayList<Date> WorkTo=new ArrayList<Date>();
	private ArrayList<Date> WorkFrom=new ArrayList<Date>();
	

	public OffDays getOffDays() {
		return offDays;
	}

	public void setOffDays(OffDays offDays) {
		this.offDays = offDays;
	}

	public String getProfession() {
		return Profession;
	}

	public void setProfession(String profession) {
		Profession = profession;
	}


	public ArrayList<Date> getWorkTo() {
		return WorkTo;
	}
	
	
	public void setWorkTo(ArrayList<Date> workTo) {
		this.WorkTo = workTo;
	}
	
	
	public ArrayList<Date> getWorkFrom() {
		return WorkFrom;
	}
	
	
	public void setWorkFrom(ArrayList<Date> workFrom) {
		this.WorkFrom = workFrom;
	}

	@Override
	public String toString() {
		return "WorkHistory [Profession=" + Profession + ", offDays=" + offDays + ", WorkTo=" + WorkTo + ", WorkFrom="
				+ WorkFrom + "]";
	}

	

}
