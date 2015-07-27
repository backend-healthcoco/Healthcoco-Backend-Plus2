package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Prescription extends GenericCollection {
    private String id;

    private String name;

    private List<PrescriptionItemDetail> items;

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

    public List<PrescriptionItemDetail> getItems() {
	return items;
    }

    public void setItems(List<PrescriptionItemDetail> items) {
	this.items = items;
    }

    @Override
    public String toString() {
	return "Prescription [id=" + id + ", name=" + name + ", items=" + items + "]";
    }

}
