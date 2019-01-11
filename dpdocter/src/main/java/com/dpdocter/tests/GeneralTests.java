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
		
		String csvLine = null;

		Scanner scanner = new Scanner(new File("/Users/nehakariya/Downloads/PractoExport-faizan/Prescriptions.csv"));
		
		FileWriter fileWriter = new FileWriter("/Users/nehakariya/Prescriptions.csv");
//		fileWriter.append(FILE_HEADER.toString());
//		fileWriter.append("\n");
		
		while (scanner.hasNext()) {
			csvLine = scanner.nextLine();
            List<String> line = CSVUtils.parseLine(csvLine);
            String pnum = line.get(2);
            
        	Boolean found = false;

            Scanner scannerForApp = new Scanner(new File("/Users/nehakariya/Downloads/PractoExport-faizan/PatientsNotRegistered.csv"));
	        while (scannerForApp.hasNext()) {
	        	
	        		List<String> appLine = CSVUtils.parseLine(scannerForApp.nextLine());
	        		if(appLine.get(0).equalsIgnoreCase(pnum)) {
	        			csvLine = csvLine.replace(pnum, appLine.get(1));
	        			
	        			fileWriter.append(csvLine);fileWriter.append("\n");
	        			found = true;
	        			break;
	        		}
//	        		else {
//	        			csvLine = csvLine.replace(pnum, "");
//	        			
//	        			fileWriter.append(csvLine);fileWriter.append("\n");
//	        		}
   		        }
	        if(!found) {
    			csvLine = csvLine.replace(pnum, "");
    			
    			fileWriter.append(csvLine);fileWriter.append("\n");
    		}
	        scannerForApp.close();
	        found = false;
		}
		fileWriter.close();
		scanner.close();
		
		System.out.println("Done");
        
	}
}
