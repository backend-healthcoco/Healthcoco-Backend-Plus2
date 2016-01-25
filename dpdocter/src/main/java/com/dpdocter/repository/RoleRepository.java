package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

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
public interface RoleRepository extends MongoRepository<RoleCollection, String> {

    @Query("{'role':?0}")
    public RoleCollection findByRole(String role);

    @Query("{'role':?0, 'locationId': ?1, 'hospitalId': ?2}")
    public RoleCollection findByRole(String role, String locationId, String hospitalId);

    @Query("{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findGlobal(Date date, Pageable pageRequest);

    @Query("{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findGlobal(Date date, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}}")
    public List<RoleCollection> findCustom(String locationId, String hospitalId, Date date, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}}")
    public List<RoleCollection> findCustom(String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findCustomGlobal(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findCustomGlobal(Date date, Sort sort);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
    public List<RoleCollection> findCustomGlobal(String locationId, String hospitalId, Date date, Pageable pageRequest);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
    public List<RoleCollection> findCustomGlobal(String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    public List<RoleCollection> findByLocationIdAndHospitalId(String locationId, String hospitalId);

    @Query("{'id':?0, 'locationId': ?1, 'hospitalId': ?2}")
	public RoleCollection find(String roleId, String id, String hospitalId);

}
