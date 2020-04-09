package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "educationinstitutes_in", type = "educationinstitutes")
public class ESEducationInstituteDocument {

	@Id
    private String id;

	@Field(type = FieldType.Text,fielddata = true)
	private String name;

	@Field(type = FieldType.Date)
    private Date updatedTime = new Date();

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

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "ESEducationInstituteDocument [id=" + id + ", name=" + name + ", updatedTime=" + updatedTime + "]";
    }
}
