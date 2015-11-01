package com.dpdocter.repository;

import java.io.Serializable;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;

public class TestAdvancedRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements TestAdvancedRepository<T, ID> {

    private final MongoOperations mongoOperations;

    public TestAdvancedRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
	super(metadata, mongoOperations);
	this.mongoOperations = mongoOperations;
    }

    @Override
    public User findById(ID id) {
	User user = null;
	UserCollection userCollection = null;
	try {
	    userCollection = (UserCollection) findOne(id);
	    if (userCollection != null) {
		user = new User();
		BeanUtil.map(userCollection, user);
	    }
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return user;
    }

}
