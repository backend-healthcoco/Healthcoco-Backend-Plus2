package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

public class NdhmPrescriptionDetails {

	private List<Drug>  drug;
	
	private Date authoredOn;
	
	private String id;

	public Date getAuthoredOn() {
		return authoredOn;
	}

	public void setAuthoredOn(Date authoredOn) {
		this.authoredOn = authoredOn;
	}

	public List<Drug> getDrug() {
		return drug;
	}

	public void setDrug(List<Drug> drug) {
		this.drug = drug;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	
	
	
}
