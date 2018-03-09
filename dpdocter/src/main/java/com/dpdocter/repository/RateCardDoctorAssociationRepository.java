package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RateCardDoctorAssociationCollection;

public interface RateCardDoctorAssociationRepository extends MongoRepository<RateCardDoctorAssociationCollection, ObjectId>{

	@Query("{'dentalLabId': ?0 , 'doctorId' : ?1}")
	public RateCardDoctorAssociationCollection getByLocationDoctor(ObjectId dentalLabId , ObjectId doctorId);
	
	
}
