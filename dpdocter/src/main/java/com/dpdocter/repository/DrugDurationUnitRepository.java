package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository
	extends MongoRepository<DrugDurationUnitCollection, ObjectId>, PagingAndSortingRepository<DrugDurationUnitCollection, ObjectId> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards,
	    Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getGlobalDrugDurationUnit(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards,
	    Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugDurationUnitCollection> getCustomDrugDurationUnit(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnit(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getCustomGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnitForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnitForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getCustomDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'unit' : {$regex : '^?2', $options : 'i'}}")
	List<DrugDurationUnitCollection> getGlobalDrugDurationUnitForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

}
