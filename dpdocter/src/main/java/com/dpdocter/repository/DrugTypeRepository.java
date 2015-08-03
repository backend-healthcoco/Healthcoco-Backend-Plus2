package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String> {

    @Query("{'isDeleted': ?2}")
    List<DrugTypeCollection> getDrugType(boolean isDeleted, Sort sort);

    @Query(value = "{'doctorId': ?0}")
    List<DrugTypeCollection> getDrugType(String doctorId, Sort sort);

    @Query("{'doctorId': ?0,'isDeleted': ?1}")
    List<DrugTypeCollection> getDrugType(String doctorId, boolean isDeleted, Sort sort);

    @Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugTypeCollection> getDrugType(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'isDeleted': ?3}")
    List<DrugTypeCollection> getDrugType(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort);

    @Query("{'createdTime': {'$gte': ?0}}")
    List<DrugTypeCollection> getDrugType(Date date, Sort sort);

    @Query("{'createdTime': {'$gte': ?0},'isDeleted': ?1}")
    List<DrugTypeCollection> getDrugType(Date date, boolean isDeleted, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
    List<DrugTypeCollection> getDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
    List<DrugTypeCollection> getDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort);

}
