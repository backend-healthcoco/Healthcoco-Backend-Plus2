package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, ObjectId>, PagingAndSortingRepository<DrugTypeCollection, ObjectId> {

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugTypeCollection> getCustomDrugType(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugTypeCollection> getCustomDrugType(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}")
    List<DrugTypeCollection> getCustomDrugType(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}")
    List<DrugTypeCollection> getCustomDrugType(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean[] discards, Sort sort);

    @Query("{'type': ?0, 'doctorId': null, 'locationId': null, 'hospitalId': null}")
    DrugTypeCollection findByType(String string);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getCustomGlobalDrugTypeForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getCustomGlobalDrugTypeForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getCustomGlobalDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getCustomGlobalDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getCustomDrugTypeForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getCustomDrugTypeForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getCustomDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getCustomDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getGlobalDrugTypeForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugTypeCollection> getGlobalDrugTypeForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getGlobalDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'type' : {$regex : '^?2', $options : 'i'}}")
	List<DrugTypeCollection> getGlobalDrugTypeForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

}
