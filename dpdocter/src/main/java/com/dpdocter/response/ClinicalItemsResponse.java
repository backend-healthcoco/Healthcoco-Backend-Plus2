package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

public class ClinicalItemsResponse {

	private ObjectId keepResourceId;

	private List<ObjectId> resourceIds;

	private List<String> resourceIdsForEs;

	private String resourceName;

	long count = 0;

	public ObjectId getKeepResourceId() {
		return keepResourceId;
	}

	public void setKeepResourceId(ObjectId keepResourceId) {
		this.keepResourceId = keepResourceId;
	}

	public List<ObjectId> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<ObjectId> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<String> getResourceIdsForEs() {
		return resourceIdsForEs;
	}

	public void setResourceIdsForEs(List<String> resourceIdsForEs) {
		this.resourceIdsForEs = resourceIdsForEs;
	}

	@Override
	public String toString() {
		return "ClinicalItemsResponse [keepResourceId=" + keepResourceId + ", resourceIds=" + resourceIds
				+ ", resourceIdsForEs=" + resourceIdsForEs + ", resourceName=" + resourceName + ", count=" + count
				+ "]";
	}

}
