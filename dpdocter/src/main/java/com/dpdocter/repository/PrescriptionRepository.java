package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, ObjectId>, PagingAndSortingRepository<PrescriptionCollection, ObjectId> {

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<PrescriptionCollection> getPrescription(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, ObjectId patientId, Date date, boolean[] discards,
	    boolean[] inHistorys, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<PrescriptionCollection> getPrescription(ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
    List<PrescriptionCollection> getPrescription(ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

    @Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
    List<PrescriptionCollection> getPrescription(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId, ObjectId patientId, Date date, boolean[] discards,
	    boolean[] inHistorys, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
    Integer getPrescriptionCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId, boolean discarded);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
    List<PrescriptionCollection> getPrescription(ObjectId doctorId, ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
    List<PrescriptionCollection> getPrescription(ObjectId doctorId, ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

    @Query("{'patientId' : ?0}")
    List<PrescriptionCollection> findAll(ObjectId patientId);

    @Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
    Integer getPrescriptionCountForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

    @Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
    Integer getPrescriptionCountForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);
    
    @Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}}")
    List<PrescriptionCollection> getPrescription(ObjectId patientId, Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}}")
    List<PrescriptionCollection> getPrescription(ObjectId patientId, Date date, boolean[] discards, Sort sort);

    @Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
    Integer getPrescriptionCount(ObjectId patientId, boolean discarded);

    @Query("{'uniqueEmrId' : ?0, 'patientId' : ?1}")
    PrescriptionCollection findByUniqueIdAndPatientId(String uniqueEmrId, ObjectId patientId);

    @Query("{'uniqueEmrId' : ?0}")
	PrescriptionCollection findByUniqueId(String uniqueEmrId);

    @Query("{'isActive' : ?0, 'items' : {$exists : true}}")
	List<PrescriptionCollection> findActiveAndDrugExistRx();

    @Query("{'isActive' : true, 'items' : {$exists : true}, 'patientId' : ?0}")
	List<PrescriptionCollection> findActiveAndDrugExistRx(ObjectId patientId);

}
