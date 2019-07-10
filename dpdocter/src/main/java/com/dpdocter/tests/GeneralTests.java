//package com.dpdocter.tests;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.List;
//import java.util.Scanner;
//
//import javax.xml.parsers.ParserConfigurationException;
//
////import org.xhtmlrenderer.pdf.ITextRenderer;
//import org.xml.sax.SAXException;
//
//import com.lowagie.text.DocumentException;
//
//import common.util.web.CSVUtils;
//
//public class GeneralTests {
//
//	public static void main(String args[]) throws SAXException, IOException, DocumentException, ParserConfigurationException {
//	//	patients();
////		treatmentPlans();
//		appointments();
//  }
//	
//	private static void appointments() throws IOException {
//		FileWriter fileWriter = new FileWriter("/Users/nehakariya/PractoExport/Payments.csv");
//		fileWriter.append("Date,Patient Number,Patient Name,Receipt Number,Treatment name,Amount Paid,Invoice Number,Notes,Refund,Refund Receipt Number,Refunded amount,Payment Mode,Card Number,Card Type,Cheque Number,Cheque Bank,Netbanking Bank Name,Vendor Name,Vendor Fees Percent,Cancelled");
//		fileWriter.append("\n");
//		
//		Scanner scanner = new Scanner(new File("/Users/nehakariya/Downloads/PractoExport/Payments.csv"));
//		Integer lineCount = 0;
//		
//		while (scanner.hasNext()) {
//			String csvLine = scanner.nextLine();
//			if(lineCount > 0) {
//				
//				List<String> line = CSVUtils.parseLine(csvLine);
//				String pnum = "";
//				Scanner scannerForApp = new Scanner(new File("/Users/nehakariya/PractoExport/Patients.csv"));
//		        while (scannerForApp.hasNext()) {
//		        		List<String> appLine = CSVUtils.parseLine(scannerForApp.nextLine());
////		        		System.out.println(appLine);
//		        		
//		        		String patientName = appLine.get(1).replaceAll("'","").replaceAll("\\s", "");
//		        		String patientNameInOtherFile = line.get(2).replaceAll("'","").replaceAll("\\s", "");
//		        		if (patientName.equalsIgnoreCase(patientNameInOtherFile)) {
//		        				pnum = appLine.get(0);
//		        				break;
//						}
//       		        }
//			
//				System.out.println(pnum);
//				String finalStr = "";
//				for(int i=0;i<line.size();i++) {
//					
//					if(i == 0) {
//						finalStr = finalStr + line.get(i);
//					}
//					else if(i == 1) {
//						finalStr = finalStr + ","+pnum;
//					}else {
//						finalStr = finalStr + ","+line.get(i);
//					}
//				}
//				System.out.println(csvLine);
//	            fileWriter.append(finalStr);
//	            fileWriter.append("\n");
//			}else {
//				System.out.println(lineCount);
//			}
//			lineCount = lineCount + 1;
//	  }
//		if (scanner != null) {
//			try {
//				scanner.close();
//				if (fileWriter != null) {
//					fileWriter.flush();
//					fileWriter.close();
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	
//	private static void treatmentPlans() throws IOException {
//		FileWriter fileWriter = new FileWriter("/Users/nehakariya/PractoExport/TreatmentPlans.csv");
//		fileWriter.append("Date,Patient Number,Patient Name,Doctor,Treatment Name,UnitCost,Quantity,Discount,DiscountType,Amount,Treatment Description");
//		fileWriter.append("\n");
//		
//		Scanner scanner = new Scanner(new File("/Users/nehakariya/Downloads/PractoExport/TreatmentPlans.csv"));
//		Integer lineCount = 0;
//		
//		while (scanner.hasNext()) {
//			String csvLine = scanner.nextLine();
//			if(lineCount > 0) {
//				
//				List<String> line = CSVUtils.parseLine(csvLine);
//				String pnum = "";
//				Scanner scannerForApp = new Scanner(new File("/Users/nehakariya/Patients.csv"));
//		        while (scannerForApp.hasNext()) {
//		        		List<String> appLine = CSVUtils.parseLine(scannerForApp.nextLine());
////		        		System.out.println(appLine);
//		        		
//		        		String patientName = appLine.get(1).replaceAll("'","").replaceAll("\\s", "");
//		        		String patientNameInOtherFile = line.get(2).replaceAll("'","").replaceAll("\\s", "");
//		        		if (patientName.equalsIgnoreCase(patientNameInOtherFile)) {
//		        				pnum = appLine.get(0);
//		        				break;
//						}
//       		        }
//			
//				System.out.println(pnum);
//				String finalStr = "";
//				for(int i=0;i<line.size();i++) {
//					
//					if(i == 0) {
//						finalStr = finalStr + line.get(i);
//					}
//					else if(i == 1) {
//						finalStr = finalStr + ","+pnum;
//					}else {
//						finalStr = finalStr + ","+line.get(i);
//					}
//				}
//				System.out.println(csvLine);
//	            fileWriter.append(finalStr);
//	            fileWriter.append("\n");
//			}else {
//				System.out.println(lineCount);
//			}
//			lineCount = lineCount + 1;
//	  }
//		if (scanner != null) {
//			try {
//				scanner.close();
//				if (fileWriter != null) {
//					fileWriter.flush();
//					fileWriter.close();
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	static void patients() throws IOException{
//		FileWriter fileWriter = new FileWriter("/Users/nehakariya/Patients.csv");
//		fileWriter.append("Patient Number,Patient Name,Mobile Number,Contact Number,Email Address,Secondary Mobile"
//				+ ",Gender,Address,Locality,City,Pincode,National Id,Date of Birth,Age,Anniversary Date,Blood Group,Remarks,Medical History"
//				+ ",Referred By,Groups,Patient Notes" + 
//				"");
//		fileWriter.append("\n");
//		
//		Scanner scanner = new Scanner(new File("/Users/nehakariya/Downloads/PractoExport/Patients.csv"));
//		Integer lineCount = 19;
//		
//		while (scanner.hasNext()) {
//			
//			String csvLine = scanner.nextLine();
//			if(lineCount > 19) {
//				List<String> line = CSVUtils.parseLine(csvLine);
//
//				String finalStr = "";
//				for(int i=0;i<line.size();i++) {
//					
//					if(i == 0) {
//						finalStr = finalStr +"P"+lineCount;
//					}else {
//						finalStr = finalStr + ",'"+line.get(i)+"'";
//					}
//				}
//				
//	            fileWriter.append(finalStr);
//	            fileWriter.append("\n");
//	            
//	            System.out.println("P"+lineCount);
//			    
//			}
//			lineCount = lineCount + 1;
//	}
//		System.out.println(lineCount);
//		
//		if (scanner != null) {
//			try {
//				scanner.close();
//				if (fileWriter != null) {
//					fileWriter.flush();
//					fileWriter.close();
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
// }
//}
