package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "services_cl")
public class ServicesCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private String service;

    @Field
    private Boolean toShow = true;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	@Override
	public String toString() {
		return "ServicesCollection [id=" + id + ", service=" + service + ", toShow=" + toShow + "]";
	}

}
