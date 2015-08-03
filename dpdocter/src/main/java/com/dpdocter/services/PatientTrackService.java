package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientTrack;
import com.dpdocter.enums.VisitedFor;

public interface PatientTrackService {
    boolean addRecord(PatientTrack request);

    boolean addRecord(Object details, VisitedFor visitedFor);

    boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor);

    List<Patient> recentlyVisited(int page, int size);

    List<Patient> mostVisited(int page, int size);
}
