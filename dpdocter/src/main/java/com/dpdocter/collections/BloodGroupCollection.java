package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "blood_group_cl")
public class BloodGroupCollection extends GenericCollection{

    @Id
    private String id;

    @Field
    private String bloodGroup;

    @Field
    private String description;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "BloodGroupCollection [id=" + id + ", bloodGroup=" + bloodGroup + ", description=" + description + "]";
    }
}
