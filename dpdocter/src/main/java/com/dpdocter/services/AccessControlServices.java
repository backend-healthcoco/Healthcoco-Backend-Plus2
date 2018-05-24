package com.dpdocter.services;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.AccessControl;

public interface AccessControlServices {

    AccessControl getAccessControls(ObjectId roleOrUserId, ObjectId locationId, ObjectId hospitalId);

    AccessControl setAccessControls(AccessControl accessControl);

	List<AccessControl> getAllAccessControls(Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId);

}
