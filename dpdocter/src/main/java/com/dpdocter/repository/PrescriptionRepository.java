package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, String>, PagingAndSortingRepository<PrescriptionCollection, String> {

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, boolean[] discards,
    		boolean[] inHistorys, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, boolean[] discards,
    		boolean[] inHistorys, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
    Integer getPrescriptionCount(String doctorId, String patientId, String hospitalId, String locationId, boolean discarded);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gte' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gte' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

}
