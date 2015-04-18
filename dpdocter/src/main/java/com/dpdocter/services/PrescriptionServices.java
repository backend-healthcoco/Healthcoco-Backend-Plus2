package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.PrescriptionDeleteRequest;
import com.dpdocter.request.PrescriptionGetRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.request.TemplateDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionGetResponse;
import com.dpdocter.response.TemplateAddEditResponse;

public interface PrescriptionServices {
	DrugAddEditResponse addDrug(DrugAddEditRequest request);

	DrugAddEditResponse editDrug(DrugAddEditRequest request);

	Boolean deleteDrug(DrugDeleteRequest request);

	TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);

	TemplateAddEditResponse editTemplate(TemplateAddEditRequest request);

	Boolean deleteTemplate(TemplateDeleteRequest request);

	PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request);

	PrescriptionAddEditResponse editPrescription(PrescriptionAddEditRequest request);

	Boolean deletePrescription(PrescriptionDeleteRequest request);

	List<PrescriptionGetResponse> getPrescription(PrescriptionGetRequest request);
	
	DrugAddEditResponse getDrugById(String drugId);
	
	
}
