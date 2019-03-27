package com.dpdocter.tests;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

//import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class GeneralTests {

	public static void main(String args[]) throws SAXException, IOException, DocumentException, ParserConfigurationException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		sdf.format(new Date(Long.parseLong("1520286012812")));
        System.out.println(sdf.format(new Date(Long.parseLong("1520286012812"))));

	}
}
