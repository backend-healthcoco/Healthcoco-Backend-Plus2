package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DentalWorksSample;

@Document(collection = "dental_lab_pickup_cl")
public class DentalLabPickupCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String patientName;
	@Field
	private String mobileNumber;
	@Field
	private String gender;
	@Field
	private Integer age;
	@Field
	private List<DentalWorksSample> dentalWorksSamples;
	@Field
	private String crn;
	@Field
	private Long pickupTime;
	@Field
	private Long deliveryTime;
	@Field
	private String status;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId dentalLabId;
	@Field
	private Boolean discarded = false;
	@Field
	private Integer numberOfSamplesRequested;
	@Field
	private Integer numberOfSamplesPicked;
	@Field
	private String requestId;
	@Field
	private Boolean isAcceptedAtLab = false;
	@Field
	private Boolean isCollectedAtDoctor = false;
	@Field
	private Boolean isCompleted = false;
	@Field
	private ObjectId collectionBoyId;
	@Field
	private String serialNumber;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<DentalWorksSample> getDentalWorksSamples() {
		return dentalWorksSamples;
	}

	public void setDentalWorksSamples(List<DentalWorksSample> dentalWorksSamples) {
		this.dentalWorksSamples = dentalWorksSamples;
	}

	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public Long getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Long pickupTime) {
		this.pickupTime = pickupTime;
	}

	public Long getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(ObjectId dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Integer getNumberOfSamplesRequested() {
		return numberOfSamplesRequested;
	}

	public void setNumberOfSamplesRequested(Integer numberOfSamplesRequested) {
		this.numberOfSamplesRequested = numberOfSamplesRequested;
	}

	public Integer getNumberOfSamplesPicked() {
		return numberOfSamplesPicked;
	}

	public void setNumberOfSamplesPicked(Integer numberOfSamplesPicked) {
		this.numberOfSamplesPicked = numberOfSamplesPicked;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Boolean getIsAcceptedAtLab() {
		return isAcceptedAtLab;
	}

	public void setIsAcceptedAtLab(Boolean isAcceptedAtLab) {
		this.isAcceptedAtLab = isAcceptedAtLab;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getIsCollectedAtDoctor() {
		return isCollectedAtDoctor;
	}

	public void setIsCollectedAtDoctor(Boolean isCollectedAtDoctor) {
		this.isCollectedAtDoctor = isCollectedAtDoctor;
	}

	@Override
	public String toString() {
		return "DentalLabPickupCollection [id=" + id + ", patientName=" + patientName + ", mobileNumber=" + mobileNumber
				+ ", gender=" + gender + ", age=" + age + ", dentalWorksSamples=" + dentalWorksSamples + ", crn=" + crn
				+ ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime + ", status=" + status + ", doctorId="
				+ doctorId + ", dentalLabId=" + dentalLabId + ", discarded=" + discarded + ", numberOfSamplesRequested="
				+ numberOfSamplesRequested + ", numberOfSamplesPicked=" + numberOfSamplesPicked + ", requestId="
				+ requestId + ", isAcceptedAtLab=" + isAcceptedAtLab + ", isCompleted=" + isCompleted
				+ ", collectionBoyId=" + collectionBoyId + ", serialNumber=" + serialNumber + "]";
	}

}
