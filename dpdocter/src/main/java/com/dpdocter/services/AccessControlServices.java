package com.dpdocter.services;

import com.dpdocter.beans.AccessControl;

public interface AccessControlServices {

    AccessControl getAccessControls(String roleOrUserId, String locationId, String hospitalId);

    AccessControl setAccessControls(AccessControl accessControl);

}
