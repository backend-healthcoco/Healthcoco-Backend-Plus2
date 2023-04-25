package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.Type;

@Document(collection = "aros_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class ArosCollection {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId roleOrUserId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId locationId;

	@Field
	private Type type;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getRoleOrUserId() {
		return roleOrUserId;
	}

	public void setRoleOrUserId(ObjectId roleOrUserId) {
		this.roleOrUserId = roleOrUserId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ArosCollection [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + "]";
	}

}
