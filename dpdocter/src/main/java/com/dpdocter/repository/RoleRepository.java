package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RoleCollection;

/**
 * @author veeraj
 */
@Repository
public interface RoleRepository extends MongoRepository<RoleCollection, ObjectId> {

    public RoleCollection findByRole(String role);

    public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndRoleNotIn(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageRequest);

    public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndRoleNotIn(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

	public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndRoleIn(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageable);

	public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndRoleIn(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

    @Query(value = "{'role': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
	public Integer countByRole(String role, ObjectId locationId, ObjectId hospitalId);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}}]}")
	public List<RoleCollection> findCustomGlobalRole(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageable);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}}]}")
	public List<RoleCollection> findCustomGlobalRole(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role': {'$in' : ?3}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}, 'role': {'$in' : ?3}}]}")
	public List<RoleCollection> findCustomGlobalDoctorRole(ObjectId locationId, ObjectId hospitalId, Date date,	List<String> roles, Pageable pageable);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role': {'$in' : ?3}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}, 'role': {'$in' : ?3}}]}")
	public List<RoleCollection> findCustomGlobalDoctorRole(ObjectId locationId, ObjectId hospitalId, Date date,	List<String> roles, Sort sort);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}]}")
	public List<RoleCollection> findCustomGlobalStaffRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roleIds, Pageable pageable);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}},{'locationId': null, 'hospitalId': null,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}]}")
    public List<RoleCollection> findCustomGlobalStaffRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roleIds, Sort sort);

    @Query(value = "{'role': ?0}", count = true)
	public Integer countByRole(String role);

    public List<RoleCollection> findByLocationIdNotNullAndHospitalIdNotNull();
    
    public List<RoleCollection> findByRoleInAndLocationIdAndHospitalId(List<String> roles, ObjectId locationId, ObjectId hospitalId);


}
