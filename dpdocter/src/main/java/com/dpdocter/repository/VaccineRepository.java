package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VaccineCollection;

public interface VaccineRepository extends MongoRepository<VaccineCollection, ObjectId> {

	public List<VaccineCollection> findByPatientId(ObjectId patientId);

}
