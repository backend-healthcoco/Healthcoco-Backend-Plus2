package com.dpdocter.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Base64Test {
	public static void main(String[] args) {

		String txt = null;
		try {
			txt = FileUtils.readFileToString(new File("/home/veeraj/pdf-file.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			IOUtils.write(Base64.decodeBase64(txt), new FileOutputStream("/home/veeraj/temp123456.pdf"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
