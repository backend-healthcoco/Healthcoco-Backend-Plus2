package com.dpdocter.response;

import java.util.Arrays;

public class AddEditFoodPreferencesResponse {
	
	
	private String patientId;
	private String Id;
	private String FoodPref[][];
	
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
	public String[][] getFoodPref() {
		return FoodPref;
	}
	public void setFoodPref(String[][] foodPref) {
		FoodPref = foodPref;
	}
	
	
	@Override
	public String toString() {
		return "AddEditFoodPreferences [patientId=" + patientId + ", Id=" + Id + ", FoodPref="
				+ Arrays.toString(FoodPref) + "]";
	}
	
	
	

}
