package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.KioskDynamicUiCollection;

public interface KioskDynamicUiRepository extends MongoRepository<KioskDynamicUiCollection, ObjectId> {
	@Query("{'doctorId': ?0}")
	public KioskDynamicUiCollection findByDoctorId(ObjectId doctorId);
}
