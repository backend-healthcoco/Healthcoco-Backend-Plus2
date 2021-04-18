package com.dpdocter.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.ClinicalNotesJasperDetails;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PatientVisit;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;

public interface PatientVisitService {
	// boolean addRecord(PatientVisit request);

	String addRecord(Object details, VisitedFor visitedFor, String visitId);

	boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor);

	DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, long page, int size,
			String role);

	DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, long page, int size,
			String role);

	PatientVisitResponse addMultipleData(AddMultipleDataRequest request, MultipartFile file);

	PatientVisitResponse getVisit(String visitId);

	List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId,
			long page, int size, Boolean isOTPVerified, String updatedTime, String visitFor);

	Boolean email(String visitId, String emailAddress);

	PatientVisitResponse deleteVisit(String visitId, Boolean discarded);

	Boolean smsVisit(String visitId, String doctorId, String locationId, String hospitalId, String mobileNumber);

	List<PatientVisit> getVisitsHandheld(String doctorId, String locationId, String hospitalId, String patientId,
			long page, int size, Boolean isOTPVerified, String updatedTime);

	String editRecord(String id, VisitedFor prescription);

	int getVisitCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	String getPatientVisitFile(String visitId, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA,
			Boolean showUSG, Boolean isLabPrint, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren, Boolean showPrescription, Boolean showTreatment, Boolean showclinicalNotes,
			Boolean showVitalSign);

	void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings, ObjectId doctorId);

	void generatePatientDetails(PatientDetails patientDetails, PatientCollection patient, String uniqueEMRId,
			String firstName, String mobileNumber, Map<String, Object> parameters, Date date, String hospitalUId,
			Boolean isPidHasDate);

	void updateAppointmentTime(ObjectId visitId, String appointmentId, WorkingHours workingHours, Date fromDate);

	void includeHistoryInPdf(HistoryCollection historyCollection, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Map<String, Object> parameters);

	PatientVisitResponse getPatientLastVisit(String doctorId, String locationId, String hospitalId, String patientId);



	ClinicalNotesJasperDetails getClinicalNotesJasperDetails(String clinicalNotesId, String contentLineStyle,
			Map<String, Object> parameters, Boolean showUSG, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren, ClinicalNotesCollection clinicalNotesCollection, Boolean showVitalSign);

}
