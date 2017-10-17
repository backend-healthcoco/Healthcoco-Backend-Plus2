package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class GenericCodeWithStrength extends GenericCollection {

	private String id;
	private String genericId;
	private GenericCode genericCode;
	private String strength;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGenericId() {
		return genericId;
	}

	public void setGenericId(String genericId) {
		this.genericId = genericId;
	}

	public GenericCode getGenericCode() {
		return genericCode;
	}

	public void setGenericCode(GenericCode genericCode) {
		this.genericCode = genericCode;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	@Override
	public String toString() {
		return "GenericCodeWithStrength [id=" + id + ", genericId=" + genericId + ", strength=" + strength + "]";
	}

}
