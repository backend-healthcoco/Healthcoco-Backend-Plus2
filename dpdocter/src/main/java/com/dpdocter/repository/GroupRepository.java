package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    public List<GroupCollection> findByDoctorId(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
    public List<GroupCollection> findByDoctorId(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
    public List<GroupCollection> findByDoctorId(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, boolean discarded, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3, 'updatedTime': {'$gte': ?4}}")
    public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId, boolean discarded, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId,
			String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
	public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId,
			String hospitalId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}}")
	public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId,
			String hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3, 'updatedTime': {'$gte': ?4}}")
	public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId,
			String hospitalId, boolean discarded, Date date, Sort sort);
}
