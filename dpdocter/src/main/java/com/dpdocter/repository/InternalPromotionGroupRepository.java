package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.InternalPromotionGroupCollection;

public interface InternalPromotionGroupRepository extends MongoRepository<InternalPromotionGroupCollection, ObjectId>{

	@Query("{'promoCode' : ?0}")
	public InternalPromotionGroupCollection getByPromoCode(String promoCode);
	
}
