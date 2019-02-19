package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorLoginPinCollection;

@Repository
public interface DoctorLoginPinRepository extends MongoRepository<DoctorLoginPinCollection, ObjectId> {
	@Query("{'doctorId': ?0}")
	public DoctorLoginPinCollection findByDoctorId(ObjectId doctorId);
}
