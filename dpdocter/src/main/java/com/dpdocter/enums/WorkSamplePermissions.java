package com.dpdocter.enums;



public enum WorkSamplePermissions {

	TOOTH_NUMBER("TOOTH_NUMBER"),INSTRUCTIONS("INSTRUCTIONS"), OCCULUSAL_STAINING("OCCULUSAL_STAINING"),PONTIC_DESIGN("PONTIC_DESIGN"),COLLAR_AND_METAL_DESIGN("COLLAR_AND_METAL_DESIGN"),UNIQUE_WORK_ID("UNIQUE_WORK_ID"),SHADE("SHADE"),MATERIAL("MATERIAL"),PROCESS_STATUS("PROCESS_STATUS");
	
	
	private String permission;

	public String getPermission() {
		return permission;
	}

	private WorkSamplePermissions(String permission) {
		this.permission = permission;
	}
	
}
