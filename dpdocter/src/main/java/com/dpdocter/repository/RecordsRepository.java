package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsCollection;

@Repository
public interface RecordsRepository extends MongoRepository<RecordsCollection, String>, PagingAndSortingRepository<RecordsCollection, String> {
	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'isDeleted':?3}")
	List<RecordsCollection> findRecords(String doctorId, String locationId, String hospitalId, boolean isDeleted);

	@Query("{'id':?0}")
	RecordsCollection findByRecordId(String recordId);

	@Query(value = "{'doctorId':?0, 'hospitalId':?1, 'locationId':?2, 'isDeleted':?3}", count = true)
	Integer getRecordCount(String doctorId, String hospitalId, String locationId, boolean isDeleted);

	@Query("{'doctorId': ?0, 'isDeleted': ?1}")
	List<RecordsCollection> findAll(String doctorId, boolean isDeleted, Sort sort);

	@Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
	List<RecordsCollection> findAll(String doctorId, Date date, boolean isDeleted, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
	List<RecordsCollection> findAll(String doctorId, String locationId, String hospitalId, boolean isDeleted, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4}")
	List<RecordsCollection> findAll(String doctorId, String locationId, String hospitalId, Date date, boolean isDeleted, Sort sort);
}
