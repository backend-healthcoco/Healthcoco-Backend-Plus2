package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Services extends GenericCollection {
    private String id;

    private String service;

    private Boolean toShow = true;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	@Override
	public String toString() {
		return "Services [id=" + id + ", service=" + service + ", toShow=" + toShow + "]";
	}

}
