package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.TransactionalSmsReport;

public interface TransactionSmsServices {

	List<TransactionalSmsReport> getSmsReport(int page, int size, String doctorId, String locationId, String fromDate,
			String toDate);

	
}
