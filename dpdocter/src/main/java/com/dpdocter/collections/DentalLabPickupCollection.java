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
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId dentalLabLocationId;
	@Field
	private ObjectId dentalLabHospitalId;
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
	@Field
	private String reasonForCancel;
	@Field
	private String cancelledBy;
	@Field
	private Integer feedBackRating;
	@Field
	private String feedBackComment;
	@Field
	private ObjectId invoiceId;
	@Field
	private String uniqueInvoiceId;
	@Field
	private Boolean isPatientDiscarded = false;
	
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

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
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

	public ObjectId getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(ObjectId dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public ObjectId getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(ObjectId dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DentalLabPickupCollection [id=" + id + ", patientId=" + patientId + ", patientName=" + patientName
				+ ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", age=" + age + ", dentalWorksSamples="
				+ dentalWorksSamples + ", crn=" + crn + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime
				+ ", status=" + status + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId="
				+ dentalLabHospitalId + ", dentalLabId=" + dentalLabId + ", discarded=" + discarded
				+ ", numberOfSamplesRequested=" + numberOfSamplesRequested + ", numberOfSamplesPicked="
				+ numberOfSamplesPicked + ", requestId=" + requestId + ", isAcceptedAtLab=" + isAcceptedAtLab
				+ ", isCollectedAtDoctor=" + isCollectedAtDoctor + ", isCompleted=" + isCompleted + ", collectionBoyId="
				+ collectionBoyId + ", serialNumber=" + serialNumber + ", reasonForCancel=" + reasonForCancel
				+ ", cancelledBy=" + cancelledBy + ", feedBackRating=" + feedBackRating + ", feedBackComment="
				+ feedBackComment + ", invoiceId=" + invoiceId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
