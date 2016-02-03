package com.dpdocter.solr.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.beans.SolrWorkingSchedule;

@SolrDocument(solrCoreName = "doctors")
public class SolrDoctorDocument extends DoctorLocation {
    @Id
    @Field
    private String id;

    @Field
    private String userId;

    @Field
    private String firstName;

    @Field
    private String gender;

    @Field
    private String emailAddress;

    @Field
    private String mobileNumber;

    @Field
    private String imageUrl;

    @Transient
    private ConsultationFee consultationFee;

    @Field
    private List<SolrWorkingSchedule> workingSchedules;

    @Field
    private List<String> specialities;

    @Transient
    private DoctorExperience experience;

    @Field
    private List<ConsultationFee> consultationFeeList;

    @Field
    private List<DoctorExperience> experienceList;    
    
    @Field
    private String facility;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public ConsultationFee getConsultationFee() {
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
	if(consultationFee != null){
		List<ConsultationFee> consultationFees = new ArrayList<ConsultationFee>();
		consultationFees.add(consultationFee);
		this.consultationFeeList = consultationFees;
	}
    }

    public List<SolrWorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<SolrWorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public List<String> getSpecialities() {
	return specialities;
    }

    public void setSpecialities(List<String> specialities) {
	this.specialities = specialities;
    }

    public DoctorExperience getExperience() {
	return experience;
    }

    public void setExperience(DoctorExperience experience) {
	this.experience = experience;
	if(experience != null){
		List<DoctorExperience> experiences = new ArrayList<DoctorExperience>();
		experiences.add(experience);
		this.experienceList = experiences;
	}
    }

	public List<ConsultationFee> getConsultationFeeList() {
		return consultationFeeList;
	}

	public void setConsultationFeeList(List<ConsultationFee> consultationFeeList) {
		this.consultationFeeList = consultationFeeList;
		if(consultationFeeList != null && !consultationFeeList.isEmpty()){
			this.consultationFee = consultationFeeList.get(0);
		}
	}

	public List<DoctorExperience> getExperienceList() {
		return experienceList;
	}

	public void setExperienceList(List<DoctorExperience> experienceList) {
		this.experienceList = experienceList;
		if(experienceList != null && !experienceList.isEmpty()){
			this.experience = experienceList.get(0);
		}
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	@Override
	public String toString() {
		return "SolrDoctorDocument [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", gender=" + gender
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl
				+ ", consultationFee=" + consultationFee + ", workingSchedules=" + workingSchedules + ", specialities="
				+ specialities + ", experience=" + experience + ", consultationFeeList=" + consultationFeeList
				+ ", experienceList=" + experienceList + ", facility=" + facility + "]";
	}
}
