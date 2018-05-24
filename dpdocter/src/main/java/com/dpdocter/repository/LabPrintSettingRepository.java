package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabPrintSettingCollection;

public interface LabPrintSettingRepository extends MongoRepository<LabPrintSettingCollection, ObjectId> {
	@Query("{'locationId': ?0 ,'hospitalId': ?1}")
	LabPrintSettingCollection findBylocationIdAndhospitalId(ObjectId locationId, ObjectId hospitalId);
}
