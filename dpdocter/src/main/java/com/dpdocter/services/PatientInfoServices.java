package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.Community;
import com.dpdocter.beans.FoodPreferences;
import com.dpdocter.beans.GeographicalArea;
import com.dpdocter.beans.LaptopUsage;
import com.dpdocter.beans.Meal;
import com.dpdocter.beans.MobilePhoneUsage;
import com.dpdocter.beans.PatientInfo;
import com.dpdocter.beans.PrimaryDetail;
import com.dpdocter.beans.Sleep;
import com.dpdocter.beans.TvUsage;
import com.dpdocter.beans.WorkHistory;
import com.dpdocter.collections.PatientInfoCollection;

public interface PatientInfoServices {
	
	PatientInfo addPatient(PatientInfo request);
	
	
	Activity updateActivity(PatientInfo request);
	
	Activity getActivity(String request);
	
	
	Community updateCommunity(PatientInfo request);
	
	Community getCommunity(String request);
	
	
	FoodPreferences updateFoodPreferences(PatientInfo request);
	
	FoodPreferences getFoodPreferences(String request);
	
	
	GeographicalArea updateGeographicalArea(PatientInfo request);
	
	GeographicalArea getGeographicalArea(String request);
	
	
	LaptopUsage updateLaptopUsage(PatientInfo request);
	
	LaptopUsage getLaptopUsage(String request);
	
	
	Meal updateMeal(PatientInfo request);
	
	Meal getMeal(String request);
	
	
	MobilePhoneUsage updateMobilePhoneUsage(PatientInfo request);
	
	MobilePhoneUsage getMobilePhoneUsage(String request);
	
	
	PrimaryDetail updatePrimaryDetail(PatientInfo request);
	
	PrimaryDetail getPrimaryDetail(String request);
	
	
    Sleep updateSleep(PatientInfo request);
	
	Sleep getSleep(String request);
	
	
	TvUsage updateTvUsage(PatientInfo request);
	
	TvUsage getTvUsage(String request);
	
	
	WorkHistory updateWorkHistory(PatientInfo request);
	
	WorkHistory getWorkHistory(String request);
	
	Boolean deletePatient(PatientInfo request);

	PatientInfo findById(String patientInfoId);
	
	List<PatientInfo> findAll(String doctorId);


}
