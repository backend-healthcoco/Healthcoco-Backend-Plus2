package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Drug;
@Document(collection = "drugs_allerigies_cl")
public class DrugsAndAllergiesCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private List<Drug> drugs;
	@Field
	private String allergies;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;

	@Override
	public String toString() {
		return "DrugsAndAllergiesCollection [id=" + id + ", drugs=" + drugs + ", allergies=" + allergies + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ "]";
	}

}
