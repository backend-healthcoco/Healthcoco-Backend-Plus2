package com.dpdocter.response;

import org.bson.types.ObjectId;

import com.dpdocter.collections.AcosCollection;
import com.dpdocter.collections.ArosAcosCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.Type;

public class ArosAcosLookupResponse extends GenericCollection {

	private String id;

	private ObjectId roleOrUserId;

	private ObjectId hospitalId;

	private ObjectId locationId;

	private Type type;

	ArosAcosCollection arosAcos;

	AcosCollection acos;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public ArosAcosCollection getArosAcos() {
		return arosAcos;
	}

	public void setArosAcos(ArosAcosCollection arosAcos) {
		this.arosAcos = arosAcos;
	}

	public AcosCollection getAcos() {
		return acos;
	}

	public void setAcos(AcosCollection acos) {
		this.acos = acos;
	}

	@Override
	public String toString() {
		return "ArosAcosLookupResponse [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", type=" + type + ", arosAcos=" + arosAcos + ", acos=" + acos + "]";
	}
}
