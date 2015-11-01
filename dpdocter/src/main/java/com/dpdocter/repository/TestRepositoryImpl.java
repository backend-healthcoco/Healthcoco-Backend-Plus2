package com.dpdocter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;

public class TestRepositoryImpl implements TestRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User findByUsername(String username) {
	Query query = new Query(Criteria.where("userName").is(username));
	UserCollection userCollection = null;
	User user = null;
	try {
	    userCollection = mongoTemplate.findOne(query, UserCollection.class);
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
