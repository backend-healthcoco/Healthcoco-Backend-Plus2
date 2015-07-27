package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorContactCollection;

@Repository
public interface DoctorContactsRepository extends MongoRepository<DoctorContactCollection, String>, PagingAndSortingRepository<DoctorContactCollection, String> {
    @Query("{'doctorId': ?0, 'isBlocked': ?1}")
    List<DoctorContactCollection> findByDoctorIdAndIsBlocked(String doctorId, boolean isBlocked, Pageable pageable);

    @Query("{'doctorId':?0,'isBlocked':?1}")
    List<DoctorContactCollection> findByDoctorIdAndIsBlocked(String doctorId, boolean isBlocked, Sort sort);

    @Query("{'doctorId':?0, 'isBlocked':?1, 'createdTime': {'$gte' : ?2}}")
    List<DoctorContactCollection> findByDoctorIdAndIsBlocked(String doctorId, boolean isBlocked, Date date, Sort sort);

    @Query("{'doctorId':?0,'contactId':?1}")
    DoctorContactCollection findByDoctorIdAndContactId(String doctorId, String contactId);

}
