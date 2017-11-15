package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.TreatmentServicesCollection;

public interface TreatmentServicesRepository extends MongoRepository<TreatmentServicesCollection, ObjectId> {
	@Query("{'specialityIds' : {$in : ?0}}")
	public List<TreatmentServicesCollection> findAll(List<ObjectId> specialityIds);

	@Query("{'treatmentCode' : ?0,'doctorId':?1}")
	public TreatmentServicesCollection findbyTreatmentCodeAndDoctorId(String treatmentCode, ObjectId doctorId);

	@Query("{'$or': [{'name' : {$regex : '^?0', $options : 'i'}, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3},{'name' : {$regex : '^?0', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	public List<TreatmentServicesCollection> findByNameAndDoctorLocationHospital(String treatmentName,
			ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);

	@Query("{'locationId':?0}")
	public List<TreatmentServicesCollection> findByLocationId(ObjectId locationObjectId);

}
