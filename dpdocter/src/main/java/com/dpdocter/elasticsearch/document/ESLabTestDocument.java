package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "labtests_in", type = "labtests")
public class ESLabTestDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String testId;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Integer)
	private int cost = 0;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		if (locationId == null) {
			return "";
		}
		return locationId;
	}

	public void setLocationId(String locationId) {
		if (locationId == null) {
			this.locationId = "";
		} else {
			this.locationId = locationId;
		}
	}

	public String getHospitalId() {
		if (hospitalId == null) {
			return "";
		}
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		if (hospitalId == null) {
			this.hospitalId = "";
		} else {
			this.hospitalId = hospitalId;
		}
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "ESLabTestDocument [id=" + id + ", testId=" + testId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", cost=" + cost + ", discarded=" + discarded + ", updatedTime=" + updatedTime + "]";
	}

}
