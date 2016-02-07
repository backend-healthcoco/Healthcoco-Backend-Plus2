package com.dpdocter.solr.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "bloodGroups")
public class SolrBloodGroupDocument {

    @Id
    private String id;

    @Field
    private String bloodGroup;

    @Field
    private String description;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "SolrBloodGroupDocument [id=" + id + ", bloodGroup=" + bloodGroup + ", description=" + description + ", updatedTime=" + updatedTime + "]";
    }
}
