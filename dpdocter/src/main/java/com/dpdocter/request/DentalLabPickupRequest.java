package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DentalWorksSample;

public class DentalLabPickupRequest {

	private String id;
	private String patientId;
	private String patientName;
	private String mobileNumber;
	private String gender;
	private Integer age;
	private List<DentalWorksSampleRequest> dentalWorksSamples;
	private String crn;
	private Long pickupTime;
	private Long deliveryTime;
	private String status;
	private String doctorId;
	private String dentalLabId;
	private Boolean discarded = false;
	private Integer numberOfSamplesRequested;
	private Integer numberOfSamplesPicked;
	private String requestId;
	private Boolean isAcceptedAtLab = false;
	private Boolean isCompleted = false;
	private Boolean isCollectedAtDoctor = false;
	private String collectionBoyId;
	private String serialNumber;
	private String reasonForCancel;
	private String cancelledBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public List<DentalWorksSampleRequest> getDentalWorksSamples() {
		return dentalWorksSamples;
	}

	public void setDentalWorksSamples(List<DentalWorksSampleRequest> dentalWorksSamples) {
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

	public Boolean getIsAcceptedAtLab() {
		return isAcceptedAtLab;
	}

	public void setIsAcceptedAtLab(Boolean isAcceptedAtLab) {
		this.isAcceptedAtLab = isAcceptedAtLab;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
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

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public Boolean getIsCollectedAtDoctor() {
		return isCollectedAtDoctor;
	}

	public void setIsCollectedAtDoctor(Boolean isCollectedAtDoctor) {
		this.isCollectedAtDoctor = isCollectedAtDoctor;
	}

	public String getReasonForCancel() {
		return reasonForCancel;
	}

	public void setReasonForCancel(String reasonForCancel) {
		this.reasonForCancel = reasonForCancel;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	@Override
	public String toString() {
		return "DentalLabPickupRequest [id=" + id + ", patientName=" + patientName + ", mobileNumber=" + mobileNumber
				+ ", dentalWorksSamples=" + dentalWorksSamples + ", crn=" + crn + ", pickupTime=" + pickupTime
				+ ", deliveryTime=" + deliveryTime + ", status=" + status + ", doctorId=" + doctorId + ", dentalLabId="
				+ dentalLabId + ", discarded=" + discarded + ", numberOfSamplesRequested=" + numberOfSamplesRequested
				+ ", numberOfSamplesPicked=" + numberOfSamplesPicked + ", requestId=" + requestId + ", isAcceptedAtLab="
				+ isAcceptedAtLab + ", isCompleted=" + isCompleted + ", collectionBoyId=" + collectionBoyId
				+ ", serialNumber=" + serialNumber + "]";
	}

}
