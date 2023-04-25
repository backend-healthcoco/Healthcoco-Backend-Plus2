package com.dpdocter.request;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class EncodeFileWithBase64Testing {
	public static void main(String[] args) throws Exception {
		String encodedFile = Base64
				.encodeBase64String(IOUtils.toByteArray(new FileInputStream("/home/veeraj/getprofile")));
		File f = new File("/home/veeraj/fff.txt");
		FileUtils.writeStringToFile(f, encodedFile);
	}
}
