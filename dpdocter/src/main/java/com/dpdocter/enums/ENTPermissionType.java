package com.dpdocter.enums;

public enum ENTPermissionType {

	PC_NOSE("PC_NOSE"), PC_EARS("PC_EARS"), PC_THROAT("PC_THROAT"), PC_ORAL_CAVITY("PC_ORAL_CAVITY"), NOSE_EXAM(
			"NOSE_EXAM"), NECK_EXAM("NECK_EXAM"), EARS_EXAM("EARS_EXAM"), ORAL_CAVITY_THROAT_EXAM(
					"ORAL_CAVITY_THROAT_EXAM"), INDIRECT_LARYGOSCOPY_EXAM("INDIRECT_LARYGOSCOPY_EXAM");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private ENTPermissionType(String permission) {
		this.permission = permission;
	}

}
