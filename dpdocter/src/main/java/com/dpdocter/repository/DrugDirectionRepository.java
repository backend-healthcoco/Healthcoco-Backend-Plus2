package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDirectionCollection;

public interface DrugDirectionRepository extends MongoRepository<DrugDirectionCollection, ObjectId>, PagingAndSortingRepository<DrugDirectionCollection, ObjectId> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDirectionCollection> getGlobalDrugDirection(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDirectionCollection> getGlobalDrugDirection(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDirectionCollection> getCustomDrugDirection(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDirectionCollection> getCustomDrugDirection(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDirectionCollection> getCustomDrugDirection(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDirectionCollection> getCustomDrugDirection(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDirectionCollection> getCustomGlobalDrugDirection(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirectionForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirectionForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getCustomGlobalDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getCustomDrugDirectionForAdmin(Date date, boolean[] discards,	Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getCustomDrugDirectionForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getCustomDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getCustomDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getGlobalDrugDirectionForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDirectionCollection> getGlobalDrugDirectionForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getGlobalDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'direction' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDirectionCollection> getGlobalDrugDirectionForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

}
