package com.dpdocter.services;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.request.DynamicUIRequest;

public interface DynamicUIService {

	DynamicUI getPermissionForDoctor(String doctorId);

	DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest);

	UIPermissions getAllPermissionForDoctor(String doctorId);

}
