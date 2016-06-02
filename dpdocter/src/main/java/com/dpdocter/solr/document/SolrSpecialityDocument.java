package com.dpdocter.solr.document;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "specialities")
public class SolrSpecialityDocument {
    @Id
    @Field
    private String id;

    @Field
    private String speciality;

    @Field
    private String superSpeciality;

    @Field
    private String code;
    
    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSpeciality() {
	return speciality;
    }

    public void setSpeciality(String speciality) {
	this.speciality = speciality;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

	public String getSuperSpeciality() {
		return superSpeciality;
	}

	public void setSuperSpeciality(String superSpeciality) {
		this.superSpeciality = superSpeciality;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "SolrSpecialityDocument [id=" + id + ", speciality=" + speciality + ", superSpeciality="
				+ superSpeciality + ", code=" + code + ", updatedTime=" + updatedTime + "]";
	}
}
