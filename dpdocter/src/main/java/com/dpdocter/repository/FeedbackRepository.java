package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.FeedbackCollection;

public interface FeedbackRepository extends MongoRepository<FeedbackCollection, ObjectId> {

    @Query("{'doctorId': ?0, 'isVisible': ?1, 'updatedTime': {'$gt': ?2}}")
    List<FeedbackCollection> find(ObjectId doctorId, boolean isVisible, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'isVisible': ?1, 'updatedTime': {'$gt': ?2}}")
    List<FeedbackCollection> find(ObjectId doctorId, boolean isVisible, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isVisible': ?3, 'updatedTime': {'$gt': ?4}}")
    List<FeedbackCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean isVisible, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isVisible': ?3, 'updatedTime': {'$gt': ?4}}")
    List<FeedbackCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean isVisible, Date date, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}}")
	List<FeedbackCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
	List<FeedbackCollection> find(Date date, Sort sort);

    @Query("{'type': ?0, 'updatedTime': {'$gt': ?1}}")
	List<FeedbackCollection> findByType(String type, Date date, Pageable pageRequest);

    @Query("{'type': ?0, 'updatedTime': {'$gt': ?1}}")
	List<FeedbackCollection> findByType(String type, Date date, Sort sort);

}
