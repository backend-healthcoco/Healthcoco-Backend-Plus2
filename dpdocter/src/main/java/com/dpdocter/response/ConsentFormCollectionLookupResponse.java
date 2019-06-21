package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Fields;
import com.dpdocter.collections.CertificateTemplateCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;

public class ConsentFormCollectionLookupResponse extends GenericCollection{

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String locationName;

	private String PID;

	private String formId;

	private String localPatientName;

	private String emailAddress;

	private String gender;

	private DOB dob;

	private String address;

	private String mobileNumber;

	private String landLineNumber;

	private String bloodGroup;

	private String declaration;

	private String title;

	private Date dateOfSign;

	private String signImageURL;

	private String medicalHistory;

	private Boolean discarded = false;

	private ObjectId templateId;
	
	private List<Fields> inputElements;
	
	private String type;
	
	private PatientCollection patientCollection;
	
	private UserCollection patientUser;

	private CertificateTemplateCollection certificateTemplate;
	
	private String templateHtmlText;
	
	private String PNUM;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDateOfSign() {
		return dateOfSign;
	}

	public void setDateOfSign(Date dateOfSign) {
		this.dateOfSign = dateOfSign;
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

	public PatientCollection getPatientCollection() {
		return patientCollection;
	}

	public void setPatientCollection(PatientCollection patientCollection) {
		this.patientCollection = patientCollection;
	}

	public UserCollection getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(UserCollection patientUser) {
		this.patientUser = patientUser;
	}

	public CertificateTemplateCollection getCertificateTemplate() {
		return certificateTemplate;
	}

	public void setCertificateTemplate(CertificateTemplateCollection certificateTemplate) {
		this.certificateTemplate = certificateTemplate;
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

	@Override
	public String toString() {
		return "ConsentFormCollectionLookupResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", locationName=" + locationName
				+ ", PID=" + PID + ", formId=" + formId + ", localPatientName=" + localPatientName + ", emailAddress="
				+ emailAddress + ", gender=" + gender + ", dob=" + dob + ", address=" + address + ", mobileNumber="
				+ mobileNumber + ", landLineNumber=" + landLineNumber + ", bloodGroup=" + bloodGroup + ", declaration="
				+ declaration + ", title=" + title + ", dateOfSign=" + dateOfSign + ", signImageURL=" + signImageURL
				+ ", medicalHistory=" + medicalHistory + ", discarded=" + discarded + ", templateId=" + templateId
				+ ", inputElements=" + inputElements + ", type=" + type + ", patientCollection=" + patientCollection
				+ ", patientUser=" + patientUser + ", certificateTemplate=" + certificateTemplate
				+ ", templateHtmlText=" + templateHtmlText + ", PNUM=" + PNUM + "]";
	}

}
