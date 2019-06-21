package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.DentalWorksSampleRequest;

public class DentalLabPickupResponse extends GenericCollection {

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
	private String locationId;
	private String hospitalId;
	private String dentalLabLocationId;
	private String dentalLabHospitalId;
	private Boolean discarded = false;
	private Integer numberOfSamplesRequested;
	private Integer numberOfSamplesPicked;
	private String requestId;
	private Boolean isAcceptedAtLab = false;
	private Boolean isCompleted = false;
	private String collectionBoyId;
	private Boolean isCollectedAtDoctor = false;
	private String serialNumber;
	private Location dentalLab;
	private User doctor;
	private CollectionBoy collectionBoy;
	private String reasonForCancel;
	private String cancelledBy;
	private Integer feedBackRating;
	private String feedBackComment;
	private String invoiceId;
	private String uniqueInvoiceId;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public Location getDentalLab() {
		return dentalLab;
	}

	public void setDentalLab(Location dentalLab) {
		this.dentalLab = dentalLab;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public CollectionBoy getCollectionBoy() {
		return collectionBoy;
	}

	public void setCollectionBoy(CollectionBoy collectionBoy) {
		this.collectionBoy = collectionBoy;
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

	public Integer getFeedBackRating() {
		return feedBackRating;
	}

	public void setFeedBackRating(Integer feedBackRating) {
		this.feedBackRating = feedBackRating;
	}

	public String getFeedBackComment() {
		return feedBackComment;
	}

	public void setFeedBackComment(String feedBackComment) {
		this.feedBackComment = feedBackComment;
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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	@Override
	public String toString() {
		return "DentalLabPickupResponse [id=" + id + ", patientId=" + patientId + ", patientName=" + patientName
				+ ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", age=" + age + ", dentalWorksSamples="
				+ dentalWorksSamples + ", crn=" + crn + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime
				+ ", status=" + status + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId="
				+ dentalLabHospitalId + ", discarded=" + discarded + ", numberOfSamplesRequested="
				+ numberOfSamplesRequested + ", numberOfSamplesPicked=" + numberOfSamplesPicked + ", requestId="
				+ requestId + ", isAcceptedAtLab=" + isAcceptedAtLab + ", isCompleted=" + isCompleted
				+ ", collectionBoyId=" + collectionBoyId + ", isCollectedAtDoctor=" + isCollectedAtDoctor
				+ ", serialNumber=" + serialNumber + ", dentalLab=" + dentalLab + ", doctor=" + doctor
				+ ", collectionBoy=" + collectionBoy + ", reasonForCancel=" + reasonForCancel + ", cancelledBy="
				+ cancelledBy + ", feedBackRating=" + feedBackRating + ", feedBackComment=" + feedBackComment
				+ ", invoiceId=" + invoiceId + ", uniqueInvoiceId=" + uniqueInvoiceId + "]";
	}

}
