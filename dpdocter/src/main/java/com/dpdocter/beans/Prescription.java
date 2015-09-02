package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Prescription extends GenericCollection {
    private String id;

    private String name;

    private List<PrescriptionItemDetail> items;

    private boolean inHistory = false;

    private Boolean discarded;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "Prescription [id=" + id + ", name=" + name + ", items=" + items + ", inHistory=" + inHistory
				+ ", discarded=" + discarded + "]";
	}
}
