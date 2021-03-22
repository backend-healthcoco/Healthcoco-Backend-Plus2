package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.beans.v2.DoctorContactsResponse;
import com.dpdocter.response.v2.PatientVisitResponse;

public interface PatientVisitService {
	// boolean addRecord(PatientVisit request);
/*
	String addRecord(Object details, VisitedFor visitedFor, String visitId);

	boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor);
*/
	DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role);

	DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role);
/*
	PatientVisitResponse addMultipleData(AddMultipleDataRequest request);

	PatientVisitResponse getVisit(String visitId);
*/
	List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor, String from,String to,Boolean discarded);

//	List<PatientVisitResponse> getVisitNew(String doctorId, String locationId, String hospitalId, String patientId,
//			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor, String from, String to,
//			Boolean discarded);

	/*Boolean email(String visitId, String emailAddress);

	PatientVisitResponse deleteVisit(String visitId, Boolean discarded);

	Boolean smsVisit(String visitId, String doctorId, String locationId, String hospitalId, String mobileNumber);

	List<PatientVisit> getVisitsHandheld(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime);

	String editRecord(String id, VisitedFor prescription);

	int getVisitCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	String getPatientVisitFile(String visitId, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA,
			Boolean showUSG, Boolean isLabPrint, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren);

	void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings, ObjectId doctorId);

	void generatePatientDetails(PatientDetails patientDetails, PatientCollection patient, String uniqueEMRId,
			String firstName, String mobileNumber, Map<String, Object> parameters, Date date, String hospitalUId, Boolean isPidHasDate);

	void updateAppointmentTime(ObjectId visitId, String appointmentId, WorkingHours workingHours, Date fromDate);

	void includeHistoryInPdf(HistoryCollection historyCollection, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Map<String, Object> parameters);

	PatientVisitResponse getPatientLastVisit(String doctorId, String locationId, String hospitalId, String patientId);
	
	ClinicalNotesJasperDetails getClinicalNotesJasperDetails(String clinicalNotesId, String contentLineStyle,
			Map<String, Object> parameters, Boolean showUSG, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren, ClinicalNotesCollection clinicalNotesCollection);*/
}
