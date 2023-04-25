package com.dpdocter.response;

import org.bson.types.ObjectId;

import com.dpdocter.collections.ArosAcosCollection;
import com.dpdocter.enums.Type;

public class ArosLookupResponse {

	private ObjectId id;

	private ObjectId roleOrUserId;

	private ObjectId hospitalId;

	private ObjectId locationId;

	private Type type;

	private ArosAcosCollection arosAcosCollection;

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

	public ArosAcosCollection getArosAcosCollection() {
		return arosAcosCollection;
	}

	public void setArosAcosCollection(ArosAcosCollection arosAcosCollection) {
		this.arosAcosCollection = arosAcosCollection;
	}

	@Override
	public String toString() {
		return "ArosLookupResponse [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", type=" + type + ", arosAcosCollection=" + arosAcosCollection + "]";
	}
}
