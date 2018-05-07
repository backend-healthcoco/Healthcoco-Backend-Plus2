package com.dpdocter.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xhtmlrenderer.pdf.ITextRenderer;
//import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class GeneralTests {

	public static void main(String args[]) throws SAXException, IOException, DocumentException, ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		File file = new File("/Users/nehakariya/Healthcoco Projects/pdf.pdf");
//		file.
		FileOutputStream os= new FileOutputStream(file);        
		String html = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'><html><div><h2>MLC INFORMATION</h2></div>"
				+ "<div><span style='display:block;width:100%; border-top: 1px solid #ccc;'> </span><pre>1. TO THE OFFICER ON DU\n" + 
				"TY SADAR POLICE STATION NAGPUR.</pre></div>"
				+ "<hr style= 'color: black;background-color:black;height: 1px;  border-bottom: -4px solid #rrggbb;'/>"
				+ "<div><div class='col-sm-12;'>SUBJECT :-  MLC INFORMATION <div \n" + 
				"style='float:right;padding-right:10%;'> DATE : <span style='color:black;'><b>13/03/2018</b></span> </div></div><br><hr>"
				+ "<div style='line-height: 10px;width:100%;'><pre>RESPECTED SIR,</br>THIS IS TO INFORM \n" + 
				"YOU THAT MR/MRS.<span style='color:black;'><b>Neha Pateliya</b></span> AGE <span style='color:black;'><b>$__________</b></span> RESIDENT OF <span style='color:black;'><b>$City</b></span> HAS BEEN ADMITTED\n" + 
				" TO OUR SETUP WITH THE HISTORY OF <span style='color:black;'><b>AAAAA</b></span> ON DATED <span style='color:black;'><b>13/03/2018</b></span> AT "
				+ "<span style='color:black;'><b>$Time</b></span> PM/AM. THIS \n" + 
				"REPORT IS FOR YOUR KIND INFORMATION AND NECESSARY ACTION. <br>THANKING YOU IN ANTICIPATION.</br></pre><br>"
				+ "<div style='float:right;padding-right:10%;'><span style='color:black;'><b>$__________</b></span> :</br>\n" + 
				" SIGNATURE</div></div></div></b></div><hr style= 'clear: both;color: black;background-color:black;height:1px; border-width:0;'/></html>";
		
		
		System.out.println(html.getBytes());
//		Document document= builder.parse(new ByteArrayInputStream(html.tob)); //Parse the content of the given InputStream as an XML document and return a new DOM Document object. 
		 
        ITextRenderer itxtrenderer = new ITextRenderer();
        itxtrenderer.setDocumentFromString(html);
        itxtrenderer.layout();
        itxtrenderer.createPDF(os,true); 
		

        
>>>>>>> Stashed changes
	}
}
