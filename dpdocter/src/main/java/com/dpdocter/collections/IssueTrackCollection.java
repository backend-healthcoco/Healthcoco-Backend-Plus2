package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.IssueStatus;

@Document(collection = "issue_track_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class IssueTrackCollection extends GenericCollection {

    @Id
    private String id;

    @Indexed
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String issueCode;

    @Field
    private String subject;

    @Field
    private String explanation;

    @Field
    private IssueStatus status = IssueStatus.OPEN;

    @Field
    private Boolean discarded = false;

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

    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public IssueStatus getStatus() {
	return status;
    }

    public void setStatus(IssueStatus status) {
	this.status = status;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public String getIssueCode() {
	return issueCode;
    }

    public void setIssueCode(String issueCode) {
	this.issueCode = issueCode;
    }

    @Override
    public String toString() {
	return "IssueTrackCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", issueCode="
		+ issueCode + ", subject=" + subject + ", explanation=" + explanation + ", status=" + status + ", discarded=" + discarded + "]";
    }
}
