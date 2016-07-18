package com.dpdocter.services;

import org.bson.types.ObjectId;

import com.dpdocter.beans.AccessControl;

public interface AccessControlServices {

    AccessControl getAccessControls(ObjectId roleOrUserId, ObjectId locationId, ObjectId hospitalId);

    AccessControl setAccessControls(AccessControl accessControl);

}
