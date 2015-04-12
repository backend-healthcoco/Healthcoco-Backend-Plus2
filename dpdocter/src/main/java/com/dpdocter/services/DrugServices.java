package com.dpdocter.services;

import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;

public interface DrugServices {
	DrugAddEditResponse addDrug(DrugAddEditRequest request);

	DrugAddEditResponse editDrug(DrugAddEditRequest request);

	String deleteDrug(DrugDeleteRequest request);
}
