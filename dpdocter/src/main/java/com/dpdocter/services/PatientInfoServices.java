package com.repository;

import com.bean.Activity;
import com.bean.Community;
import com.bean.FoodPreferences;
import com.bean.GeographicalArea;
import com.bean.LaptopUsage;
import com.bean.Meal;
import com.bean.MobilePhoneUsage;
import com.bean.PatientInfo;
import com.bean.PrimaryDetail;
import com.bean.Sleep;
import com.bean.TvUsage;
import com.bean.WorkHistory;
import com.collection.PatientInfoCollection;

public interface PatientInfoServices {
	
	
	Activity updateActivity(PatientInfo request);
	
	Activity getActivity(PatientInfo request);
	
	
	Community updateCommunity(PatientInfo request);
	
	Community getCommunity(PatientInfo request);
	
	
	FoodPreferences updateFoodPreferences(PatientInfo request);
	
	FoodPreferences getFoodPreferences(PatientInfo request);
	
	
	GeographicalArea updateGeographicalArea(PatientInfo request);
	
	GeographicalArea getGeographicalArea(PatientInfo request);
	
	
	LaptopUsage updateLaptopUsage(PatientInfo request);
	
	LaptopUsage getLaptopUsage(PatientInfo request);
	
	
	Meal updateMeal(PatientInfo request);
	
	Meal getMeal(PatientInfo request);
	
	
	MobilePhoneUsage updateMobilePhoneUsage(PatientInfo request);
	
	MobilePhoneUsage getMobilePhoneUsage(PatientInfo request);
	
	
	PrimaryDetail updatePrimaryDetail(PatientInfo request);
	
	PrimaryDetail getPrimaryDetail(PatientInfo request);
	
	
    Sleep updateSleep(PatientInfo request);
	
	Sleep getSleep(PatientInfo request);
	
	
	TvUsage updateTvUsage(PatientInfo request);
	
	TvUsage getTvUsage(PatientInfo request);
	
	
	WorkHistory updateWorkHistory(PatientInfo request);
	
	WorkHistory getWorkHistory(PatientInfo request);
	
	Boolean deletePatient(PatientInfo request);


}
