package com.dpdocter.services;

public interface UploadDateService {

	Boolean uploadPatientData(String doctorId, String locationId, String hospitalId);

	Boolean uploadPrescriptionData(String doctorId, String locationId, String hospitalId);

	Boolean uploadAppointmentData(String doctorId, String locationId, String hospitalId);

	Boolean uploadTreatmentPlansData(String doctorId, String locationId, String hospitalId);

	Boolean uploadTreatmentData(String doctorId, String locationId, String hospitalId);

	Boolean assignPNUMToPatientsHavingPNUMAsNull(String doctorId, String locationId, String hospitalId);

	Boolean deletePatients(String doctorId, String locationId, String hospitalId);

	Boolean updateEMR();

	Boolean uploadTreatmentServicesData(String doctorId, String locationId, String hospitalId);

	Boolean uploadClinicalNotesData(String doctorId, String locationId, String hospitalId);

	Boolean uploadInvoicesData(String doctorId, String locationId, String hospitalId);

	Boolean uploadPaymentsData(String doctorId, String locationId, String hospitalId);

	Boolean updatePaymentsData(String doctorId, String locationId, String hospitalId);
}
