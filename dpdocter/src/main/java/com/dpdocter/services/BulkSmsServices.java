package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.MessageStatus;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.response.MessageResponse;

public interface BulkSmsServices {

	 BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request);
		
		List<BulkSmsPackage> getBulkSmsPackage(int page,int size,String searchTerm,Boolean discarded);
		
		Integer CountBulkSmsPackage(String searchTerm,Boolean discarded);
		
		List<BulkSmsCredits> getBulkSmsHistory(int page,int size,String searchTerm,String doctorId,String locationId);
		
		public Boolean verifySignature(PaymentSignatureRequest request);

		BulkSmsPaymentResponse addCredits(OrderRequest request);

		Boolean bulkSmsCreditCheck();

		List<MessageResponse> getSmsReport(int page, int size, String doctorId, String locationId,String messageType, String type, String status, String fromDate, String toDate);

		List<BulkSmsCredits> getCreditsByDoctorIdAndLocationId(int size, int page, String searchTerm, String doctorId,
				String locationId);

		MessageStatus getSmsStatus(String messageId);

}
