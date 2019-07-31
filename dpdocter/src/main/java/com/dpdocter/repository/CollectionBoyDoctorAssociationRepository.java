package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;

public interface CollectionBoyDoctorAssociationRepository extends MongoRepository<CollectionBoyDoctorAssociationCollection, ObjectId>{

	public CollectionBoyDoctorAssociationCollection findByDentalLabIdAndDoctorIdAndCollectionBoyId(ObjectId dentalLabId , ObjectId doctorId , ObjectId collectionBoyId);
	
	public CollectionBoyDoctorAssociationCollection findByDentalLabIdAndDoctorId(ObjectId dentalLabId , ObjectId doctorId);
	
	public CollectionBoyDoctorAssociationCollection findByDentalLabIdAndDoctorIdAndIsActive(ObjectId dentalLabId , ObjectId doctorId , Boolean isActive);
	
}
