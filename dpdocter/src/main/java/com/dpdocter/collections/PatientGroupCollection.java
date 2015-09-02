package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient_group_cl")
public class PatientGroupCollection extends GenericCollection{
    @Id
    private String id;

    @Field
    private String groupId;

    @Field
    private String patientId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getGroupId() {
	return groupId;
    }

    public void setGroupId(String groupId) {
	this.groupId = groupId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

}
