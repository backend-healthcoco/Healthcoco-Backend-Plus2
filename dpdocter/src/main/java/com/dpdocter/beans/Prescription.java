package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Prescription extends GenericCollection {
    private String id;

    private String name;

    private List<PrescriptionItemDetail> items;

    private boolean inHistory = false;

    private Boolean isDeleted;

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

    public Boolean getIsDeleted() {
	return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
	this.isDeleted = isDeleted;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    @Override
    public String toString() {
	return "Prescription [id=" + id + ", name=" + name + ", items=" + items + ", inHistory=" + inHistory + ", isDeleted=" + isDeleted + ", createdDate="
		+ createdDate + "]";
    }
}
