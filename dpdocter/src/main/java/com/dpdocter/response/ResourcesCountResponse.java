package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Fields;

public class ResourcesCountResponse {

	private String resourceType;

	private List<Fields> fields;

	private String totalCount;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public List<Fields> getFields() {
		return fields;
	}

	public void setFields(List<Fields> fields) {
		this.fields = fields;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "ResourcesCountResponse [resourceType=" + resourceType + ", fields=" + fields + ", totalCount="
				+ totalCount + "]";
	}

}
