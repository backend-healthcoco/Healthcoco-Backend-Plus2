package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "investigations")
public class SolrInvestigations {
	@Id
	@Field
	private String id;

	@Field
	private String investigation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	@Override
	public String toString() {
		return "SolrInvestigations [id=" + id + ", investigation=" + investigation + "]";
	}

}
