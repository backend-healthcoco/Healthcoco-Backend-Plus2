package com.dpdocter.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import common.util.web.DPDoctorUtils;

public class GeneralTests {

	public static void main(String args[]) throws IOException, DocumentException {
		
//		Scanner scanner = new Scanner(new File("/Users/nehakariya/Healthcoco Projects/PractoExport-Bhutada/Patients.csv"));
//		
//		
//		while (scanner.hasNext()) {
//			String csvLine = scanner.nextLine();
//            List<String> line = CSVUtils.parseLine(csvLine);
//            String service = line.get(0).trim();
//    		String slugUrl = service.toLowerCase().trim().replaceAll("[a-zA-Z\\s\\W_]", "");
//            if(!DPDoctorUtils.anyStringEmpty(slugUrl)) System.out.println(Integer.parseInt(slugUrl));
//            
//		}
//        
//		scanner.close();
		System.out.println(DPDoctorUtils.anyStringEmpty("aa", "n"
				+ ""));
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
