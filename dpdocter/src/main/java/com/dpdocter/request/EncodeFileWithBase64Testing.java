package com.dpdocter.request;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class EncodeFileWithBase64Testing {
	public static void main(String[] args) throws Exception {
		String encodedFile = Base64.encodeBase64String(IOUtils.toByteArray(new FileInputStream("/home/veeraj/work/study-stuff/headfirst books/All Head First Series Ebooks Collection/Head First 2D Geometry.pdf")));
		IOUtils.write(Base64.decodeBase64(encodedFile), new FileOutputStream("/home/veeraj/new-ch.pdf"));
	    }
}
