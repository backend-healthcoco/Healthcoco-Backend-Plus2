package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.TreatmentServicesCollection;

public interface TreatmentServicesRepository extends MongoRepository<TreatmentServicesCollection, ObjectId>, PagingAndSortingRepository<TreatmentServicesCollection, ObjectId> {
	
	public TreatmentServicesCollection findByTreatmentCodeAndDoctorId(String treatmentCode, ObjectId doctorId);
	
	@Query("{'$or': [{'name' : {$regex : '^?0', $options : 'i'}, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3},{'name' : {$regex : '^?0', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	public List<TreatmentServicesCollection> findByNameAndDoctorLocationHospital(String treatmentName, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);

	@Query("{'$or': [{'name' : {$regex : '^?0', $options : 'i'}, 'locationId': ?1, 'hospitalId': ?2},{'name' : {$regex : '^?0', $options : 'i'}, 'locationId': null, 'hospitalId': null}]}")
    public List<TreatmentServicesCollection> findByNameAndLocationHospital(String treatmentName, ObjectId locationObjectId, ObjectId hospitalObjectId, Sort sort);

}
