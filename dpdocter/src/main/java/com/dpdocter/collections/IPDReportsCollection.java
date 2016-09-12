package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class IPDReportsCollection extends GenericCollection{
	
	private ObjectId id;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	

}
