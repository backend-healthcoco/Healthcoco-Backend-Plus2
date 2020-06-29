package com.dpdocter.services;

import java.util.List;


import com.dpdocter.beans.BulkSmsPackage;

public interface BulkSmsServices {
	
   BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request);
	
	List<BulkSmsPackage> getBulkSmsPackage(int page,int size,String searchTerm,Boolean discarded);
	
	Integer CountBulkSmsPackage(String searchTerm,Boolean discarded);
	
	BulkSmsPackage getBulkSmsPackageByDoctorId(String doctorId);
	
//	BulkSmsCredits getCreditsByDoctorId(String doctorId);

}
