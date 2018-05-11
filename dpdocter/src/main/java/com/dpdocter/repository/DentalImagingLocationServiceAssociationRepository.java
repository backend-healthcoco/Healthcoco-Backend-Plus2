package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;

public interface DentalImagingLocationServiceAssociationRepository extends MongoRepository<DentalImagingLocationServiceAssociationCollection, ObjectId>{

	@Query("{'dentalDiagnosticServiceId':?0 , 'locationId':?1 , 'hospitalId' : ?2}")
	DentalImagingLocationServiceAssociationCollection findbyServiceLocationHospital(ObjectId dentalDiagnosticServiceId , ObjectId locationId , ObjectId hospitalId);
	
	@Query("{'hospitalId' : {$in : ?0}}")
	List<DentalImagingLocationServiceAssociationCollection> findbyHospital(List<ObjectId> hospitalId);

}
