package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactsResponse;
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

    List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId, int page, int size, Boolean isOTPVerified,
	    String updatedTime);

    Boolean email(String visitId, String emailAddress);
}
