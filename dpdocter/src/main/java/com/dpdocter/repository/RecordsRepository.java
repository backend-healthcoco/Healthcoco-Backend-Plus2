package com.dpdocter.repository;

import java.util.List;

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
}
