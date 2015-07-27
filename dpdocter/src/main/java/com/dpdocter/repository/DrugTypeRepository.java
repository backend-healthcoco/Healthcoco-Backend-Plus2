package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String> {

    @Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}", fields = "{'dcotorId': 0, 'hospitalId': 0, 'locationId': 0, 'isDeleted': 0}")
    List<DrugTypeCollection> findByDoctorIdAndLocationIdAndHospitalIdCustomDrugType(String doctorId, String locationId, String hospitalId);

}
