package com.dpdocter.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

//import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class GeneralTests {

	public static void main(String args[]) throws SAXException, IOException, DocumentException, ParserConfigurationException {
		
//		int startMillis = 480;
//        int endMillis = 720;
//        int durationMillis = endMillis - startMillis;
//        int chunkSize = 25;
//
//        while(startMillis < endMillis) {
//            System.out.println(startMillis);
//            int cal = startMillis += chunkSize;
//            System.out.println(cal);
//            
//            System.out.println(String.format("%02d:%02d", startMillis / 60, startMillis % 60));
//            System.out.println(String.format("%02d:%02d", cal / 60, cal % 60));
//        }
        
		String service = "-abc  -";
		String slugUrl = service.toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
		
//		int countOfCharoccurence = Strings.countOccurrencesOf(slugUrl, "--");
//		if(countOfCharoccurence > 0) {
//			slugUrl = slugUrl.replaceAll("--","-");
//		}
//		char[] repeat = new char[countOfCharoccurence];
//		Arrays.fill(repeat, '-');
//		slugUrl = slugUrl.replace(repeat.toString(), "-");
			
//		slugUrl = "doctors-for-"+slugUrl;
		
//		int length = slugUrl.length();
//		char[] updatedSlug = slugUrl.toCharArray();
//		char[] newSLug = {};
//		
//		Boolean isPresent = false;
//		for(int i = 0; i<length; i++) {
//			if(updatedSlug[i] == '-') {
//				for(int j=i; j<length; j++) {
//					if(updatedSlug[j] == '-') {
//						
//					}
//				}
//			}
//		}
		
		System.out.println(slugUrl.replaceAll("-*-","-"));
//		System.out.println(Pattern.compile(slugUrl).matcher(slugUrl).replaceAll("-*-"));
		
		List<String> ne = new ArrayList<String>();
		ne.add("a");
		ne.add("b");
		ne.add("c");
		ne.add("d");
//		ne.add("e");
//		ne.add("f");
//		ne.add("g");
		
		if(ne.size()>3) {
			System.out.println("yes");
			List<String> e = ne.subList(0, 3);
			System.out.println(e);
		}
		
	}
}
