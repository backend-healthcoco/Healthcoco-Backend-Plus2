package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.BranchCollection;

@Repository
public interface BranchRepository extends MongoRepository<BranchCollection, ObjectId>, PagingAndSortingRepository<BranchCollection, ObjectId> {
    @Query("{'doctorId': ?0}")
    public List<BranchCollection> findByDoctorId(ObjectId doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    public List<BranchCollection> findByDoctorId(ObjectId doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}}")
    public List<BranchCollection> findByDoctorId(ObjectId doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}, 'discarded': ?2}")
    public List<BranchCollection> findByDoctorId(ObjectId doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded,
	    Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3, 'updatedTime': {'$gt': ?4}}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Date date,
	    Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3, 'updatedTime': {'$gt': ?4}}")
    public List<BranchCollection> findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Date date,
	    Sort sort);

    @Query("{'name': ?0, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'discarded': ?4}")
    public BranchCollection findByName(String name, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded);

    @Query("{'doctorId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<BranchCollection> findAll(ObjectId doctorId, boolean[] discarded, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'discarded': {$in: ?1}, 'updatedTime': {'$gt': ?2}}")
    public List<BranchCollection> findAll(ObjectId doctorId, boolean[] discarded, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': {$in: ?3}, 'updatedTime': {'$gt': ?4}}")
    public List<BranchCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean[] discarded, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': {$in: ?3}, 'updatedTime': {'$gt': ?4}}")
    public List<BranchCollection> findAll(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean[] discarded, Date date, Sort sort);

    @Query("{'id': {'$in' : ?0}, 'doctorId': ?1, 'locationId': ?2, 'hospitalId': ?3, 'discarded': ?4}")
	public List<BranchCollection> find(Collection<ObjectId> groupIds, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded);

    @Query("{'id': {'$in' : ?0}, 'doctorId': ?1, 'discarded': ?2}")
	public List<BranchCollection> find(Collection<ObjectId> groupIds, ObjectId doctorId, boolean discarded);

}
