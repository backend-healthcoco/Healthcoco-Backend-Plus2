package com.dpdocter.beans;

public class DataDynamicField {

	private PrescriptionDynamicField prescriptionDynamicField;

	private ClinicalNotesDynamicField clinicalNotesDynamicField;

	public PrescriptionDynamicField getPrescriptionDynamicField() {
		return prescriptionDynamicField;
	}

	public void setPrescriptionDynamicField(PrescriptionDynamicField prescriptionDynamicField) {
		this.prescriptionDynamicField = prescriptionDynamicField;
	}

	public ClinicalNotesDynamicField getClinicalNotesDynamicField() {
		return clinicalNotesDynamicField;
	}

	public void setClinicalNotesDynamicField(ClinicalNotesDynamicField clinicalNotesDynamicField) {
		this.clinicalNotesDynamicField = clinicalNotesDynamicField;
	}

	@Override
	public String toString() {
		return "DataDynamicField [prescriptionDynamicField=" + prescriptionDynamicField + ", clinicalNotesDynamicField="
				+ clinicalNotesDynamicField + "]";
	}

}
