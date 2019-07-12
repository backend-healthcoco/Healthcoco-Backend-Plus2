package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "professionalmemberships_in", type = "professionalmemberships")
public class ESProfessionalMembershipDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String membership;

    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getMembership() {
	return membership;
    }

    public void setMembership(String membership) {
	this.membership = membership;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
	return "ESProfessionalMembershipDocument [id=" + id + ", membership=" + membership + ", updatedTime=" + updatedTime + "]";
    }
}
