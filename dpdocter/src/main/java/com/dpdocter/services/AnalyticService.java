package com.dpdocter.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dpdocter.response.PatientAnalyticResponse;

@Service
public interface AnalyticService {

	public List<PatientAnalyticResponse> getPatientCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm);

}
