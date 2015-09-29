package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ComplaintCollection;

public interface ComplaintRepository extends MongoRepository<ComplaintCollection, String>, PagingAndSortingRepository<ComplaintCollection, String> {

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<ComplaintCollection> findGlobalComplaints(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<ComplaintCollection> findGlobalComplaints(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<ComplaintCollection> findGlobalComplaints(Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<ComplaintCollection> findGlobalComplaints(Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': null}")
    List<ComplaintCollection> findGlobalComplaints(Pageable pageable);

    @Query("{'doctorId': null}")
    List<ComplaintCollection> findGlobalComplaints(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<ComplaintCollection> findGlobalComplaints(Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<ComplaintCollection> findGlobalComplaints(Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2},{'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'discarded': ?1},{'doctorId': null, 'discarded': ?1}]}")
    List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Boolean discarded, Sort sort);

    Page<ComplaintCollection> findAll(Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
    List<ComplaintCollection> findCustomGlobalComplaints(Date date, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
	List<ComplaintCollection> findCustomGlobalComplaints(Date date, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<ComplaintCollection> findCustomGlobalComplaints(Date date, Boolean discarded, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<ComplaintCollection> findCustomGlobalComplaints(Date date, Boolean discarded, Sort sort);

    @Query("{'discarded': ?0}")
	List<ComplaintCollection> findCustomGlobalComplaints(Boolean discarded, Pageable pageable);

    @Query("{'discarded': ?0}")
	List<ComplaintCollection> findCustomGlobalComplaints(Boolean discarded, Sort sort);

}
