package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.GroupCollection;
@Repository
public interface GroupRepository extends MongoRepository<GroupCollection, String>{
	public List<GroupCollection> findByDoctorId(String doctorId);
}
