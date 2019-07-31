package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InternalPromotionGroupCollection;

public interface InternalPromotionGroupRepository extends MongoRepository<InternalPromotionGroupCollection, ObjectId>{

	public InternalPromotionGroupCollection findByPromoCode(String promoCode);
	
}
