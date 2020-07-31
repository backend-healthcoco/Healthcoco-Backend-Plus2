package com.dpdocter.beans;

import com.dpdocter.enums.Encoding;

public class SmsParts {

	 private final Encoding encoding;

	    private final String[] parts;

	    public SmsParts(Encoding encoding, String[] parts) {
	        this.encoding = encoding;
	        this.parts = parts;
	    }

	    public Encoding getEncoding() {
	        return encoding;
	    }

	    public String[] getParts() {
	        return parts;
	    }
	
}
