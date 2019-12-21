package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecipeTemplateItem;

@Document(collection = "recipe_template_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class RecipeTemplateCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private String name;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	@Field
	private List<RecipeTemplateItem> items;
	
	@Field
	private List<ObjectId> recipeIds;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<RecipeTemplateItem> getItems() {
		return items;
	}

	public void setItems(List<RecipeTemplateItem> items) {
		this.items = items;
	}

	public List<ObjectId> getRecipeIds() {
		return recipeIds;
	}

	public void setRecipeIds(List<ObjectId> recipeIds) {
		this.recipeIds = recipeIds;
	}

	@Override
	public String toString() {
		return "RecipeTemplateCollection [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", items=" + items
				+ ", recipeIds=" + recipeIds + "]";
	}
}
