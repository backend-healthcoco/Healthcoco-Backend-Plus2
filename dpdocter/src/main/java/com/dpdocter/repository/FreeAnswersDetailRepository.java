/**
 * 
 */
package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FreeAnswersDetailCollection;

/**
 * @author shreshtha
 *
 */
public interface FreeAnswersDetailRepository extends MongoRepository<FreeAnswersDetailCollection, ObjectId> {

}
