package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ComplaintCollection;

public interface ComplaintRepository extends MongoRepository<ComplaintCollection, String>, PagingAndSortingRepository<ComplaintCollection, String> {
    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
    List<ComplaintCollection> findCustomComplaints(String doctorId, String locationId, String hospitalId, boolean isDeleted, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<ComplaintCollection> findComplaints(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    List<ComplaintCollection> findComplaints(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
    List<ComplaintCollection> findComplaints(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}, 'isDeleted': ?1}")
    List<ComplaintCollection> findComplaints(Date date, boolean isDeleted, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}} , {'doctorId': null, 'createdTime': {'$gte': ?1}}]}")
	List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}},{'doctorId': null, 'createdTime': {'$gte': ?1},'isDeleted': ?2}]}")
	List<ComplaintCollection> findCustomGlobalComplaints(String doctorId, Date date, boolean isDeleted, Sort sort);
}
