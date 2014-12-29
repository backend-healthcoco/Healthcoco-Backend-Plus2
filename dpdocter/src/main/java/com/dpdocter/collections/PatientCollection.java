package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="patient_cl")
public class PatientCollection {
	
	@Id
	private String id;
	@Field
	private String bloodGroup;
	@Field
	private String profession;
	@Field
	private List<String> relations;
	@Field
	private String userId;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public List<String> getRelations() {
		return relations;
	}
	public void setRelations(List<String> relations) {
		this.relations = relations;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "PatientCollection [id=" + id + ", bloodGroup=" + bloodGroup
				+ ", profession=" + profession + ", relations=" + relations
				+ ", userId=" + userId + "]";
	}
	
		
}
