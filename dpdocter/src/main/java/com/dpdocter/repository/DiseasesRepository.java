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

	List<DiseasesCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Pageable pageable);

	List<DiseasesCollection> findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			Date date, boolean[] discards, Sort sort);

	List<DiseasesCollection> findByDoctorIdNullAndUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Pageable pageable);

	List<DiseasesCollection> findByDoctorIdNullAndUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Sort sort);

	List<DiseasesCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Sort sort);

	List<DiseasesCollection> findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}")
	List<DiseasesCollection> findCustomGlobalDiseases(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	List<DiseasesCollection> findByUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Pageable pageable);

	List<DiseasesCollection> findByUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiseasesCollection> findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomDiseasesForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findGlobalDiseasesForAdmin(Date date, String searchTerm, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}")
	List<DiseasesCollection> findCustomGlobalDiseasesForAdmin(Date date, boolean[] discards, String searchTerm,
			Sort sort);

	List<DiseasesCollection> findAllById(List<ObjectId> diseasesIds);

	@Query("{'$or': [{'disease' : {$regex : '^?0', $options : 'i'}, 'doctorId': ?1,  'locationId': ?2, 'hospitalId': ?3, 'discarded': ?4},{'disease' : {$regex : '^?0', $options : 'i'}, 'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?4}]}")
	DiseasesCollection find(String disease, ObjectId doctorObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean discarded);

	@Query(value = "{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}", count = true)
	Integer findGlobalDiseasesCount(Date date, boolean[] discards);

	@Query(value = "{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}", count = true)
	Integer findGlobalDiseasesForAdminCount(Date date, boolean[] discards);

	@Query(value = "{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?2}, 'disease' : {$regex : '^?1', $options : 'i'}}", count = true)
	Integer findGlobalDiseasesForAdminCount(Date date, String searchTerm, boolean[] discards);

	@Query(value = "{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}", count = true)
	Integer findCustomGlobalDiseasesForAdminCount(Date date, boolean[] discards);

	@Query(value = "{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}", count = true)
	Integer findCustomGlobalDiseasesForAdminCount(Date date, boolean[] discards, String searchTerm);

	@Query(value = "{'$or': [{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}},{'doctorId': null, 'updatedTime': {'$gt': ?1},'discarded': {$in: ?2}}]}", count = true)
	Integer findCustomGlobalDiseasesCount(ObjectId doctorObjectId, Date date, boolean[] discards);

	@Query(value = "{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?3},'discarded': {$in: ?4}}]}", count = true)
	Integer findCustomGlobalDiseasesCount(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
			Date date, boolean[] discards);

	@Query(value = "{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}", count = true)
	Integer findCustomDiseasesForAdminCount(Date date, boolean[] discards);

	@Query(value = "{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'disease' : {$regex : '^?2', $options : 'i'}}", count = true)
	Integer findCustomDiseasesForAdminCount(Date date, boolean[] discards, String searchTerm);

	@Query(value = "{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}", count = true)
	Integer findCustomDiseasesCount(ObjectId doctorObjectId, Date date, boolean[] discards);

	@Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}", count = true)
	Integer findCustomDiseasesCount(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
			Date date, boolean[] discards);

}
