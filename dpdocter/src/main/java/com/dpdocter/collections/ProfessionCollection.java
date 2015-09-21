package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "profession_cl")
public class ProfessionCollection {

    @Id
    private String id;

    @Field
    private String profession;

    @Field
    private String description;

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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "ProfessionCollection [id=" + id + ", profession=" + profession + ", description=" + description + "]";
    }
}
