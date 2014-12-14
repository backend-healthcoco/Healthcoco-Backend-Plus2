package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorContactCollection;

@Repository
public interface DoctorContactsRepository extends MongoRepository<DoctorContactCollection, String>,PagingAndSortingRepository<DoctorContactCollection, String>{
	@Query("{'docterId':?0,'isBlocked':?1}")
	List<DoctorContactCollection> findByDoctorId(String docterId,Boolean isBlocked,Pageable pageable);
}
