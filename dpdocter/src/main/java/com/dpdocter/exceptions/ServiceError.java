package com.dpdocter.exceptions;

public enum ServiceError {
	NotFound(404), Forbidden(403), InvalidInput(417), NotAuthorized(401), NotAcceptable(406), NoRecord(204),
	Unknown(500);

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
