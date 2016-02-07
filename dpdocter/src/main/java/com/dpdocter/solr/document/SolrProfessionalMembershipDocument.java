package com.dpdocter.solr.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "professionalMemberships")
public class SolrProfessionalMembershipDocument {

    @Id
    private String id;

    @Field
    private String membership;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getMembership() {
	return membership;
    }

    public void setMembership(String membership) {
	this.membership = membership;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "SolrProfessionalMembershipDocument [id=" + id + ", membership=" + membership + ", updatedTime=" + updatedTime + "]";
    }
}
