package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "education_institute_cl")
public class EducationInstituteCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Field
    private String name;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "EducationInstituteCollection [id=" + id + ", name=" + name + "]";
    }
}
