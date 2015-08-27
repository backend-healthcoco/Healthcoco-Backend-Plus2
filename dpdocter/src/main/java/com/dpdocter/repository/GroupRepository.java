package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.GroupCollection;

@Repository
public interface GroupRepository extends MongoRepository<GroupCollection, String>, PagingAndSortingRepository<GroupCollection, String> {
    @Query("{'doctorId': ?0}")
    public List<GroupCollection> findByDoctorId(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'isDeleted': ?1}")
    public List<GroupCollection> findByDoctorId(String doctorId, boolean isDeleted, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    public List<GroupCollection> findByDoctorId(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted': ?2}")
    public List<GroupCollection> findByDoctorId(String doctorId, Date date, boolean isDeleted, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, boolean isDeleted, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3, 'createdTime': {'$gte': ?4}}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, boolean isDeleted, Date date, Sort sort, PageRequest pageRequest);
}
