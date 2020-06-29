package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UnifiedCommunicationDetailsCollection;

public interface UnifiedCommunicationDetailsRepository extends MongoRepository<UnifiedCommunicationDetailsCollection, ObjectId> {

	List<UnifiedCommunicationDetailsCollection> findByUserIdAndTypeAndIsExpired(ObjectId userId, String type, boolean isExpired);

}
