package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DentalWorksInvoiceCollection;

public interface DentalWorksInvoiceRepository extends MongoRepository<DentalWorksInvoiceCollection, ObjectId> {
	@Query("{'_id':{$in : ?0}}")
	List<DentalWorksInvoiceCollection> findByIds(List<ObjectId> invoiceId);

}
