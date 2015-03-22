package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferrencesCollection;

@Repository
public interface ReferrenceRepository extends MongoRepository<ReferrencesCollection,String>{
	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'isdeleted':?3}")
	List<ReferrencesCollection> findByDoctorIdAndLocationIdAndHospitalId(String doctorId,String locationId,String hospitalId,boolean isdeleted);
	
}
