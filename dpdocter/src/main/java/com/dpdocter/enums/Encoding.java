package com.dpdocter.enums;

public enum Encoding {

	GSM_7BIT(160, 153),

	/**
	 * Encoding that is used for messages that have characters outside of the
	 * Gsm7BitCharset
	 * 
	 * @see GSM0338Charset
	 */
	GSM_UNICODE(70, 67);

	private int maxLengthSinglePart;

	/**
	 * For SMS messages that are split into multiple parts, some bytes need to be
	 * used as a header to establish a sequence for reassembly the parts when they
	 * arrive at the destination
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/User_Data_Header">UDH</a>
	 */
	private int maxLengthMultiPart;

	Encoding(int maxLengthSinglePart, int maxLengthMultiPart) {
		this.maxLengthSinglePart = maxLengthSinglePart;
		this.maxLengthMultiPart = maxLengthMultiPart;
	}

	public int getMaxLengthSinglePart() {
		return maxLengthSinglePart;
	}

	public int getMaxLengthMultiPart() {
		return maxLengthMultiPart;
	}

}
