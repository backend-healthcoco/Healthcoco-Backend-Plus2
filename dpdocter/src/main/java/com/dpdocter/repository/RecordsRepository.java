package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsCollection;
@Repository
public interface RecordsRepository extends MongoRepository<RecordsCollection, String>,PagingAndSortingRepository<RecordsCollection, String>{
	@Query("{'patientId':?0,'doctorId':?1}")
	List<RecordsCollection> findRecords(String patientId,String doctorId);
}
