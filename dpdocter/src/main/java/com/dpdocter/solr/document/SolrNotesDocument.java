package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "notes")
public class SolrNotesDocument {
    @Id
    @Field
    private String id;

    @Field
    private String notes;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Override
    public String toString() {
	return "SolrNotes [id=" + id + ", notes=" + notes + "]";
    }

}
