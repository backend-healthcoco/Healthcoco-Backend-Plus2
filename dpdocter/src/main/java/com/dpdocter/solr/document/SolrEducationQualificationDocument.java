package com.dpdocter.solr.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "educationQualifications")
public class SolrEducationQualificationDocument {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "SolrEducationQualificationDocument [id=" + id + ", name=" + name + ", updatedTime=" + updatedTime + "]";
    }
}
