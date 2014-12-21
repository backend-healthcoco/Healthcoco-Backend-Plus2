package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="patient_info_cl")
public class PatientInfoCollection {
	
	@Id
	private String id;
	@Field
	private String bloodGroup;
	@Field
	private String profession;
	@Field
	private List<String> relations;
	
	private List<String> groups;
	
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
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	@Override
	public String toString() {
		return "PatientInfoCollection [id=" + id + ", bloodGroup=" + bloodGroup
				+ ", profession=" + profession + ", relations=" + relations
				+ ", groups=" + groups + "]";
	}
	
	
	
}
