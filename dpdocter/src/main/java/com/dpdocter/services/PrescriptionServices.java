package com.dpdocter.services;

import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.request.TemplateDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponse;

public interface PrescriptionServices {
	DrugAddEditResponse addDrug(DrugAddEditRequest request);

	DrugAddEditResponse editDrug(DrugAddEditRequest request);

	Boolean deleteDrug(DrugDeleteRequest request);

	TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);

	TemplateAddEditResponse editTemplate(TemplateAddEditRequest request);

	Boolean deleteTemplate(TemplateDeleteRequest request);
}
