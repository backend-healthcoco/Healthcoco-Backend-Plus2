package com.dpdocter.services;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.PatientTrack;
import com.dpdocter.enums.VisitedFor;

public interface PatientTrackService {
    boolean addRecord(PatientTrack request);

    boolean addRecord(Object details, VisitedFor visitedFor);

    boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor);

    DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size);

    DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size);
}
