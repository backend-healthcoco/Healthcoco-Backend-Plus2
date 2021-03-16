package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.TransactionalSmsReport;
import com.dpdocter.response.MessageResponse;

public interface TransactionSmsServices {

	List<MessageResponse> getSmsReport(int page, int size, String doctorId, String locationId, String fromDate,
			String toDate);

	
}
