package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Fields;

@Document(collection = "consent_form_cl")
public class ConsentFormCollection extends GenericCollection {
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
	private String locationName;

	@Field
	private String PID;

	@Field
	private String formId;

	@Field
	private String localPatientName;

	@Field
	private String emailAddress;

	@Field
	private String gender;

	@Field
	private DOB dob;

	@Field
	private String address;

	@Field
	private String mobileNumber;

	@Field
	private String landLineNumber;

	@Field
	private String bloodGroup;

	@Field
	private String declaration;

	@Field
	private String title;

	@Field
	private Date dateOfSign;

	@Field
	private String signImageURL;

	@Field
	private String medicalHistory;

	@Field
	private Boolean discarded = false;

	@Field
	private ObjectId templateId;
	
	@Field
	private List<Fields> inputElements;
	
	@Field
	private String type;
	
	@Field
	private String templateHtmlText;
	
	@Field
	private String PNUM;
	
	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public String getFormId() {
		return formId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFormId(String formId) {
		this.formId = formId;
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

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLandLineNumber() {
		return landLineNumber;
	}

	public void setLandLineNumber(String landLineNumber) {
		this.landLineNumber = landLineNumber;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public Date getDateOfSign() {
		return dateOfSign;
	}

	public void setDateOfSign(Date dateOfSign) {
		this.dateOfSign = dateOfSign;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getSignImageURL() {
		return signImageURL;
	}

	public void setSignImageURL(String signImageURL) {
		this.signImageURL = signImageURL;
	}

	public String getMedicalHistory() {
		return medicalHistory;
	}

	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public ObjectId getTemplateId() {
		return templateId;
	}

	public void setTemplateId(ObjectId templateId) {
		this.templateId = templateId;
	}

	public List<Fields> getInputElements() {
		return inputElements;
	}

	public void setInputElements(List<Fields> inputElements) {
		this.inputElements = inputElements;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTemplateHtmlText() {
		return templateHtmlText;
	}

	public void setTemplateHtmlText(String templateHtmlText) {
		this.templateHtmlText = templateHtmlText;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "ConsentFormCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", locationName=" + locationName
				+ ", PID=" + PID + ", formId=" + formId + ", localPatientName=" + localPatientName + ", emailAddress="
				+ emailAddress + ", gender=" + gender + ", dob=" + dob + ", address=" + address + ", mobileNumber="
				+ mobileNumber + ", landLineNumber=" + landLineNumber + ", bloodGroup=" + bloodGroup + ", declaration="
				+ declaration + ", title=" + title + ", dateOfSign=" + dateOfSign + ", signImageURL=" + signImageURL
				+ ", medicalHistory=" + medicalHistory + ", discarded=" + discarded + ", templateId=" + templateId
				+ ", inputElements=" + inputElements + ", type=" + type + ", templateHtmlText=" + templateHtmlText
				+ ", PNUM=" + PNUM + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
