package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.ClinicalNotesComplaint;
import com.dpdocter.beans.ClinicalNotesDiagnosis;
import com.dpdocter.beans.ClinicalNotesInvestigation;
import com.dpdocter.beans.ClinicalNotesNote;
import com.dpdocter.beans.ClinicalNotesObservation;
import com.dpdocter.request.ClinicalNotesAddRequest;

public class GeneralTests {
    public static void main(String args[]) {
	ClinicalNotesAddRequest request = new ClinicalNotesAddRequest();

	String patientId = "55661e9ab732a94e37e2d0ab";
	request.setPatientId(patientId);

	ClinicalNotesComplaint complaint = new ClinicalNotesComplaint();
	complaint.setComplaint("Test Complaints");
	request.setComplaints(Arrays.asList(complaint));

	ClinicalNotesObservation observation = new ClinicalNotesObservation();
	observation.setObservation("Test Observations");
	request.setObservations(Arrays.asList(observation));

	ClinicalNotesInvestigation investigation = new ClinicalNotesInvestigation();
	investigation.setInvestigation("Test Investigation");
	request.setInvestigations(Arrays.asList(investigation));

	ClinicalNotesDiagnosis diagnosis = new ClinicalNotesDiagnosis();
	diagnosis.setDiagnosis("Test Diagnosis");
	request.setDiagnoses(Arrays.asList(diagnosis));

	ClinicalNotesNote notes = new ClinicalNotesNote();
	notes.setNote("Test Note");
	request.setNotes(Arrays.asList(notes));

	request.setDiagrams(Arrays.asList("Test Diagram Link"));

	String doctorId = "5566220cb732a94e37e2d0ac";
	request.setDoctorId(doctorId);

	String locationId = "L12345";
	request.setLocationId(locationId);

	String hospitalId = "H12345";
	request.setHospitalId(hospitalId);

	System.out.println(Converter.ObjectToJSON(request));

    }
}
