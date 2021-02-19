package com.dpdocter.beans;

import java.util.List;

public class ResponseBundle {

private String resourceType;

private String id;

private String type;

private String timestamp;

private NDHMRecordDataMeta meta;

private BundleIdentifier identifiers;

private List<NDHMPrecriptionRecordData> entry;

public String getResourceType() {
	return resourceType;
}

public void setResourceType(String resourceType) {
	this.resourceType = resourceType;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getTimestamp() {
	return timestamp;
}

public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}

public NDHMRecordDataMeta getMeta() {
	return meta;
}

public void setMeta(NDHMRecordDataMeta meta) {
	this.meta = meta;
}



public BundleIdentifier getIdentifiers() {
	return identifiers;
}

public void setIdentifiers(BundleIdentifier identifiers) {
	this.identifiers = identifiers;
}

public List<NDHMPrecriptionRecordData> getEntry() {
	return entry;
}

public void setEntry(List<NDHMPrecriptionRecordData> entry) {
	this.entry = entry;
} 





}
