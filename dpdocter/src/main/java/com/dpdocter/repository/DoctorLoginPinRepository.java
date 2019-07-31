package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorLoginPinCollection;

@Repository
public interface DoctorLoginPinRepository extends MongoRepository<DoctorLoginPinCollection, ObjectId> {

	public DoctorLoginPinCollection findByDoctorId(ObjectId doctorId);
}
