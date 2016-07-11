package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.InvestigationCollection;

public interface InvestigationRepository extends MongoRepository<InvestigationCollection, String>, PagingAndSortingRepository<InvestigationCollection, String> {

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'speciality': {$in: ?0}, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<InvestigationCollection> findGlobalInvestigations(Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<InvestigationCollection> findCustomInvestigations(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': null, 'speciality': {$in: ?0}, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<InvestigationCollection> findGlobalInvestigations(Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<InvestigationCollection> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<InvestigationCollection> findCustomInvestigations(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<InvestigationCollection> findCustomGlobalInvestigations(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<InvestigationCollection> findCustomGlobalInvestigationsForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<InvestigationCollection> findCustomGlobalInvestigationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findCustomGlobalInvestigationsForAdmin(Date date, boolean[] discards,	String searchTerm, Pageable pageable);

	@Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findCustomGlobalInvestigationsForAdmin(Date date, boolean[] discards,	String searchTerm, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<InvestigationCollection> findGlobalInvestigationsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<InvestigationCollection> findGlobalInvestigationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findGlobalInvestigationsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findGlobalInvestigationsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<InvestigationCollection> findCustomInvestigationsForAdmin(Date date, boolean[] discards, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<InvestigationCollection> findCustomInvestigationsForAdmin(Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findCustomInvestigationsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

	@Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'investigation' : {$regex : '^?2', $options : 'i'}}")
	List<InvestigationCollection> findCustomInvestigationsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);
}
