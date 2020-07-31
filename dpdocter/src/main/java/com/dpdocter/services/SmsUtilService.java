package com.dpdocter.services;

import com.dpdocter.enums.Encoding;
import com.dpdocter.beans.SmsParts;
import com.dpdocter.beans.Parts;

public interface SmsUtilService {

	Encoding getGsmEncoding(String message);
	
	Parts getNumberOfParts(String content);
	
	int getNumberOfPartsFor7BitEncoding(String content);
	
	String escapeAny7BitExtendedCharsetInContent(String message);
	
	SmsParts splitSms(String message);
}
