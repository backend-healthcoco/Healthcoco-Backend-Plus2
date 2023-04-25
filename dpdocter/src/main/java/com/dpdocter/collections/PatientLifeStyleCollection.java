package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.SleepPattern;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.LifeStyleType;

@Document(collection = "patient_life_style_cl")
public class PatientLifeStyleCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId assessmentId;
	@Field
	private List<WorkingSchedule> workingschedules;
	@Field
	private List<SleepPattern> sleepPatterns;
	@Field
	private List<Day> offDays;
	@Field
	private List<WorkingSchedule> trivalingPeriod;
	@Field
	private Integer socialMediaTime;
	@Field
	private SleepPattern tvViewTime;
	@Field
	private SleepPattern loptopUseTime;
	@Field
	private Boolean tvInBedRoom = false;
	@Field
	private Boolean laptopInBedRoom = false;
	@Field
	private Integer tvInBedRoomForMinute = 0;
	@Field
	private Integer laptopInBedRoomForMinute = 0;
	@Field
	private LifeStyleType type;

	@Field
	private List<String> pregnancyCategory;

	@Field
	private SleepPattern mobileUsage;

	@Field
	private Boolean isPatientDiscarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public ObjectId getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(ObjectId assessmentId) {
		this.assessmentId = assessmentId;
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

	public SleepPattern getMobileUsage() {
		return mobileUsage;
	}

	public void setMobileUsage(SleepPattern mobileUsage) {
		this.mobileUsage = mobileUsage;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public List<String> getPregnancyCategory() {
		return pregnancyCategory;
	}

	public void setPregnancyCategory(List<String> pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}

	@Override
	public String toString() {
		return "PatientLifeStyleCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", assessmentId=" + assessmentId
				+ ", workingschedules=" + workingschedules + ", sleepPatterns=" + sleepPatterns + ", offDays=" + offDays
				+ ", trivalingPeriod=" + trivalingPeriod + ", socialMediaTime=" + socialMediaTime + ", tvViewTime="
				+ tvViewTime + ", loptopUseTime=" + loptopUseTime + ", tvInBedRoom=" + tvInBedRoom
				+ ", laptopInBedRoom=" + laptopInBedRoom + ", tvInBedRoomForMinute=" + tvInBedRoomForMinute
				+ ", laptopInBedRoomForMinute=" + laptopInBedRoomForMinute + ", type=" + type + ", pregnancyCategory="
				+ pregnancyCategory + ", mobileUsage=" + mobileUsage + ", isPatientDiscarded=" + isPatientDiscarded
				+ "]";
	}
}
