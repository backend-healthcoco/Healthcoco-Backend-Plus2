package com.dpdocter.services;


import com.dpdocter.beans.BankDetails;


public interface BankDetailsService {

	Boolean addEditBankDetails(BankDetails request);
	
	BankDetails getBankDetailsByDoctorId(String doctorId);
}
