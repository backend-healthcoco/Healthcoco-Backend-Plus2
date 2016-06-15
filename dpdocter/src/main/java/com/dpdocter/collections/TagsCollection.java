package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "tags_cl")
public class TagsCollection extends GenericCollection{
    @Id
    private String id;

    @Field
    private String tag;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String explanation;

    @Field
    private String doctorId;

    @Field
    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getTag() {
	return tag;
    }

    public void setTag(String tag) {
	this.tag = tag;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "TagsCollection [id=" + id + ", tag=" + tag + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", explanation=" + explanation + ", doctorId=" + doctorId + ", discarded=" + discarded + "]";
	}
}
