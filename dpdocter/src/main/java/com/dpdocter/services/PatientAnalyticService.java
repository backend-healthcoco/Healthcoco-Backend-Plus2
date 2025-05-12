package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.elasticsearch.response.PatientReferredByAnalyticData;
import com.dpdocter.response.AnalyticCountResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorPatientAnalyticResponse;

public interface PatientAnalyticService {

	public List<AnalyticResponse> getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String groupId, String fromDate, String toDate, String queryType, String searchType, String searchTerm);

	public Integer getPatientCount(String doctorId, String locationId, String hospitalId, String groupId,
			String fromDate, String toDate, String queryType, String searchTerm, String city);

	public List<PatientAnalyticData> getPatientData(int page, int size, String doctorId, String locationId,
			String hospitalId, String groupId, String fromDate, String toDate, String queryType, String searchTerm,
			String city);

	public DoctorPatientAnalyticResponse getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate);

	public List<AnalyticCountResponse> getPatientCountAnalytic(int size, int page, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchTerm, boolean isVisited);

	public List<PatientReferredByAnalyticData> getPatientReferredByAnalyticData(String doctorId, String locationId,
			String hospitalId, String referred, String fromDate, String toDate, String queryType,
			String searchTerm, int page, int size);
}
