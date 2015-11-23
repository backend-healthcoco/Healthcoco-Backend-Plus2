package com.dpdocter.beans;

public class LocationAndAccessControl {

    private Location location;

    private AccessControl accessControl;

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public AccessControl getAccessControl() {
	return accessControl;
    }

    public void setAccessControl(AccessControl accessControl) {
	this.accessControl = accessControl;
    }

    @Override
    public String toString() {
	return "LocationAndAccessControl [location=" + location + ", accessControl=" + accessControl + "]";
    }
}
