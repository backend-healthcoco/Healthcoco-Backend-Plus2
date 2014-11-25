package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserCollection;
/**
 * @author veeraj
 */
@Repository
public interface UserRepository extends MongoRepository<UserCollection, String>{
	public UserCollection findByUserName(String userName);
	@Query("{'userName':?0,'password':?1}")
	public UserCollection findByUserNameAndPass(String userName,String pasword);
}
