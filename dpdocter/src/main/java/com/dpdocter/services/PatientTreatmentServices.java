package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.ProductAndService;
import com.dpdocter.response.PatientTreatmentResponse;

public interface PatientTreatmentServices {

    boolean addEditProductService(ProductAndService productAndService);

    boolean addEditProductServiceCost(ProductAndService productAndService);

    List<ProductAndService> getProductsAndServices(String locationId, String hospitalId, String doctorId);

    PatientTreatmentResponse addEditPatientTreatment(String treatmentId, String locationId, String hospitalId, String doctorId,
	    List<PatientTreatment> patientTreatments);

    boolean deletePatientTreatment(String treatmentId, String locationId, String hospitalId, String doctorId);

    PatientTreatmentResponse getPatientTreatmentById(String treatmentId);

    List<PatientTreatmentResponse> getPatientTreatments(String locationId, String hospitalId, String doctorId, String patientId, int page, int size,
	    String updatedTime, Boolean discarded);

}
