package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalWorksInvoiceCollection;

public interface DentalWorksInvoiceRepository extends MongoRepository<DentalWorksInvoiceCollection, ObjectId> {

	List<DentalWorksInvoiceCollection> findAllByIds(List<ObjectId> invoiceId);

}
