package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository
		extends MongoRepository<DrugCollection, ObjectId>, PagingAndSortingRepository<DrugCollection, ObjectId> {

	DrugCollection findByDrugCode(String drugCode);

	DrugCollection findByIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId drugId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	DrugCollection findByDrugCodeAndDoctorId(String drugCode, ObjectId objectId);

	@Query("{'$or': [{'drugName' : {$regex : '^?0', $options : 'i'}, 'drugType.type' : {$regex : '^?1', $options : 'i'}, 'doctorId': ?2,  'locationId': ?3, 'hospitalId': ?4},{'drugName' : {$regex : '^?0', $options : 'i'}, 'drugType.type' : {$regex : '^?1', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugCollection> findByNameAndDoctorLocationHospital(String drugName, String drugType, ObjectId doctorObjectId,
			ObjectId locationObjectId, ObjectId hospitalObjectId);

	DrugCollection findByIdAndCreatedTime(ObjectId drugId, DateTime start);

	DrugCollection findByDrugCodeAndDoctorIdAndLocationIdAndHospitalId(String drugCode, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	List<DrugCollection> findByDrugCodeAndLocationIdAndHospitalId(String drugCode, ObjectId locationId, ObjectId hospitalId);
}
