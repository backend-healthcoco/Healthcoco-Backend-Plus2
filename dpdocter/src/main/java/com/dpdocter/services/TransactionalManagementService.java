package com.dpdocter.services;

import com.dpdocter.enums.Resource;

public interface TransactionalManagementService {

    void addResource(String resourceId, Resource resource, boolean isCached);

    void checkPatient(String id);

    void checkDrug(String id);

    void checkLabTest(String id);

    void checkComplaint(String id);

    void checkObservation(String id);

    void checkInvestigation(String id);

    void checkDiagnosis(String id);

    void checkNotes(String id);

    void checkDiagrams(String id);

    void checkResources();

    void checkLocation(String resourceId);

    void checkDoctor(String resourceId, String locationId);

}
