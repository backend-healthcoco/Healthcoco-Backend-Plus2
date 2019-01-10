package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.response.PatientAnalyticResponse;

public interface PatientAnalyticService {

	public List<PatientAnalyticResponse> getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String groupId, String fromDate, String toDate, String queryType, String searchType, String searchTerm);

	public Integer getPatientCount(String doctorId, String locationId, String hospitalId,String groupId, String fromDate,
			String toDate, String queryType, String searchTerm, String city);

	public List<PatientAnalyticData> getPatientData(int page, int size, String doctorId, String locationId,
			String hospitalId, String groupId, String fromDate, String toDate, String queryType, String searchTerm,
			String city);
}
