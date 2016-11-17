package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ProvisionalDiagnosisCollection;

public interface ProvisionalDiagnosisRepository extends MongoRepository<ProvisionalDiagnosisCollection, ObjectId>,
		PagingAndSortingRepository<ProvisionalDiagnosisCollection, ObjectId> {

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservations(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservations(Collection<String> specialities, Date date,
			boolean[] discards, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findObservations(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<ProvisionalDiagnosisCollection> findCustomObservations(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<ProvisionalDiagnosisCollection> findCustomObservations(ObjectId doctorId, Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservations(ObjectId doctorId,
			Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservations(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'speciality': {$in: ?0}, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservations(Collection<String> specialities, Date date,
			boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findObservations(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
	List<ProvisionalDiagnosisCollection> findCustomObservations(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
	List<ProvisionalDiagnosisCollection> findCustomObservations(ObjectId doctorId, Date date, boolean[] discards,
			Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservations(ObjectId doctorId,
			Collection<String> specialities, Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservationsForAdmin(Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findCustomGlobalObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservationsForAdmin(Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findGlobalObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findCustomObservationsForAdmin(Date date, boolean[] discards,
			Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ProvisionalDiagnosisCollection> findCustomObservationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findCustomObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'provisionalDiagnosis' : {$regex : '^?2', $options : 'i'}}")
	List<ProvisionalDiagnosisCollection> findCustomObservationsForAdmin(Date date, boolean[] discards,
			String searchTerm, Sort sort);
}
