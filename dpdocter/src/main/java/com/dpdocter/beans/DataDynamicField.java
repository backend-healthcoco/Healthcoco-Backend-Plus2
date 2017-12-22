package com.dpdocter.beans;

public class DataDynamicField {

	private PrescriptionDynamicField prescriptionDynamicField;

	private ClinicalNotesDynamicField clinicalNotesDynamicField;
	
	private DischargeSummaryDynamicFields dischargeSummaryDynamicFields;
	
	private TreatmentDynamicFields treatmentDynamicFields;

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
	

	public DischargeSummaryDynamicFields getDischargeSummaryDynamicFields() {
		return dischargeSummaryDynamicFields;
	}

	public void setDischargeSummaryDynamicFields(DischargeSummaryDynamicFields dischargeSummaryDynamicFields) {
		this.dischargeSummaryDynamicFields = dischargeSummaryDynamicFields;
	}

	public TreatmentDynamicFields getTreatmentDynamicFields() {
		return treatmentDynamicFields;
	}

	public void setTreatmentDynamicFields(TreatmentDynamicFields treatmentDynamicFields) {
		this.treatmentDynamicFields = treatmentDynamicFields;
	}

	@Override
	public String toString() {
		return "DataDynamicField [prescriptionDynamicField=" + prescriptionDynamicField + ", clinicalNotesDynamicField="
				+ clinicalNotesDynamicField + ", dischargeSummaryDynamicFields=" + dischargeSummaryDynamicFields
				+ ", treatmentDynamicFields=" + treatmentDynamicFields + "]";
	}

}
