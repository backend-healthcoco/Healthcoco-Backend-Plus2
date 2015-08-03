package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, String>, PagingAndSortingRepository<PrescriptionCollection, String> {
    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3,'isDeleted' : ?4}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Boolean isDeleted, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'createdTime' : {'$gte' : ?4}, 'isDeleted' : ?5}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Boolean isDeleted,
	    Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'createdTime' : {'$gte' : ?4}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Sort sort);

    @Query("{'patientId' : ?0,'isDeleted' : ?1}")
    List<PrescriptionCollection> getPrescription(String patientId, Boolean isDeleted, Sort sort);

    @Query("{'patientId' : ?0}")
    List<PrescriptionCollection> getPrescription(String patientId, Sort sort);

    @Query("{'patientId' : ?0, 'createdTime' : {'$gte' : ?1}, 'isDeleted' : ?2}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Boolean isDeleted, Sort sort);

    @Query("{'patientId' : ?0, 'createdTime' : {'$gte' : ?1}}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'isDeleted' : ?4}", count = true)
    Integer getPrescriptionCount(String doctorId, String patientId, String hospitalId, String locationId, Boolean isDeleted);

}
