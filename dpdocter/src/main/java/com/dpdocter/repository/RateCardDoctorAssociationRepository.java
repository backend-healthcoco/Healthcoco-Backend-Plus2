package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RateCardDoctorAssociationCollection;

public interface RateCardDoctorAssociationRepository extends MongoRepository<RateCardDoctorAssociationCollection, ObjectId>{

	public RateCardDoctorAssociationCollection getByDentalLabIdAndDoctorId(ObjectId dentalLabId , ObjectId doctorId);
	
	
}
