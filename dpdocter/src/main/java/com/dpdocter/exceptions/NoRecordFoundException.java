package com.dpdocter.exceptions;

public class NoRecordFoundException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public NoRecordFoundException(ServiceError errorCode) {
		super(errorCode);
	}

	public NoRecordFoundException(ServiceError errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
