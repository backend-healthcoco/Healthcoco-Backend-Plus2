package com.dpdocter.beans;

import com.dpdocter.enums.Encoding;

public class Parts {
	private final Encoding encoding;

	private final int numberOfParts;

	public Parts(Encoding encoding, int numberOfParts) {
		this.encoding = encoding;
		this.numberOfParts = numberOfParts;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public int getNumberOfParts() {
		return numberOfParts;
	}
}
