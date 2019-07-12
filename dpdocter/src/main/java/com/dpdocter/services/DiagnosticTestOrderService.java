package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.response.LabSearchResponse;

public interface DiagnosticTestOrderService {

	List<LabSearchResponse> searchLabs(String city, String location, String latitude, String longitude, String searchTerm, List<String> testNames, long page, int size, Boolean havePackage);

	List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots();

	OrderDiagnosticTest placeDiagnosticTestOrder(OrderDiagnosticTest request);

}
