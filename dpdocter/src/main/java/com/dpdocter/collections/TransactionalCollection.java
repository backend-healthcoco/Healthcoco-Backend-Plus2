package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.Resource;

@Document(collection = "transnational_cl")
public class TransactionalCollection {

    @Id
    private String id;

    @Field
    private String resourceId;

    @Field
    private Resource resource;

    @Field
    private Boolean isCached = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getResourceId() {
	return resourceId;
    }

    public void setResourceId(String resourceId) {
	this.resourceId = resourceId;
    }

    public Resource getResource() {
	return resource;
    }

    public void setResource(Resource resource) {
	this.resource = resource;
    }

    public Boolean getIsCached() {
	return isCached;
    }

    public void setIsCached(Boolean isCached) {
	this.isCached = isCached;
    }

    @Override
    public String toString() {
	return "TransactionalCollection [id=" + id + ", resourceId=" + resourceId + ", resource=" + resource + ", isCached=" + isCached + "]";
    }
}
