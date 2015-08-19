package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "cities")
public class SolrCityDocument {

	@Id
    @Field
    private String id;

    @Field
    private String city;

    @Field
    private Boolean isActivated = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	@Override
	public String toString() {
		return "SolrCityDocument [id=" + id + ", city=" + city + ", isActivated=" + isActivated + "]";
	}
}
