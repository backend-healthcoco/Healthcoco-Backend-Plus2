package com.dpdocter.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentCountAnalyticResponse;
import com.dpdocter.response.PatientAnalyticResponse;

@Service
public interface AnalyticsService {

	public List<PatientAnalyticResponse> getPatientCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm);

	public List<?> getMostPrescribedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchType, int page, int size);

	public AppointmentAnalyticResponse getAppointmentAnalyticnData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, String searchTerm, int page, int size);

	public List<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticnData(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String queryType, String searchType,
			String searchTerm, int page, int size);

	public List<AppointmentCountAnalyticResponse> getAppointmentCountAnalyticnData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, String searchTerm,
			int page, int size);

}
