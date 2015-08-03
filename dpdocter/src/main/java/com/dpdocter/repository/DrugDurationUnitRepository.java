package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository extends MongoRepository<DrugDurationUnitCollection, String> {

    @Query("{'isDeleted': ?0}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(boolean isDeleted, Sort sort);

    @Query(value = "{'doctorId': ?0}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, Sort sort);

    @Query("{'doctorId': ?0,'isDeleted': ?1}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, boolean isDeleted, Sort sort);

    @Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'isDeleted': ?3}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0},'isDeleted': ?1}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(Date date, boolean isDeleted, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1},'isDeleted': ?2}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
    List<DrugDurationUnitCollection> getDrugDurationUnit(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort);

}
