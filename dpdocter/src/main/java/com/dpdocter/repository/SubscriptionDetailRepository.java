package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SubscriptionDetailCollection;

@Repository
public interface SubscriptionDetailRepository extends MongoRepository<SubscriptionDetailCollection, ObjectId> {
	@Query("{'doctorId': ?0}")
	public SubscriptionDetailCollection findByDoctorId(ObjectId doctorId);

	@Query("{'locationIds': ?0}")
	public List<SubscriptionDetailCollection> findSuscriptionDetailBylocationId(ObjectId locationId);

}
