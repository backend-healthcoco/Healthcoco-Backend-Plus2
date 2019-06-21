package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OrderDrugCollection;

@Repository
public interface OrderDrugRepository extends MongoRepository<OrderDrugCollection, ObjectId> {
	
	@Query("{'uniqueRequestId' : ?0 , 'localeId' : ?1 , 'userId' : ?2}")
	public OrderDrugCollection findByRequestIdandPharmacyId(String uniqueRequestId , ObjectId localeId , ObjectId userId);

	@Query("{'id' : ?0 , 'userId' : ?1}")
	public OrderDrugCollection findByIdAndUserId(ObjectId id, ObjectId userId);
}
