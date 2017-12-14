package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DataDynamicUI extends GenericCollection{

	private String id;
	private DataDynamicField dataDynamicField;
	private String doctorId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DataDynamicField getDataDynamicField() {
		return dataDynamicField;
	}

	public void setDataDynamicField(DataDynamicField dataDynamicField) {
		this.dataDynamicField = dataDynamicField;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

}
