package com.dpdocter.beans;

public class ClinicalNotesProvisionalDiagnosis {

	private String id;

	private String provisionalDiagnosis;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}

	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}

	@Override
	public String toString() {
		return "ClinicalNotesProvisionalDiagnosis [id=" + id + ", provisionalDiagnosis=" + provisionalDiagnosis + "]";
	}

}
