package com.dpdocter.response;

import java.util.ArrayList;
import java.util.Date;

import com.dpdocter.enums.MealType;

public class AddEditMealResponse {
	
	private String patientId;
	private String Id;
	private MealType mealtype;
	private Date mealtime;
	private ArrayList<String> mealcontent = new ArrayList<String>();
	
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
	public MealType getMealtype() {
		return mealtype;
	}
	public void setMealtype(MealType mealtype) {
		this.mealtype = mealtype;
	}
	public Date getMealtime() {
		return mealtime;
	}
	public void setMealtime(Date mealtime) {
		this.mealtime = mealtime;
	}
	public ArrayList<String> getMealcontent() {
		return mealcontent;
	}
	public void setMealcontent(ArrayList<String> mealcontent) {
		this.mealcontent = mealcontent;
	}
	
	@Override
	public String toString() {
		return "AddEditMealRequest [patientId=" + patientId + ", Id=" + Id + ", mealtype=" + mealtype + ", mealtime="
				+ mealtime + ", mealcontent=" + mealcontent + "]";
	}
	
	
	

}
