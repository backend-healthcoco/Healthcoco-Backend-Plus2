package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Prescription extends GenericCollection {
    private String id;

    private String name;

    private List<PrescriptionItemDetail> items;

    private boolean inHistory = false;

    private boolean deleted;

    private Long createdDate;

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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    @Override
    public String toString() {
	return "Prescription [id=" + id + ", name=" + name + ", items=" + items + ", inHistory=" + inHistory + ", deleted=" + deleted + ", createdDate="
		+ createdDate + "]";
    }
}
