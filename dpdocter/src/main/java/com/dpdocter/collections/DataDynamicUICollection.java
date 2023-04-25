package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DataDynamicField;

@Document(collection = "data_dynamic_ui_cl")
public class DataDynamicUICollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private DataDynamicField dataDynamicField;

	@Field
	private ObjectId doctorId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public DataDynamicField getDataDynamicField() {
		return dataDynamicField;
	}

	public void setDataDynamicField(DataDynamicField dataDynamicField) {
		this.dataDynamicField = dataDynamicField;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "DataDynamicUICollection [id=" + id + ", dataDynamicField=" + dataDynamicField + ", doctorId=" + doctorId
				+ "]";
	}

}
