package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ComplaintCollection;

public interface ComplaintRepository extends MongoRepository<ComplaintCollection, String>, PagingAndSortingRepository<ComplaintCollection, String> {

    @Query("{'doctorId': null, 'speciality': {$in: ?0}, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findGlobalComplaints(Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'speciality': {$in: ?0}, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findGlobalComplaints(Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded': {$in: ?4}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4}, 'discarded': {$in: ?5}},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'speciality': {$in: ?3}, 'updatedTime': {'$gt': ?4},'discarded': {$in: ?5}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': {$in: ?2}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2}, 'discarded': {$in: ?3}},{'doctorId': null, 'speciality': {$in: ?1}, 'updatedTime': {'$gt': ?2},'discarded': {$in: ?3}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Collection<String> specialities, Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findCustomGlobalComplaintsForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
    List<ComplaintCollection> findCustomGlobalComplaintsForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findCustomGlobalComplaintsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findCustomGlobalComplaintsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ComplaintCollection> findGlobalComplaintsForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ComplaintCollection> findGlobalComplaintsForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findGlobalComplaintsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findGlobalComplaintsForAdmin(Date date, String searchTerm, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ComplaintCollection> findCustomComplaintsForAdmin(Date date, boolean[] discards, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}}")
	List<ComplaintCollection> findCustomComplaintsForAdmin(Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findCustomComplaintsForAdmin(Date date, boolean[] discards, String searchTerm, Pageable pageable);

    @Query("{'doctorId': {'$ne' : null}, 'updatedTime': {'$gt': ?0}, 'discarded': {$in: ?1}, 'complaint' : {$regex : '^?2*', $options : 'i'}}")
	List<ComplaintCollection> findCustomComplaintsForAdmin(Date date, boolean[] discards, String searchTerm, Sort sort);
}
