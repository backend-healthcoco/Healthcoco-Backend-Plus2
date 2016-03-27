package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "landmark_cl")
public class LandmarkCollection {

    @Id
    private String id;

    @Field
    private String cityId;

    @Field
    private String landmark;

    @Field
    private String explanation;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getLandmark() {
	return landmark;
    }

    public void setLandmark(String landmark) {
	this.landmark = landmark;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
    public String toString() {
	return "LandmarkCollection [id=" + id + ", cityId=" + cityId + ", landmark=" + landmark + ", explanation=" + explanation + "]";
    }
}
