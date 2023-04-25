package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.AccessPermissionType;

@Document(collection = "acos_cl")
public class AcosCollection {
	@Id
	private ObjectId id;

	@Field
	private String module;

	@Field
	private String url;

	@Field
	private List<AccessPermissionType> accessPermissionTypes;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<AccessPermissionType> getAccessPermissionTypes() {
		return accessPermissionTypes;
	}

	public void setAccessPermissionTypes(List<AccessPermissionType> accessPermissionTypes) {
		this.accessPermissionTypes = accessPermissionTypes;
	}

	@Override
	public String toString() {
		return "AcosCollection [id=" + id + ", module=" + module + ", url=" + url + ", accessPermissionTypes="
				+ accessPermissionTypes + "]";
	}

}
