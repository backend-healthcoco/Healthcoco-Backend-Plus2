package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "collection1")
public class SearchDoctorSolrDocument {
	@Id
	@Field
	private String id;

	@Field
	private String name;

	@Field
	private String title;

	@Field
	private String doctorSpecification;

	public String getDoctorSpecification() {
		return doctorSpecification;
	}

	public void setDoctorSpecification(String doctorSpecification) {
		this.doctorSpecification = doctorSpecification;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

}
