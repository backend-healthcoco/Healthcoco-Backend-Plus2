package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;

public interface CollectionBoyDoctorAssociationRepository extends MongoRepository<CollectionBoyDoctorAssociationCollection, ObjectId>{

	
	@Query("{'dentalLabId': ?0 , 'doctorId' : ?1 , 'collectionBoyId' :?2}")
	public CollectionBoyDoctorAssociationCollection getByLocationDoctorCollectionBoy(ObjectId dentalLabId , ObjectId doctorId , ObjectId collectionBoyId);
	
	
	@Query("{'dentalLabId': ?0 , 'doctorId' : ?1}")
	public CollectionBoyDoctorAssociationCollection getByLocationDoctor(ObjectId dentalLabId , ObjectId doctorId);
	
	@Query("{'dentalLabId': ?0 , 'doctorId' : ?1 , 'isActive' : ?2}")
	public CollectionBoyDoctorAssociationCollection getByLocationDoctorIsActive(ObjectId dentalLabId , ObjectId doctorId , Boolean isActive);
	
}
