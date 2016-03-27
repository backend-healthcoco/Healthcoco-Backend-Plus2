package com.dpdocter.solr.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "professions")
public class SolrProfessionDocument {

    @Id
    private String id;

    @Field
    private String profession;

    @Field
    private String explanation;

    @Field
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

	@Override
	public String toString() {
		return "SolrProfessionDocument [id=" + id + ", profession=" + profession + ", explanation=" + explanation
				+ ", updatedTime=" + updatedTime + "]";
	}
}
