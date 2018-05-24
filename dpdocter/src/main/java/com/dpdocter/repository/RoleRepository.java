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
    public List<RoleCollection> findCustomRole(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}}")
    public List<RoleCollection> findCustomRole(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
    public List<RoleCollection> findCustomDoctorRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
    public List<RoleCollection> findCustomDoctorRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
    public List<RoleCollection> findCustomStaffRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
    public List<RoleCollection> findCustomStaffRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$in': ?3}}")
	public List<RoleCollection> findCustomAdminRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageable);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$in': ?3}}")
	public List<RoleCollection> findCustomAdminRole(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
	public List<RoleCollection> findCustomRoleAndNotLocationHospitalAdmin(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Pageable pageable);

    @Query("{'locationId': ?0, 'hospitalId': ?1,'updatedTime': {'$gt': ?2}, 'role':{'$nin': ?3}}")
	public List<RoleCollection> findCustomRoleAndNotLocationHospitalAdmin(ObjectId locationId, ObjectId hospitalId, Date date, List<String> roles, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    public List<RoleCollection> findByLocationIdAndHospitalId(ObjectId locationId, ObjectId hospitalId);

    @Query("{'id':?0, 'locationId': ?1, 'hospitalId': ?2}")
    public RoleCollection find(ObjectId roleId, ObjectId locationId, ObjectId hospitalId);

    @Query("{'id':{'$in' :?0}, 'role': ?1}")
	public List<RoleCollection> findByIdAndRole(Collection<ObjectId> roleIds, String role);

    @Query(value = "{'id':{$in :?0}, 'role': ?1}", count = true)
	public Integer findCountByIdAndRole(Collection<ObjectId> roleIds, String role);

    @Query("{'$or': [{'id':{$in :?0}, 'locationId': ?1, 'hospitalId': ?2},{'id':{$in :?0}, 'locationId': null, 'hospitalId': null}]}")
	public List<RoleCollection> find(Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

    @Query("{'$or': [{'role': {'$in': ?0}, 'id':{$in :?1}, 'locationId': ?2, 'hospitalId': ?3},{'role': {'$in': ?0}, 'id':{$in :?1}, 'locationId': null, 'hospitalId': null}]}")
    public List<RoleCollection> find(List<String> roles, Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

    @Query("{'$or': [{'role': {'$nin': ?0}, 'id':{$in :?1}, 'locationId': ?2, 'hospitalId': ?3}, {'role': {'$nin': ?0}, 'id':{$in :?1}, 'locationId': null, 'hospitalId': null}]}")
	public List<RoleCollection> findStaffs(List<String> roles, Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

    @Query("{'$or': [{'role': {'$in': ?0}, 'id':{$in :?1}, 'locationId': ?2, 'hospitalId': ?3},{'role': {'$in': ?0}, 'id':{$in :?1}, 'locationId': null, 'hospitalId': null}]}")
    public List<RoleCollection> findLocationHospitalAdmin(List<String> roles, Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

    @Query("{'$or': [{'role': {'$nin': ?0}, 'id':{$in :?1}, 'locationId': ?2, 'hospitalId': ?3},{'role': {'$nin': ?0}, 'id':{$in :?1}, 'locationId': null, 'hospitalId': null}]}")
    public List<RoleCollection> findNotLocationHospitalAdmin(List<String> roles, Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

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

    @Query("{'locationId': ?0, 'role': ?1}")
	public RoleCollection findLocationAdmin(ObjectId locationId, String role);

    @Query("{'locationId': {'$ne' : null}, 'hospitalId': {'$ne' : null}}")
	public List<RoleCollection> findCustomRoles();
    
    @Query("{'role':{'$in' :?0}, 'locationId': {'$exists':true}, 'hospitalId': {'$exists':true}}")
	public List<RoleCollection> findByRoles(List<String> roles);
    
    @Query("{'role':{'$in' :?0}, 'locationId': ?1, 'hospitalId': ?2}")
	public List<RoleCollection> findByRoles(List<String> roles, ObjectId locationId, ObjectId hospitalId);


}
