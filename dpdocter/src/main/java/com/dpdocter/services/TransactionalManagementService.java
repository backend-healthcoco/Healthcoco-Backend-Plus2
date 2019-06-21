package com.dpdocter.services;

import org.bson.types.ObjectId;

import com.dpdocter.enums.Resource;

public interface TransactionalManagementService {

    void addResource(ObjectId resourceId, Resource resource, boolean isCached);

    void checkPatient(ObjectId id);

    void checkDrug(ObjectId id);

    void checkLabTest(ObjectId id);

    void checkComplaint(ObjectId id);

    void checkObservation(ObjectId id);

    void checkInvestigation(ObjectId id);

    void checkDiagnosis(ObjectId id);

    void checkNotes(ObjectId id);

    void checkDiagrams(ObjectId id);

    void checkResources();

    void checkLocation(ObjectId resourceId);

    void checkDoctor(ObjectId resourceId, ObjectId locationId);
    
    void checkPharmacy(ObjectId resourceId);

	void checkDoctorDrug(ObjectId resourceId);

}
