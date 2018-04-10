package com.dpdocter.beans;

public class PatientInfo {
	
	
	private String id;
	
	
	private String patientId;
	
	
	private Activity activity;
	
	
	private Community community;
	
	
	private FoodPreferences foodPreferences;
	
	
	private GeographicalArea geographicalArea;
	
	
	private LaptopUsage laptopUsage;
	
	
	private Meal meal;
	

	private MobilePhoneUsage mobilePhoneUsage;
	
	
	private PrimaryDetail primaryDetail;
	
	
	private Sleep sleep;
	
	
	private TvUsage tvUsage;
	
	
	private WorkHistory workHistory;
	
 	private Boolean isPatientDiscarded = false;
 	
 	private String doctorId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	
  
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "PatientInfo [id=" + id + ", patientId=" + patientId + ", activity=" + activity + ", community="
				+ community + ", foodPreferences=" + foodPreferences + ", geographicalArea=" + geographicalArea
				+ ", laptopUsage=" + laptopUsage + ", meal=" + meal + ", mobilePhoneUsage=" + mobilePhoneUsage
				+ ", primaryDetail=" + primaryDetail + ", sleep=" + sleep + ", tvUsage=" + tvUsage + ", workHistory="
				+ workHistory + ", isPatientDiscarded=" + isPatientDiscarded + ", doctorId=" + doctorId + "]";
	}

	

		

}
