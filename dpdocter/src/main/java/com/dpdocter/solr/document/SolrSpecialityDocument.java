package com.dpdocter.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "specialities")
public class SolrSpecialityDocument {
    @Id
    @Field
    private String id;

    @Field
    private String speciality;

    @Field
    private String code;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSpeciality() {
	return speciality;
    }

    public void setSpeciality(String speciality) {
	this.speciality = speciality;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "SolrSpecialityDocument [id=" + id + ", speciality=" + speciality + ", code=" + code + "]";
	}
}
