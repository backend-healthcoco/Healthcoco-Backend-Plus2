package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "localitiesAndLandmarks")
public class SolrLocalityLandmarkDocument {

    @Id
    @Field
    private String id;

    @Field
    private String cityId;

    @Field
    private String locality;

    @Field
    private String landmark;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
    }

    public String getLandmark() {
	return landmark;
    }

    public void setLandmark(String landmark) {
	this.landmark = landmark;
    }

    @Override
    public String toString() {
	return "SolrLocalityLandmarkDocument [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", landmark=" + landmark + "]";
    }
}
