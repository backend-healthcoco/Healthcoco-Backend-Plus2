package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "services_in", type = "services")
public class ESServicesDocument {
    @Id
    private String id;

    @Field(type = FieldType.String)
    private String service;
    
    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "ESServicesDocument [id=" + id + ", service=" + service + ", updatedTime=" + updatedTime + "]";
	}

}
