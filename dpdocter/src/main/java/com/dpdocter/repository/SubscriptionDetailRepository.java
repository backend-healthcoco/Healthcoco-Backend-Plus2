package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SubscriptionDetailCollection;


public interface SubscriptionDetailRepository extends MongoRepository<SubscriptionDetailCollection, ObjectId>,
		PagingAndSortingRepository<SubscriptionDetailCollection, ObjectId> {

	@Query("{'doctorId': ?0}")
	public SubscriptionDetailCollection findSuscriptionDetailByDoctorId(ObjectId doctorId);
	
	@Query("{'locations.locationId': ?0}")
	public SubscriptionDetailCollection findSuscriptionDetailByLocationId(ObjectId locationId);

}
