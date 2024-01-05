package com.dpdocter.services.v2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface PrescriptionAnalyticsV2Service {

	public List<?> getMostPrescripedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);


	public Integer countPrescripedItems(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String type);

}