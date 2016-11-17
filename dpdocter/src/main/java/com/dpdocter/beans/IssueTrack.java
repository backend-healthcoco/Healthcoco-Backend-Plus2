package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.IssueStatus;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IssueTrack extends GenericCollection {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String issueCode;

    private String subject;

    private String explanation;

    private Boolean discarded = false;
    
    private IssueStatus status;

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

	public IssueStatus getStatus() {
		return status;
	}

	public void setStatus(IssueStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "IssueTrack [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", issueCode=" + issueCode + ", subject=" + subject + ", explanation=" + explanation
				+ ", discarded=" + discarded + ", status=" + status + "]";
	}

}
