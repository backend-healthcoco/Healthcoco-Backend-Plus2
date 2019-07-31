package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferencesCollection;

@Repository
public interface ReferenceRepository extends MongoRepository<ReferencesCollection, ObjectId> {

	ReferencesCollection findByReferenceAndDoctorIdAndLocationIdAndHospitalId(String reference, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}
