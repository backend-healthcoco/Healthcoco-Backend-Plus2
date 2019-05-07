package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SpecialityCollection;

public interface SpecialityRepository extends MongoRepository<SpecialityCollection, ObjectId> {

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<SpecialityCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<SpecialityCollection> find(Date date, Sort sort);

    @Query("{'superSpeciality': {'$in': ?0}}")
	List<SpecialityCollection> findBySuperSpeciality(List<String> speciality);

    @Query("{'$or': [{'speciality': {'$in': ?0}}, {'superSpeciality': {'$in': ?0}}]}")
	List<SpecialityCollection> find(String[] specialities);

//    @Query("{'id': {'$in': ?0}}")
//	List<SpecialityCollection> findById(List<ObjectId> specialities);

}
