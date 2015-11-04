package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "aros_acos_cl")
public class ArosAcosCollection {
    @Id
    private String id;

    @Field
    private String arosId;

    @Field
    private List<String> acosIds;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getArosId() {
	return arosId;
    }

    public void setArosId(String arosId) {
	this.arosId = arosId;
    }

    public List<String> getAcosIds() {
	return acosIds;
    }

    public void setAcosIds(List<String> acosIds) {
	this.acosIds = acosIds;
    }

    @Override
    public String toString() {
	return "ArosAcosCollection [id=" + id + ", arosId=" + arosId + ", acosIds=" + acosIds + "]";
    }

}
