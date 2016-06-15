package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "generic_code_cl")
public class GenericCodeCollection {

    @Id
    private String id;

    @Field
    private String code;
    
    @Field
    private String name;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return "GenericCodeCollection [id=" + id + ", code=" + code + ", name=" + name + "]";
    }
}
