package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsTagsCollection;

@Repository
public interface RecordsTagsRepository extends MongoRepository<RecordsTagsCollection, String> {
	List<RecordsTagsCollection> findByTagsId(String tagsId);

	@Query("{'doctorId': ?0}")
	List<RecordsTagsCollection> findAll(String doctorId, Sort sort);

	@Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
	List<RecordsTagsCollection> findAll(String doctorId, Date date, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	List<RecordsTagsCollection> findAll(String doctorId, String locationId, String hospitalId, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}}")
	List<RecordsTagsCollection> findAll(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

	/*@Query("{'tagsId': ?0, 'doctorId': ?1}")
	List<RecordsTagsCollection> findAll(String tagsId, String doctorId, Sort sort);

	@Query("{'tagsId': ?0, 'doctorId': ?1, 'createdTime': {'$gte': ?2}}")
	List<RecordsTagsCollection> findAll(String tagsId, String doctorId, Date date, Sort sort);

	@Query("{'tagsId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3}")
	List<RecordsTagsCollection> findAll(String tagsId, String doctorId, String locationId, String hospitalId, Sort sort);

	@Query("{'tagsId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'createdTime': {'$gte': ?4}}")
	List<RecordsTagsCollection> findAll(String tagsId, String doctorId, String locationId, String hospitalId, Date date, Sort sort);*/

}
