package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "professions_in", type = "professions")
public class ESProfessionDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String profession;

    @Field(type = FieldType.Text)
    private String explanation;

    @Field(type = FieldType.Date)
    private Date updatedTime;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public Date getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
	this.updatedTime = updatedTime;
    }

	@Override
	public String toString() {
		return "ESProfessionDocument [id=" + id + ", profession=" + profession + ", explanation=" + explanation
				+ ", updatedTime=" + updatedTime + "]";
	}
}
