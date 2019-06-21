package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository
		extends MongoRepository<DrugCollection, ObjectId>, PagingAndSortingRepository<DrugCollection, ObjectId> {

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<DrugCollection> getCustomDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date,
			boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getGlobalDrugs(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getGlobalDrugs(Date date, boolean[] discards, Sort sort);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
	List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date,
			boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<DrugCollection> getCustomDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date,
			boolean[] discards, Sort sort);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
	List<DrugCollection> getCustomGlobalDrugs(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, Date date,
			boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<DrugCollection> getCustomDrugs(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<DrugCollection> getCustomDrugs(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomGlobalDrugs(Date date, boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomGlobalDrugs(Date date, boolean[] discards, Sort sort);

	@Query("{'drugCode': ?0}")
	DrugCollection findByDrugCode(String drugCode);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getCustomGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getCustomDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'drugName' : {$regex : '^?2', $options : 'i'}}")
	List<DrugCollection> getGlobalDrugsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'id': ?0, 'doctorId': ?1}")
	DrugCollection findByIdAndDoctorId(ObjectId drugId, ObjectId doctorObjectId);

	@Query("{'id': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3}")
	DrugCollection find(ObjectId drugId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
	
	@Query("{'id': ?0,  'locationId': ?1, 'hospitalId': ?2}")
	List<DrugCollection> findByIdLocationIdHospitalId(ObjectId drugId, ObjectId locationId, ObjectId hospitalId);

	@Query("{'drugCode': ?0, 'doctorId': ?1}")
	DrugCollection findByCodeAndDoctorId(String drugCode, ObjectId objectId);

	@Query("{'$or': [{'drugName' : {$regex : '^?0', $options : 'i'}, 'drugType.type' : {$regex : '^?1', $options : 'i'}, 'doctorId': ?2,  'locationId': ?3, 'hospitalId': ?4},{'drugName' : {$regex : '^?0', $options : 'i'}, 'drugType.type' : {$regex : '^?1', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugCollection> findByNameAndDoctorLocationHospital(String drugName, String drugType, ObjectId doctorObjectId,
			ObjectId locationObjectId, ObjectId hospitalObjectId);

	@Query("{'locationId': ?0}")
	List<DrugCollection> findByLocationId(ObjectId locationObjectId);

	@Query("{'id': ?0, 'createdTime': {'$lt': ?0}}")
	DrugCollection findByIdAndTime(ObjectId drugId, DateTime start);

	@Query("{'drugCode': {$regex : '^?0.*', $options : 'i'}, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3}")
	DrugCollection findByStartWithDrugCode(String drugCode, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, Sort sort);

	@Query("{'drugCode': ?0, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3}")
	DrugCollection findByDrugCode(String drugCode, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	@Query("{'drugCode': ?0,  'locationId': ?1, 'hospitalId': ?2}")
	List<DrugCollection> findByDrugCodeLocationIdHospitalId(String drugCode, ObjectId locationId, ObjectId hospitalId);
}
