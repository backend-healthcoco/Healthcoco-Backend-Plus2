package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.Resource;

@Document(collection = "transnational_cl")
public class TransactionalCollection extends GenericCollection{

    @Id
    private ObjectId id;

    @Field
    private ObjectId resourceId;

    @Field
    private Resource resource;

    @Indexed
    private Boolean isCached = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getResourceId() {
	return resourceId;
    }

    public void setResourceId(ObjectId resourceId) {
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
