package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "city_cl")
public class CityCollection {

	@Id
    private String id;

	@Indexed(unique = true)
    private String city;

    @Field
    private String description;

    @Field
    private Boolean isActivated = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	@Override
	public String toString() {
		return "CityCollection [id=" + id + ", city=" + city + ", description=" + description + ", isActivated="
				+ isActivated + "]";
	}
}
