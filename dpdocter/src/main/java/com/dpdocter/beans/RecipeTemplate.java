package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.RecipeTemplateItemResponse;

public class RecipeTemplate extends GenericCollection {

	private String id;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private List<RecipeTemplateItemResponse> items;

	private List<String> recipeIds;

	private Map<String, String> multilingualName;

	public Map<String, String> getMultilingualName() {
		return multilingualName;
	}

	public void setMultilingualName(Map<String, String> multilingualName) {
		this.multilingualName = multilingualName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<RecipeTemplateItemResponse> getItems() {
		return items;
	}

	public void setItems(List<RecipeTemplateItemResponse> items) {
		this.items = items;
	}

	public List<String> getRecipeIds() {
		return recipeIds;
	}

	public void setRecipeIds(List<String> recipeIds) {
		this.recipeIds = recipeIds;
	}

	@Override
	public String toString() {
		return "RecipeTemplate [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", items=" + items + ", recipeIds="
				+ recipeIds + ", multilingualName=" + multilingualName + "]";
	}
}
