package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Vaccination;

@Document(collection = "immunisation_cl")
public class ImmunisationCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId patientId;

	@Field
	private List<Vaccination> vaccinations;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	@Override
	public String toString() {
		return "ImmunisationCollection [id=" + id + ", patientId=" + patientId + ", vaccinations=" + vaccinations + "]";
	}

}
