package com.dpdocter.repository;

import java.util.Collection;
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

    @Query("{'role':?0}")
    public RoleCollection findByRole(String role);

    @Query("{'role':?0, 'locationId': ?1, 'hospitalId': ?2}")
    public RoleCollection findByRole(String role, ObjectId locationId, ObjectId hospitalId);

    @Query("{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findGlobal(Date date, Pageable pageRequest);

    @Query("{'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findGlobal(Date date, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}}")
    public List<RoleCollection> findCustom(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}}")
    public List<RoleCollection> findCustom(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query("{'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findCustomGlobal(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    public List<RoleCollection> findCustomGlobal(Date date, Sort sort);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
    public List<RoleCollection> findCustomGlobal(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    @Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
    public List<RoleCollection> findCustomGlobal(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    public List<RoleCollection> findByLocationIdAndHospitalId(ObjectId locationId, ObjectId hospitalId);

    @Query("{'id':?0, 'locationId': ?1, 'hospitalId': ?2}")
    public RoleCollection find(ObjectId roleId, ObjectId locationId, ObjectId hospitalId);

    @Query("{'id':{'$in' :?0}, 'role': ?1}")
	public List<RoleCollection> findByIdAndRole(Collection<ObjectId> roleIds, String role);

    @Query(value = "{'id':{$in :?0}, 'role': ?1}", count = true)
	public Integer findCountByIdAndRole(Collection<ObjectId> roleIds, String role);

}
