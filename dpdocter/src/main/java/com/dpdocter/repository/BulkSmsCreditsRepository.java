package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulkSmsCreditsCollection;

public interface BulkSmsCreditsRepository extends MongoRepository<BulkSmsCreditsCollection, ObjectId> {

	BulkSmsCreditsCollection findByDoctorId(ObjectId objectId);

}
