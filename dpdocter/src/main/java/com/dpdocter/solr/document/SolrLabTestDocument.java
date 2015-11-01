package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "labTests")
public class SolrLabTestDocument {

    @Id
    @Field
    private String id;

    @Field
    private String testName;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getTestName() {
	return testName;
    }

    public void setTestName(String testName) {
	this.testName = testName;
    }

    @Override
    public String toString() {
	return "SolrLabTestDocument [id=" + id + ", testName=" + testName + "]";
    }
}
