package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorClinicProfileCollection;

public interface DoctorClinicProfileRepository extends MongoRepository<DoctorClinicProfileCollection, ObjectId> {
    @Query("{'id': ?0, 'userLocationId': ?1}")
    DoctorClinicProfileCollection findByIdAndLocationId(ObjectId id, ObjectId userLocationId);

    @Query("{'userLocationId': ?0}")
    DoctorClinicProfileCollection findByLocationId(ObjectId userLocationId);
}
