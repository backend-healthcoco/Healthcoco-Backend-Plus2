package com.collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bean.Activity;
import com.bean.Community;
import com.bean.FoodPreferences;
import com.bean.GeographicalArea;
import com.bean.LaptopUsage;
import com.bean.Meal;
import com.bean.MobilePhoneUsage;
import com.bean.PrimaryDetail;
import com.bean.Sleep;
import com.bean.TvUsage;
import com.bean.WorkHistory;

@Document(collection = "patient_info_cl")
public class PatientInfoCollection  {
	
	@Id
	private ObjectId id;
	
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
		return "PatientInfoCollection [id=" + id + ", patientID=" + patientId + ", activity=" + activity
				+ ", community=" + community + ", foodPreferences=" + foodPreferences + ", geographicalArea="
				+ geographicalArea + ", laptopUsage=" + laptopUsage + ", meal=" + meal + ", mobilePhoneUsage="
				+ mobilePhoneUsage + ", primaryDetail=" + primaryDetail + ", sleep=" + sleep + ", tvUsage=" + tvUsage
				+ ", workHistory=" + workHistory + "]";
	}

	

	
	

}
