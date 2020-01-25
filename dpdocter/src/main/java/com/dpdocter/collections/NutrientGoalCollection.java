package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "nutrient_goal_cl")
public class NutrientGoalCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String value;

	@Field
	private Boolean discarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "NutrientGoalCollection [id=" + id + ", value=" + value + ", discarded=" + discarded + "]";
	}
}
