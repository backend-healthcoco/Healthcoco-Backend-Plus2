package com.dpdocter.solr.document;

import java.util.Date;

import javax.ws.rs.DefaultValue;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "notes")
public class SolrNotesDocument {
    @Id
    @Field
    private String id;

    @Field
    private String note;

    @Field
    private String doctorId = "";

    @Field
    private String locationId = "";

    @Field
    @DefaultValue(value = "")
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }
    
    public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDoctorId() {
	if (doctorId == null) {
	    return "";
	}
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	if (doctorId == null) {
	    this.doctorId = "";
	} else {
	    this.doctorId = doctorId;
	}
    }

    public String getLocationId() {
	if (locationId == null) {
	    return "";
	}
	return locationId;
    }

    public void setLocationId(String locationId) {
	if (locationId == null) {
	    this.locationId = "";
	} else {
	    this.locationId = locationId;
	}
    }

    public String getHospitalId() {
	if (hospitalId == null) {
	    return "";
	}
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	if (hospitalId == null) {
	    this.hospitalId = "";
	} else {
	    this.hospitalId = hospitalId;
	}
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

	@Override
	public String toString() {
		return "SolrNotesDocument [id=" + id + ", note=" + note + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", updatedTime="
				+ updatedTime + "]";
	}
}
