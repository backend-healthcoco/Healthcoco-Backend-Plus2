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

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
	public List<RoleCollection> findGlobal(Date date, Pageable pageRequest);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
	public List<RoleCollection> findGlobal(Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
	public List<RoleCollection> findCustom(String doctorId, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
	public List<RoleCollection> findCustom(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gte': ?3}}")
	public List<RoleCollection> findCustom(String doctorId, String locationId, String hospitalId, Date date, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'updatedTime': {'$gte': ?3}}")
	public List<RoleCollection> findCustom(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}}")
	public List<RoleCollection> findCustomGlobal(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gte': ?0}}")
	public List<RoleCollection> findCustomGlobal(Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}} , {'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
	public List<RoleCollection> findCustomGlobal(String doctorId, Date date, Pageable pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}} , {'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
	public List<RoleCollection> findCustomGlobal(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
	public List<RoleCollection> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, Pageable pageRequest);

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
	public List<RoleCollection> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

}
