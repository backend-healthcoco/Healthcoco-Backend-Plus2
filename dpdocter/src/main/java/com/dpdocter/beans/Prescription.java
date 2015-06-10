package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Prescription extends GenericCollection {
	private String id;

	private String name;

	private List<PrescriptionItemDetails> itemList;

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

	public List<PrescriptionItemDetails> getItemList() {
		return itemList;
	}

	public void setItemList(List<PrescriptionItemDetails> itemList) {
		this.itemList = itemList;
	}

	@Override
	public String toString() {
		return "Prescription [id=" + id + ", name=" + name + ", itemList=" + itemList + "]";
	}

}
