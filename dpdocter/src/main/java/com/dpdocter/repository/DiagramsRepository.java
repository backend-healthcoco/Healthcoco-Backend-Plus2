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

import com.dpdocter.collections.DiagramsCollection;

public interface DiagramsRepository extends MongoRepository<DiagramsCollection, ObjectId>, PagingAndSortingRepository<DiagramsCollection, ObjectId> {

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'speciality': {$in: ?2}}")
    List<DiagramsCollection> findGlobalDiagrams(Date date, boolean[] discards, Collection<String> specialities, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'speciality': {$in: ?2}}")
    List<DiagramsCollection> findGlobalDiagrams(Date date, boolean[] discards, Collection<String> specialities, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<DiagramsCollection> findCustomDiagrams(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(ObjectId doctorId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<DiagramsCollection> findCustomDiagrams(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<DiagramsCollection> findCustomDiagrams(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<DiagramsCollection> findCustomDiagrams(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<DiagramsCollection> findCustomGlobalDiagrams(ObjectId doctorId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findCustomGlobalDiagramsForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findCustomGlobalDiagramsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'tags' : {$regex : '^?2', $options : 'i'}}")
	List<DiagramsCollection> findCustomGlobalDiagramsForAdmin(Date date, boolean[] discards, String searchTerm,	Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'tags' : {$regex : '^?2', $options : 'i'}}")
	List<DiagramsCollection> findCustomGlobalDiagramsForAdmin(Date date, boolean[] discards, String searchTerm,	Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findGlobalDiagramsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findGlobalDiagramsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'tags' : {$regex : '^?2', $options : 'i'}}")
	List<DiagramsCollection> findGlobalDiagramsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'tags' : {$regex : '^?2', $options : 'i'}}")
	List<DiagramsCollection> findGlobalDiagramsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findCustomDiagramsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<DiagramsCollection> findCustomDiagramsForAdmin(Date date, boolean[] discards, Sort sort);
}
