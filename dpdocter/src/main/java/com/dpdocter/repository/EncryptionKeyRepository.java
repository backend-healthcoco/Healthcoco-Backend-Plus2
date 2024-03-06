package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DataEncryptionCollection;

public interface EncryptionKeyRepository extends MongoRepository<DataEncryptionCollection,ObjectId>{

	DataEncryptionCollection findByRandomReceiver(String nonce);

	DataEncryptionCollection findBySharedSenderNonce(String nonce);

}
