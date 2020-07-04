package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;


public interface BulkSmsServices {
	
   BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request);
	
	List<BulkSmsPackage> getBulkSmsPackage(int page,int size,String searchTerm,Boolean discarded);
	
	Integer CountBulkSmsPackage(String searchTerm,Boolean discarded);
	
	BulkSmsPackage getBulkSmsPackageByDoctorId(String doctorId);

	BulkSmsCredits getCreditsByDoctorId(String doctorId);
	
	List<BulkSmsCredits> getBulkSmsHistory(int page,int size,String searchTerm,String doctorId);
	
	public BulkSmsPaymentResponse createOrder(OrderRequest request);
	
	public Boolean verifySignature(PaymentSignatureRequest request);

}
