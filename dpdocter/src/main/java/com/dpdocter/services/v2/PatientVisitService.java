package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.beans.v2.DoctorContactsResponse;
import com.dpdocter.response.v2.PatientVisitResponse;

public interface PatientVisitService {
	DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role);

	DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role);

	List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor, String from, String to,
			Boolean discarded);

	List<PatientVisitResponse> getVisitOLDCODE(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor, String from, String to,
			Boolean discarded);

}
