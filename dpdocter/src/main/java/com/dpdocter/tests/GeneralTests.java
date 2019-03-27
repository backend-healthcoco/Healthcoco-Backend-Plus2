package com.dpdocter.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

//import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import common.util.web.CSVUtils;

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
        
		String service = "-abc-";
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
		

		Scanner scanner = new Scanner(new File("/Users/nehakariya/Healthcoco Projects/PractoExport -rahul/Treatment.csv"));
		FileWriter file = new FileWriter(new File("/Users/nehakariya/Treatment.csv"));
		while (scanner.hasNext()) {
			String csvLine = scanner.nextLine();
            List<String> line = CSVUtils.parseLine(csvLine);
            if(line.get(1).isEmpty()) {
            	file.write(csvLine);
            }else {
            	file.write(csvLine.replaceFirst(line.get(1), "P"+line.get(1)));
            }
            
            file.write("\n");
            System.out.println(line.get(1));
         }
		scanner.close();
		file.close();
	}
}
