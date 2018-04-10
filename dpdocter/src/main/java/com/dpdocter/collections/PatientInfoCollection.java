package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.Community;
import com.dpdocter.beans.FoodPreferences;
import com.dpdocter.beans.GeographicalArea;
import com.dpdocter.beans.LaptopUsage;
import com.dpdocter.beans.Meal;
import com.dpdocter.beans.MobilePhoneUsage;
import com.dpdocter.beans.PrimaryDetail;
import com.dpdocter.beans.Sleep;
import com.dpdocter.beans.TvUsage;
import com.dpdocter.beans.WorkHistory;

@Document(collection = "patient_info_cl")
public class PatientInfoCollection  {
	
	@Id
	private ObjectId id;
	
	@Field
	private ObjectId doctorId;
	
	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	@Field
	private ObjectId patientId;
	
	@Field
	private Activity activity;
	
	@Field
	private Community community;
	
	@Field
	private FoodPreferences foodPreferences;
	
	@Field
	private GeographicalArea geographicalArea;
	
	@Field
	private LaptopUsage laptopUsage;
	
	@Field
	private Meal meal;
	
	@Field
	private MobilePhoneUsage mobilePhoneUsage;
	
	@Field
	private PrimaryDetail primaryDetail;
	
	@Field
	private Sleep sleep;
	
	@Field
	private TvUsage tvUsage;
	
	@Field
	private WorkHistory workHistory;
	
	@Field
	private Boolean isPatientDiscarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public FoodPreferences getFoodPreferences() {
		return foodPreferences;
	}

	public void setFoodPreferences(FoodPreferences foodPreferences) {
		this.foodPreferences = foodPreferences;
	}

	public GeographicalArea getGeographicalArea() {
		return geographicalArea;
	}

	public void setGeographicalArea(GeographicalArea geographicalArea) {
		this.geographicalArea = geographicalArea;
	}

	public LaptopUsage getLaptopUsage() {
		return laptopUsage;
	}

	public void setLaptopUsage(LaptopUsage laptopUsage) {
		this.laptopUsage = laptopUsage;
	}

	public Meal getMeal() {
		return meal;
	}

	public void setMeal(Meal meal) {
		this.meal = meal;
	}

	public MobilePhoneUsage getMobilePhoneUsage() {
		return mobilePhoneUsage;
	}

	public void setMobilePhoneUsage(MobilePhoneUsage mobilePhoneUsage) {
		this.mobilePhoneUsage = mobilePhoneUsage;
	}

	public PrimaryDetail getPrimaryDetail() {
		return primaryDetail;
	}

	public void setPrimaryDetail(PrimaryDetail primaryDetail) {
		this.primaryDetail = primaryDetail;
	}

	public Sleep getSleep() {
		return sleep;
	}

	public void setSleep(Sleep sleep) {
		this.sleep = sleep;
	}

	public TvUsage getTvUsage() {
		return tvUsage;
	}

	public void setTvUsage(TvUsage tvUsage) {
		this.tvUsage = tvUsage;
	}

	public WorkHistory getWorkHistory() {
		return workHistory;
	}

	public void setWorkHistory(WorkHistory workHistory) {
		this.workHistory = workHistory;
	}

	
  
	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}
	
	
	

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PatientInfoCollection [id=" + id + ", doctorId=" + doctorId + ", patientId=" + patientId + ", activity="
				+ activity + ", community=" + community + ", foodPreferences=" + foodPreferences + ", geographicalArea="
				+ geographicalArea + ", laptopUsage=" + laptopUsage + ", meal=" + meal + ", mobilePhoneUsage="
				+ mobilePhoneUsage + ", primaryDetail=" + primaryDetail + ", sleep=" + sleep + ", tvUsage=" + tvUsage
				+ ", workHistory=" + workHistory + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

	


	

	

	
	

}
