package com.dpdocter.solr.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "drugs")
public class SolrDrugDocument {
    @Id
    @Field
    private String id;

    @Field
    private String drugName;

    @Field
    private String description;

    @Field
    private String drugCode;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDrugName() {
	return drugName;
    }

    public void setDrugName(String drugName) {
	this.drugName = drugName;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getDrugCode() {
	return drugCode;
    }

    public void setDrugCode(String drugCode) {
	this.drugCode = drugCode;
    }

    @Override
    public String toString() {
	return "SolrDrugDocument [id=" + id + ", drugName=" + drugName + ", description=" + description + ", drugCode=" + drugCode + "]";
    }

}
