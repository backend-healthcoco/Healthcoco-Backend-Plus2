package com.dpdocter.services;

import com.dpdocter.beans.DataDynamicUI;
import com.dpdocter.beans.DentalLabDynamicField;
import com.dpdocter.beans.DentalLabDynamicUi;
import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.KioskDynamicUi;
import com.dpdocter.beans.NutritionUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.request.KioskDynamicUiResquest;
import com.dpdocter.request.NutrirtionUIRequest;
import com.dpdocter.response.DynamicUIResponse;

public interface DynamicUIService {

	DynamicUI getPermissionForDoctor(String doctorId);

	DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest);

	UIPermissions getAllPermissionForDoctor(String doctorId);

	DynamicUIResponse getBothPermissions(String doctorId);

	UIPermissions getDefaultPermissions();

	DataDynamicUI getDynamicDataPermissionForDoctor(String doctorId);

	DataDynamicUI postDataPermissions(DataDynamicUI dynamicUIRequest);

	DentalLabDynamicUi postDentalLabPermissions(DentalLabDynamicUi request);

	DentalLabDynamicField getAllDentalLabPermissions();

	DentalLabDynamicUi getPermissionForDentalLab(String dentalLabId);

	public KioskDynamicUi getKioskUiPermission(String doctorId);

	public KioskDynamicUi addEditKioskUiPermission(KioskDynamicUiResquest request);

	public NutritionUI getNutritionUIPermission(String doctorId);

	public NutritionUI addEditNutritionUIPermission(NutrirtionUIRequest request);

	public NutritionUI getAllNutritionUIPermission();
}
