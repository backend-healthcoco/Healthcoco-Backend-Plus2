package com.dpdocter.collections;

import org.bson.types.ObjectId;
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
    private ObjectId id;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

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
