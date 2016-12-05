package com.dpdocter.beans;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.VaccineRoute;
import com.dpdocter.enums.VaccineStatus;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Vaccine extends GenericCollection {

	private String name;
	private Date dueDate;
	private VaccineStatus status = VaccineStatus.GIVEN;
	private VaccineRoute route;
	private String bodySite;
	private Integer dosage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public VaccineStatus getStatus() {
		return status;
	}

	public void setStatus(VaccineStatus status) {
		this.status = status;
	}

	public VaccineRoute getRoute() {
		return route;
	}

	public void setRoute(VaccineRoute route) {
		this.route = route;
	}

	public String getBodySite() {
		return bodySite;
	}

	public void setBodySite(String bodySite) {
		this.bodySite = bodySite;
	}

	public Integer getDosage() {
		return dosage;
	}

	public void setDosage(Integer dosage) {
		this.dosage = dosage;
	}

	@Override
	public String toString() {
		return "Vaccine [name=" + name + ", dueDate=" + dueDate + ", status=" + status + ", route=" + route
				+ ", bodySite=" + bodySite + ", dosage=" + dosage + "]";
	}

}
