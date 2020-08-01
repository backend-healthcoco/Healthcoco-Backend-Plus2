package com.dpdocter.services;

import com.dpdocter.beans.SmsParts;
import com.dpdocter.enums.Encoding;

public interface SmsSpitterServices {

	 SmsParts splitSms(String content);
	 
	 String[] splitGsm7BitEncodedMessage(String content);
	 
	 String[] splitUnicodeEncodedMessage(String content);
	 
	 Boolean isMultipartSmsLastCharGsm7BitEscapeChar(String content);
	 
	 String escapeAny7BitExtendedCharsetInContent(String message);

	Encoding getGsmEncoding(String message);
}
