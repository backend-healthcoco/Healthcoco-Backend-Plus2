package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "diagrams")
public class SolrDiagrams {
	@Id
	@Field
	private String id;

	@Field
	private String diagramUrl;

	@Field
	private String tags;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiagramUrl() {
		return diagramUrl;
	}

	public void setDiagramUrl(String diagramUrl) {
		this.diagramUrl = diagramUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "SolrDiagrams [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + "]";
	}

}
