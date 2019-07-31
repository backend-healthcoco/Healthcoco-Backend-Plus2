package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.KioskDynamicUiCollection;

public interface KioskDynamicUiRepository extends MongoRepository<KioskDynamicUiCollection, ObjectId> {
	
	public KioskDynamicUiCollection findByDoctorId(ObjectId doctorId);
}
