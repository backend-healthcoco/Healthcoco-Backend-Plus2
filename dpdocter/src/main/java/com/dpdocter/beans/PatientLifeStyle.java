package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Day;
import com.dpdocter.enums.LifeStyleType;

public class PatientLifeStyle {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String assessmentId;

	private List<WorkingSchedule> workingschedules;

	private List<SleepPattern> sleepPatterns;

	private List<Day> offDays;

	private List<WorkingSchedule> trivalingPeriod;

	private Integer socialMediaTime;

	private SleepPattern tvViewTime;

	private SleepPattern loptopUseTime;

	private Boolean tvInBedRoom = false;

	private Boolean laptopInBedRoom = false;

	private Integer tvInBedRoomForMinute = 0;

	private Integer laptopInBedRoomForMinute = 0;

	private LifeStyleType type = LifeStyleType.MODERATE;

	private List<String> pregnancyCategory;
	
	private SleepPattern mobileUsage;

	public SleepPattern getMobileUsage() {
		return mobileUsage;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public void setMobileUsage(SleepPattern mobileUsage) {
		this.mobileUsage = mobileUsage;
	}

	public List<Day> getOffDays() {
		return offDays;
	}

	public void setOffDays(List<Day> offDays) {
		this.offDays = offDays;
	}

	public List<WorkingSchedule> getTrivalingPeriod() {
		return trivalingPeriod;
	}

	public void setTrivalingPeriod(List<WorkingSchedule> trivalingPeriod) {
		this.trivalingPeriod = trivalingPeriod;
	}

	public Integer getSocialMediaTime() {
		return socialMediaTime;
	}

	public void setSocialMediaTime(Integer socialMediaTime) {
		this.socialMediaTime = socialMediaTime;
	}

	public SleepPattern getTvViewTime() {
		return tvViewTime;
	}

	public void setTvViewTime(SleepPattern tvViewTime) {
		this.tvViewTime = tvViewTime;
	}

	public SleepPattern getLoptopUseTime() {
		return loptopUseTime;
	}

	public void setLoptopUseTime(SleepPattern loptopUseTime) {
		this.loptopUseTime = loptopUseTime;
	}

	public Boolean getTvInBedRoom() {
		return tvInBedRoom;
	}

	public void setTvInBedRoom(Boolean tvInBedRoom) {
		this.tvInBedRoom = tvInBedRoom;
	}

	public Boolean getLaptopInBedRoom() {
		return laptopInBedRoom;
	}

	public void setLaptopInBedRoom(Boolean laptopInBedRoom) {
		this.laptopInBedRoom = laptopInBedRoom;
	}

	public Integer getTvInBedRoomForMinute() {
		return tvInBedRoomForMinute;
	}

	public void setTvInBedRoomForMinute(Integer tvInBedRoomForMinute) {
		this.tvInBedRoomForMinute = tvInBedRoomForMinute;
	}

	public Integer getLaptopInBedRoomForMinute() {
		return laptopInBedRoomForMinute;
	}

	public void setLaptopInBedRoomForMinute(Integer laptopInBedRoomForMinute) {
		this.laptopInBedRoomForMinute = laptopInBedRoomForMinute;
	}

	public LifeStyleType getType() {
		return type;
	}

	public void setType(LifeStyleType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<WorkingSchedule> getWorkingschedules() {
		return workingschedules;
	}

	public void setWorkingschedules(List<WorkingSchedule> workingschedules) {
		this.workingschedules = workingschedules;
	}

	public List<SleepPattern> getSleepPatterns() {
		return sleepPatterns;
	}

	public void setSleepPatterns(List<SleepPattern> sleepPatterns) {
		this.sleepPatterns = sleepPatterns;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public List<String> getPregnancyCategory() {
		return pregnancyCategory;
	}

	public void setPregnancyCategory(List<String> pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}

	@Override
	public String toString() {
		return "PatientLifeStyle [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", assessmentId=" + assessmentId + ", workingschedules="
				+ workingschedules + ", sleepPatterns=" + sleepPatterns + ", offDays=" + offDays + ", trivalingPeriod="
				+ trivalingPeriod + ", socialMediaTime=" + socialMediaTime + ", tvViewTime=" + tvViewTime
				+ ", loptopUseTime=" + loptopUseTime + ", tvInBedRoom=" + tvInBedRoom + ", laptopInBedRoom="
				+ laptopInBedRoom + ", tvInBedRoomForMinute=" + tvInBedRoomForMinute + ", laptopInBedRoomForMinute="
				+ laptopInBedRoomForMinute + ", type=" + type + ", pregnancyCategory=" + pregnancyCategory
				+ ", mobileUsage=" + mobileUsage + "]";
	}
}
