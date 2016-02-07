package com.dpdocter.solr.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "medicalCouncils")
public class SolrMedicalCouncilDocument {

    @Id
    private String id;

    @Field
    private String medicalCouncil;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getMedicalCouncil() {
	return medicalCouncil;
    }

    public void setMedicalCouncil(String medicalCouncil) {
	this.medicalCouncil = medicalCouncil;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "SolrMedicalCouncilDocument [id=" + id + ", medicalCouncil=" + medicalCouncil + ", updatedTime=" + updatedTime + "]";
    }
}
