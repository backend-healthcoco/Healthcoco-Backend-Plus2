package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiseasesCollection;

public interface DiseasesRepository extends MongoRepository<DiseasesCollection, ObjectId>,
		PagingAndSortingRepository<DiseasesCollection, ObjectId> {

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<DiseasesCollection> findCustomDiseases(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<DiseasesCollection> findCustomDiseases(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findGlobalDiseases(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findGlobalDiseases(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<DiseasesCollection> findCustomDiseases(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<DiseasesCollection> findCustomDiseases(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomGlobalDiseases(Date date, boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomGlobalDiseases(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, String searchTerm, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Sort sort);

	@Query("{'id': {'$in': ?0}}")
	List<DiseasesCollection> findAll(List<ObjectId> diseasesIds);

	@Query("{'$or': [{'disease' : {$regex : '^?0', $options : 'i'}, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3, 'discarded': ?4},{'disease' : {$regex : '^?0', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?4}]}")
	DiseasesCollection find(String disease, ObjectId doctorObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean discarded);

}
