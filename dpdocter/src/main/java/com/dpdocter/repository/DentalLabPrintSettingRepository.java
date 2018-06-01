package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DentalLabPrintSettingCollection;

@Repository
public interface DentalLabPrintSettingRepository extends MongoRepository<DentalLabPrintSettingCollection, ObjectId> {
	@Query("{'locationId': ?0, 'hospitalId': ?1}")
	DentalLabPrintSettingCollection getSettings(ObjectId locationId, ObjectId hospitalId);
}
