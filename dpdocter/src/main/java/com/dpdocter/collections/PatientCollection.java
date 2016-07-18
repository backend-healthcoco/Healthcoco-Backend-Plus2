package com.dpdocter.collections;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Relations;

@Document(collection = "patient_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class PatientCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Field
    private String firstName;

    @Field
    private String imageUrl;

    @Field
    private String thumbnailUrl;

    @Field
    private String bloodGroup;

    @Field
    private String profession;

    @Field
    private List<Relations> relations;

    @Field
    private String emailAddress;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private String secMobile;

    @Field
    private String adhaarId;

    @Field
    private String panCardNumber;

    @Field
    private String drivingLicenseId;

    @Field
    private String insuranceId;

    @Field
    private String insuranceName;

    @Indexed
    private ObjectId userId;

    @Field
    private List<String> notes;

    @Field
    private String PID;

    @Field
    private Long registrationDate;

    @Field
    private String gender;

    @Field
    private DOB dob;

    @Field
    private Boolean discarded = false;

    @Field
    private Long dateOfVisit;

    @Field
    private ObjectId referredBy;

    @Field
    private Address address;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public List<Relations> getRelations() {
	return relations;
    }

    public void setRelations(List<Relations> relations) {
	this.relations = relations;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public String getSecMobile() {
	return secMobile;
    }

    public void setSecMobile(String secMobile) {
	this.secMobile = secMobile;
    }

    public String getAdhaarId() {
	return adhaarId;
    }

    public void setAdhaarId(String adhaarId) {
	this.adhaarId = adhaarId;
    }

    public String getPanCardNumber() {
	return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
	this.panCardNumber = panCardNumber;
    }

    public String getDrivingLicenseId() {
	return drivingLicenseId;
    }

    public void setDrivingLicenseId(String drivingLicenseId) {
	this.drivingLicenseId = drivingLicenseId;
    }

    public String getInsuranceId() {
	return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
	this.insuranceId = insuranceId;
    }

    public String getInsuranceName() {
	return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
	this.insuranceName = insuranceName;
    }

    public ObjectId getUserId() {
	return userId;
    }

    public void setUserId(ObjectId userId) {
	this.userId = userId;
    }

    public List<String> getNotes() {
	return notes;
    }

    public void setNotes(List<String> notes) {
	this.notes = notes;
    }

    public String getPID() {
	return PID;
    }

    public void setPID(String pID) {
	PID = pID;
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

    public Long getRegistrationDate() {
	return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
	this.registrationDate = registrationDate;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	if (firstName != null)
	    this.firstName = WordUtils.capitalize(firstName.toLowerCase());
	else
	    this.firstName = firstName;
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
	if(this.dob != null)dob.getAge();
    }

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Long getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Long dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public ObjectId getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(ObjectId referredBy) {
		this.referredBy = referredBy;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "PatientCollection [id=" + id + ", firstName=" + firstName + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", bloodGroup=" + bloodGroup + ", profession=" + profession + ", relations="
				+ relations + ", emailAddress=" + emailAddress + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", secMobile=" + secMobile + ", adhaarId="
				+ adhaarId + ", panCardNumber=" + panCardNumber + ", drivingLicenseId=" + drivingLicenseId
				+ ", insuranceId=" + insuranceId + ", insuranceName=" + insuranceName + ", userId=" + userId
				+ ", notes=" + notes + ", PID=" + PID + ", registrationDate=" + registrationDate + ", gender=" + gender
				+ ", dob=" + dob + ", discarded=" + discarded + ", dateOfVisit=" + dateOfVisit + ", referredBy="
				+ referredBy + ", address=" + address + "]";
	}
}
