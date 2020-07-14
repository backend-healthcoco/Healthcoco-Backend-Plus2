package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;


public interface BulkSmsServices {
	
   BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request);
	
	List<BulkSmsPackage> getBulkSmsPackage(int page,int size,String searchTerm,Boolean discarded);
	
	Integer CountBulkSmsPackage(String searchTerm,Boolean discarded);
	

	//BulkSmsCredits getCreditsByDoctorId(String doctorId);
	
	List<BulkSmsCredits> getBulkSmsHistory(int page,int size,String searchTerm,String doctorId,String locationId);
	
	
//	public BulkSmsPaymentResponse createOrder(OrderRequest request);
	
	public Boolean verifySignature(PaymentSignatureRequest request);

	BulkSmsPaymentResponse addCredits(OrderRequest request);

	//BulkSmsCredits getCreditsByDoctorIdAndLocationId(String doctorId, String locationId);

	Boolean bulkSmsCreditCheck();

	List<BulkSmsReport> getSmsReport(int page, int size, String doctorId, String locationId);

	List<BulkSmsCredits> getCreditsByDoctorIdAndLocationId(int size, int page, String searchTerm, String doctorId,
			String locationId);

}
