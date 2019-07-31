package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SpecialityCollection;

public interface SpecialityRepository extends MongoRepository<SpecialityCollection, ObjectId> {

	List<SpecialityCollection> findBySuperSpecialityIn(List<String> speciality);

    @Query("{'$or': [{'speciality': {'$in': ?0}}, {'superSpeciality': {'$in': ?0}}]}")
	List<SpecialityCollection> find(String[] specialities);


}
