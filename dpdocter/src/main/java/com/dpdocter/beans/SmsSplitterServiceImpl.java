package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dpdocter.enums.Encoding;
import com.dpdocter.services.SmsSpitterServices;
import com.dpdocter.beans.SmsParts;
@Service
public class SmsSplitterServiceImpl implements SmsSpitterServices {

	
	@Override
	  public SmsParts splitSms(String content) {
	        Encoding encoding = getGsmEncoding(content);

	        if (encoding == Encoding.GSM_7BIT) {
	            String escapedContent = escapeAny7BitExtendedCharsetInContent(content);
	            if (escapedContent.length() <= Encoding.GSM_7BIT.getMaxLengthSinglePart()) {
	                return new SmsParts(Encoding.GSM_7BIT, new String[] { escapedContent });
	            } else {
	                return new SmsParts(Encoding.GSM_7BIT, splitGsm7BitEncodedMessage(escapedContent));
	            }
	        } else {
	            if (content.length() <= Encoding.GSM_UNICODE.getMaxLengthSinglePart()) {
	                return new SmsParts(Encoding.GSM_UNICODE, new String[] {content });
	            } else {
	                return new SmsParts(Encoding.GSM_UNICODE, splitUnicodeEncodedMessage(content));
	            }
	        }
	    }

	@Override
	     public String[] splitGsm7BitEncodedMessage(String content) {
	        List<String> parts = new ArrayList<String>();
	        StringBuilder contentString = new StringBuilder(content);

	        int maxLengthMultipart = Encoding.GSM_7BIT.getMaxLengthMultiPart();

	        while (contentString.length() > 0) {
	            if (contentString.length() >= (maxLengthMultipart)) {
	                int endPosition = maxLengthMultipart;
	                if(isMultipartSmsLastCharGsm7BitEscapeChar(contentString.toString())) {
	                    endPosition = endPosition - 1;
	                }
	                parts.add(contentString.substring(0, endPosition));
	                contentString.delete(0, endPosition);
	            } else {
	                parts.add(contentString.toString());
	                break;
	            }
	        }

	        return parts.toArray(new String[parts.size()]);
	    }

	@Override
	    public String[] splitUnicodeEncodedMessage(String content) {
	        List<String> parts = new ArrayList<String>();

	        StringBuilder contentString = new StringBuilder(content);

	        int maxLengthMultipart = Encoding.GSM_UNICODE.getMaxLengthMultiPart();

	        while (contentString.length() > 0) {
	            if (contentString.length() >= (maxLengthMultipart)) {

	                parts.add(contentString.substring(0, maxLengthMultipart));
	                contentString.delete(0, maxLengthMultipart);
	            } else {
	                parts.add(contentString.toString());
	                break;
	            }
	        }

	        return parts.toArray(new String[parts.size()]);
	    }

	    public Boolean isMultipartSmsLastCharGsm7BitEscapeChar(String content) {
	        return content.charAt(Encoding.GSM_7BIT.getMaxLengthMultiPart() - 1) == GSM0338Charset.ESCAPE_CHAR;
	    }
	    
	    @Override
	public Encoding getGsmEncoding(String message) {
	        if(! GSM0338Charset.containsOnlyCharsetCharacters(message, true)) {
	            return Encoding.GSM_UNICODE;
	        }

	        return Encoding.GSM_7BIT;
	    }
	    
	    @Override
	    public  String escapeAny7BitExtendedCharsetInContent(String message) {
	        StringBuilder content7bit = new StringBuilder();

	        for (char ch : message.toCharArray()) {

	            // Add escape characters for extended charset
	            if(GSM0338Charset.isExtendedCharsetCharacter(ch)) {
	                content7bit.append(GSM0338Charset.ESCAPE_CHAR);
	            } else {
	                if(! GSM0338Charset.isBaseCharsetCharacter(ch)) { //also not in the base charset
	                    throw new IllegalArgumentException("Message contains '" + ch + "' which is not in GSM0338Charset");
	                }
	            }

	            content7bit.append(Character.toString(ch));
	        }

	        return content7bit.toString();
	    }


	}

