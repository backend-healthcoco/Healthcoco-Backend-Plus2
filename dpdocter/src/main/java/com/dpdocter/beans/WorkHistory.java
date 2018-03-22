package com.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class WorkHistory {
	
	private String Profession;
	private String OffDays[][]={{"Sun","false"},
			{"Mon","false"},
			{"Tue","false"},
			{"Wed","false"},
			{"Thu","false"},
			{"Fri","false"},
			{"Sat","false"}}; 
	
	private ArrayList<Date> WorkTo=new ArrayList<Date>();
	private ArrayList<Date> WorkFrom=new ArrayList<Date>();
	
	public String[][] getOffDays() {
		return OffDays;
	}
	
	public void setOffDays(String[][] offDays) {
		    this.OffDays = offDays;
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
		return "WorkHistory [Profession=" + Profession + ", OffDays=" + Arrays.toString(OffDays) + ", WorkTo=" + WorkTo
				+ ", WorkFrom=" + WorkFrom + "]";
	}
	

}
