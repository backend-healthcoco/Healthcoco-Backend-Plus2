package com.dpdocter.services;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PatientVisit;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;

public interface PatientVisitService {
    // boolean addRecord(PatientVisit request);

    String addRecord(Object details, VisitedFor visitedFor, String visitId);

    boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor);

    DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size);

    DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size);

    PatientVisitResponse addMultipleData(AddMultipleDataRequest request);

    PatientVisitResponse getVisit(String visitId);

    List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId, int page, int size, Boolean isOTPVerified,
	    String updatedTime,String visitFor);

    Boolean email(String visitId, String emailAddress);

    PatientVisit deleteVisit(String visitId, Boolean discarded);

    Boolean smsVisit(String visitId, String doctorId, String locationId, String hospitalId, String mobileNumber);

    List<PatientVisit> getVisitsHandheld(String doctorId, String locationId, String hospitalId, String patientId, int page, int size, Boolean isOTPVerified,
	    String updatedTime);

    String editRecord(String id, VisitedFor prescription);

    int getVisitCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, boolean isOTPVerified);

    String getPatientVisitFile(String visitId);

	void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings, ObjectId doctorId);

	void generatePatientDetails(PatientDetails patientDetails, PatientCollection patient, String uniqueEMRId,
			String firstName, String mobileNumber, Map<String, Object> parameters);
}
