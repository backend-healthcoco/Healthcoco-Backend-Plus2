package com.dpdocter.beans;

import java.util.List;

public class PrescriptionJasperDetails {

	private String drug;
	
	private String dosage;
	
	private String duration;
	
	private List<String> direction;
	
	private String instruction;

	public PrescriptionJasperDetails(String drug, String dosage, String duration, List<String> direction,
			String instruction) {
		this.drug = drug;
		this.dosage = dosage;
		this.duration = duration;
		this.direction = direction;
		this.instruction = instruction;
	}

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<String> getDirection() {
		return direction;
	}

	public void setDirection(List<String> direction) {
		this.direction = direction;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	@Override
	public String toString() {
		return "PrescriptionJasperDetails [drug=" + drug + ", dosage=" + dosage + ", duration=" + duration
				+ ", direction=" + direction + ", instruction=" + instruction + "]";
	}
	
	
}
