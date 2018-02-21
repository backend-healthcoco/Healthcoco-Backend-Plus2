package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.DiagnosticTestPackage;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.response.LabSearchResponse;

public interface DiagnosticTestOrderService {

	List<LabSearchResponse> searchLabs(String city, String location, String latitude, String longitude, String searchTerm, List<String> testNames, int page, int size, Boolean havePackage);

	List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots(String date);

	OrderDiagnosticTest placeDiagnosticTestOrder(OrderDiagnosticTest request);

	List<OrderDiagnosticTest> getPatientOrders(String userId, int page, int size);

	List<OrderDiagnosticTest> getLabOrders(String locationId, int page, int size);

	OrderDiagnosticTest cancelOrderDiagnosticTest(String orderId, String userId);

	OrderDiagnosticTest getDiagnosticTestOrderById(String orderId, Boolean isLab, Boolean isUser);

	List<DiagnosticTestPackage> getDiagnosticTestPackages(String locationId, String hospitalId, Boolean discarded, int page, int size);

	List<DiagnosticTest> searchDiagnosticTest(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm);

}
