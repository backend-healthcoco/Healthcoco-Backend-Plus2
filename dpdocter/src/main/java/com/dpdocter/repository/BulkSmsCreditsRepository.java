package com.dpdocter.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;

public interface BulkSmsCreditsRepository extends MongoRepository<BulkSmsCreditsCollection, ObjectId> {

	BulkSmsCreditsCollection findByDoctorId(ObjectId objectId);

}
