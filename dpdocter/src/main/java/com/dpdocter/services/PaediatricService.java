package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.GrowthChart;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.MasterVaccineResponse;
import com.dpdocter.response.PatientVaccineGroupedResponse;
import com.dpdocter.response.VaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineResponse;

public interface PaediatricService {

	VaccineResponse addEditVaccine(VaccineRequest request);

	VaccineResponse getVaccineById(String id);

	GrowthChart addEditGrowthChart(GrowthChart growthChart);

	GrowthChart getGrowthChartById(String id);

	Boolean discardGrowthChart(String id, Boolean discarded);

	//List<VaccineResponse> getVaccineList(String patientId, String doctorId, String locationId, String hospitalId);

	List<VaccineResponse> getVaccineList(String patientId, String doctorId, String locationId, String hospitalId,
			String updatedTime);

	List<VaccineBrandAssociationResponse> getVaccineBrandAssociation(String vaccineId, String vaccineBrandId);

	Boolean addEditMultipleVaccine(List<VaccineRequest> requests);

	Boolean addEditMultipleVaccineStatus(MultipleVaccineEditRequest request);

	List<GroupedVaccineBrandAssociationResponse> getGroupedVaccineBrandAssociation(List<String> vaccineIds);

	List<MasterVaccineResponse> getMasterVaccineList(String searchTerm, Boolean isChartVaccine, int page, int size);

	void sendBabyVaccineReminder();

	List<GrowthChart> getGrowthChartList(String patientId, String doctorId, String locationId, String hospitalId,
			String updatedTime);

	void sendBirthBabyVaccineReminder();

	Boolean updateOldPatientData();
	
	List<PatientVaccineGroupedResponse> getPatientGroupedVaccines(String patientId);

}
