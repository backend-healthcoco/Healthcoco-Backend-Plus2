package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;

public interface DentalImagingLocationServiceAssociationRepository extends MongoRepository<DentalImagingLocationServiceAssociationCollection, ObjectId>{

	DentalImagingLocationServiceAssociationCollection findByDentalDiagnosticServiceIdAndLocationIdAndHospitalId(ObjectId dentalDiagnosticServiceId , ObjectId locationId , ObjectId hospitalId);
	
	List<DentalImagingLocationServiceAssociationCollection> findByHospitalIdIn(List<ObjectId> hospitalId);

}
