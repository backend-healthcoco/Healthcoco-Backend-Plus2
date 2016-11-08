package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorDrugCollection;

public interface DoctorDrugRepository extends MongoRepository<DoctorDrugCollection, ObjectId>, PagingAndSortingRepository<DoctorDrugCollection, ObjectId> {

	@Query("{'drugId': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId' : ?3}")
	DoctorDrugCollection findByDrugIdDoctorIdLocaationIdHospitalId(ObjectId id, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	@Query("{'drugId': ?0}")
	List<DoctorDrugCollection> findByDrugId(ObjectId id);

	
//    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
//    List<DrugCollection> getCustomDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//    List<DrugCollection> getGlobalDrugs(Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//    List<DrugCollection> getGlobalDrugs(Date date, boolean[] discards, Sort sort);
//
//    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
//    List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
//    List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
//    List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, Date date, boolean[] discards, Sort sort);
//
//    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
//    List<DrugCollection> getCustomDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);
//
//    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
//    List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date, boolean[] discards, Sort sort);
//
//    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
//    List<DrugCollection> getCustomDrugs(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
//    List<DrugCollection> getCustomDrugs(ObjectId doctorId, Date date, boolean[] discards, Sort sort);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//    List<DrugCollection> getCustomGlobalDrugs(Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//    List<DrugCollection> getCustomGlobalDrugs(Date date, boolean[] discards, Sort sort);
//
//    @Query("{'drugCode': ?0}")
//	DrugCollection findByDrugCode(String drugCode);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, Sort sort);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm,	Pageable pageable);
//
//    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);
//
//    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);
//
//    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, Sort sort);
//
//    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);
//
//    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);
//
//	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);
//
//	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
//	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, Sort sort);
//
//	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);
//
//	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
//	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

}
