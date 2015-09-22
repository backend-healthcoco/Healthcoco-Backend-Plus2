package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.IssueTrackCollection;

public interface IssueTrackRepository extends MongoRepository<IssueTrackCollection, String>, PagingAndSortingRepository<IssueTrackCollection, String> {

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    List<IssueTrackCollection> find(String doctorId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    List<IssueTrackCollection> find(String doctorId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
    List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0}")
    List<IssueTrackCollection> find(String doctorId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<IssueTrackCollection> find(String doctorId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gte': ?2}}")
	List<IssueTrackCollection> find(String doctorId, String status, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'status': ?1, 'updatedTime': {'$gte': ?2}, 'discarded': ?3}")
	List<IssueTrackCollection> find(String doctorId, String status, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gte': ?4}}")
	List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, String status, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3, 'updatedTime': {'$gte': ?4}, 'discarded': ?5}")
	List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, String status, Date date, Boolean discarded, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'status': ?1}")
	List<IssueTrackCollection> find(String doctorId, String status, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'discarded': ?1, 'status': ?2}")
	List<IssueTrackCollection> find(String doctorId, Boolean discarded, String status, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'status': ?3}")
	List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, String status, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3, 'status': ?4}")
	List<IssueTrackCollection> find(String doctorId, String locationId, String hospitalId, Boolean discarded,
			String status, Sort sort, PageRequest pageRequest);

    @Query("{'status': ?0}")
	List<IssueTrackCollection> findByStatus(String status, Sort sort, PageRequest pageRequest);

}
