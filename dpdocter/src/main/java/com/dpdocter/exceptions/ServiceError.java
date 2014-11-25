package com.dpdocter.exceptions;

public enum ServiceError {
	Unknown(500), NoGuest(491), Duplicated(498), NotFound(497), Forbidden(496), InvalidInput(
			495), NotC2dmUser(494), C2dmServerError(493), C2dmPushError(492), NotAuthorized(
			499), NotAcceptable(406), NoRecord(480), NotVerified(481);

	private int errorCode;

	private ServiceError(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public static ServiceError getErrorByCode(int code) {
		for (ServiceError err : ServiceError.class.getEnumConstants()) {
			if (err.getErrorCode() == code) {
				return err;
			}
		}
		return null;
	}
}
