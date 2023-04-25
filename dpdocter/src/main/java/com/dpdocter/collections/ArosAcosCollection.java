package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "aros_acos_cl")
public class ArosAcosCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId arosId;

	@Field
	private List<ObjectId> acosIds;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getArosId() {
		return arosId;
	}

	public void setArosId(ObjectId arosId) {
		this.arosId = arosId;
	}

	public List<ObjectId> getAcosIds() {
		return acosIds;
	}

	public void setAcosIds(List<ObjectId> acosIds) {
		this.acosIds = acosIds;
	}

	@Override
	public String toString() {
		return "ArosAcosCollection [id=" + id + ", arosId=" + arosId + ", acosIds=" + acosIds + "]";
	}

}
