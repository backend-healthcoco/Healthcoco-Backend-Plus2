package com.dpdocter.beans;

import com.dpdocter.enums.Encoding;

public class SmsParts {

	 private  Encoding encoding;

	 private  String[] parts;

	    
	    
	   

		public SmsParts() {
		super();
	}

		public SmsParts(Encoding encoding, String[] parts) {
	        
			this.encoding = encoding;
	        this.parts = parts;
	    }
		
		

	    public void setEncoding(Encoding encoding) {
			this.encoding = encoding;
		}

		public void setParts(String[] parts) {
			this.parts = parts;
		}

		public Encoding getEncoding() {
	        return encoding;
	    }

	    public String[] getParts() {
	        return parts;
	    }
	
}
