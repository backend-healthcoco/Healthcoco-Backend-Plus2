package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientVisitCollection;

@Repository
public interface PatientVisitRepository extends MongoRepository<PatientVisitCollection, ObjectId>, PagingAndSortingRepository<PatientVisitCollection, ObjectId> {

	PatientVisitCollection findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndVisitedFor(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, 
			ObjectId patientId, String visitedFor);

	PatientVisitCollection findByRecordId(ObjectId recordId);
    
    PatientVisitCollection findByTreatmentId(ObjectId treatmentId);

    PatientVisitCollection findByPrescriptionId(ObjectId prescriptionId);

    PatientVisitCollection findByClinicalNotesId(ObjectId clinicalNotesId);    

}
