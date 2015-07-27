package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferencesCollection;

@Repository
public interface ReferenceRepository extends MongoRepository<ReferencesCollection, String> {
    @Query(value = "{'$and': [ {'$or': [ {'doctorId': ?0}, {'doctorId': ''} ]}, {'$or': [ {'locationId': ?1}, {'locationId': ''} ]}, {'$or': [ {'hospitalId': ?2}, {'hospitalId': ''} ]}, {'isDeleted': ?3} ]}", fields = "{'dcotorId': 0, 'hospitalId': 0, 'locationId': 0, 'isDeleted': 0}")
    List<ReferencesCollection> findByDoctorIdAndLocationIdAndHospitalId(String doctorId, String locationId, String hospitalId, boolean isDeleted);

    @Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}", fields = "{'dcotorId': 0, 'hospitalId': 0, 'locationId': 0, 'isDeleted': 0}")
    List<ReferencesCollection> findByDoctorIdAndLocationIdAndHospitalIdCustomReferences(String doctorId, String locationId, String hospitalId, boolean isDeleted);

}
