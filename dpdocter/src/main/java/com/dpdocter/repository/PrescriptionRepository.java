package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, String>, PagingAndSortingRepository<PrescriptionCollection, String> {

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'discarded' : ?4}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Pageable pageable);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}, 'discarded' : ?5}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Boolean discarded,
	    Pageable pageable);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Pageable pageable);

    @Query("{'patientId' : ?0,'discarded' : ?1}")
    List<PrescriptionCollection> getPrescription(String patientId, Boolean discarded, Pageable pageable);

    @Query("{'patientId' : ?0}")
    List<PrescriptionCollection> getPrescription(String patientId, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}, 'discarded' : ?2}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Boolean discarded, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?2}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?2,'discarded' : ?3}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, boolean discarded, Pageable pageable);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3,'discarded' : ?4}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Boolean discarded, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}, 'discarded' : ?5}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Boolean discarded,
	    Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gte' : ?4}}")
    List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId, Date date, Sort sort);

    @Query("{'patientId' : ?0,'discarded' : ?1}")
    List<PrescriptionCollection> getPrescription(String patientId, Boolean discarded, Sort sort);

    @Query("{'patientId' : ?0}")
    List<PrescriptionCollection> getPrescription(String patientId, Sort sort);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}, 'discarded' : ?2}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Boolean discarded, Sort sort);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gte' : ?1}}")
    List<PrescriptionCollection> getPrescription(String patientId, Date date, Sort sort);

    @Query("{'doctorId' : ?0, 'patientId' : ?2}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Sort sort);

    @Query("{'doctorId' : ?0, 'patientId' : ?2,'discarded' : ?3}")
    List<PrescriptionCollection> getPrescription(String doctorId, String patientId, boolean discarded, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
    Integer getPrescriptionCount(String doctorId, String patientId, String hospitalId, String locationId, Boolean discarded);

    @Query("{'doctorId' : ?0, 'patientId' : ?2, 'updatedTime' : {'$gte' : ?3}}")
	List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?2, 'updatedTime' : {'$gte' : ?3}}")
	List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, Sort sort);

    @Query("{'doctorId' : ?0, 'patientId' : ?2, 'updatedTime' : {'$gte' : ?3}, 'discarded' : ?4}")
	List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?2, 'updatedTime' : {'$gte' : ?3}, 'discarded' : ?4}")
	List<PrescriptionCollection> getPrescription(String doctorId, String patientId, Date date, boolean discarded, Sort sort);

}
