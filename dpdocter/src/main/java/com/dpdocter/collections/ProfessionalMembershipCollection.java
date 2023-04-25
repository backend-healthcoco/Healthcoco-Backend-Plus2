package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "professional_membership_cl")
public class ProfessionalMembershipCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String membership;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getMembership() {
		return membership;
	}

	public void setMembership(String membership) {
		this.membership = membership;
	}

	@Override
	public String toString() {
		return "ProfessionalMembershipCollection [id=" + id + ", membership=" + membership + "]";
	}

}
