package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AccessModule;

@Document(collection = "acos_cl")
public class AcosCollection {
    @Id
    private String id;

    @Field
    private List<AccessModule> accessModules;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<AccessModule> getAccessModules() {
	return accessModules;
    }

    public void setAccessModules(List<AccessModule> accessModules) {
	this.accessModules = accessModules;
    }

    @Override
    public String toString() {
	return "AcosCollection [id=" + id + ", accessModules=" + accessModules + "]";
    }

}
