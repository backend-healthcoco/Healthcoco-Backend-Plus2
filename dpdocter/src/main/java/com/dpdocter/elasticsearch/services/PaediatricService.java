package com.dpdocter.elasticsearch.services;

import com.dpdocter.beans.GrowthChart;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.VaccineResponse;

public interface PaediatricService {

	VaccineResponse addEditVaccine(VaccineRequest request);

	VaccineResponse getVaccineById(String id);

	GrowthChart addEditGrowthChart(GrowthChart growthChart);

	GrowthChart getGrowthChartById(String id);

	Boolean discardGrowthChart(String id, Boolean discarded);

}
