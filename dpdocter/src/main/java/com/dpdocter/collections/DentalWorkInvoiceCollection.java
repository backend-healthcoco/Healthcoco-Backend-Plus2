package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection = "dental_work_Invoice_cl")
public class DentalWorkInvoiceCollection extends GenericCollection {
	@Id
	private ObjectId id;
	
	@Indexed
	private ObjectId doctorId;
	
	@Field
	private ObjectId locationId;
	
	@Field
	private ObjectId hospitalId;
	
	@Field
	private String uniqueInvoiceId;
	
	@Field
	private List<ObjectId> receiptIds;
	
	@Field
	private List<ObjectId> requestId;
	
	 

}
