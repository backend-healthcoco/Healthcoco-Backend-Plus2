package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BulkSmsHistoryCollection;

public interface BulkSmsHistoryRepository extends MongoRepository<BulkSmsHistoryCollection, ObjectId> {

	List<BulkSmsHistoryCollection> findByDoctorId(ObjectId doctorId);

}
