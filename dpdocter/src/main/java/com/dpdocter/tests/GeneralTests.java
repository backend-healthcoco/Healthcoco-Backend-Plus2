package com.dpdocter.tests;

import java.io.IOException;

import common.util.web.DPDoctorUtils;



public class GeneralTests {

//		/** The resulting PDF file. */
//	    public static final String RESULT = "/home/suresh/fontTest.pdf";
//	    /** the text to render. */
//	    public static String TEST = ".रक्त";//"હિપ્સ"+"..रक्त दैनिक"+"தமிழ்"+"abhhhh"+"हिंदी कीबोर्ड    हिन्दी महेंद्र";
//	    IndicLigaturizer g = new DevanagariLigaturizer();
//	    String processed = g.process(TEST);
//	    public static final String FONT
//        = "/home/suresh/NotoSans-Regular.ttf";
//    /** Movie information. */
//     
//		public void createPdf(String filename) throws IOException, DocumentException, JRException {
//	    	
////	    	System.out.println(g.process(""));
////	        Document document = new Document();
////	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
////	        document.open();
////	        BaseFont bf = BaseFont.createFont(
////	            "/home/suresh/NotoSansDevanagari-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
////	        Font font = new Font(bf, 20);
////	        ColumnText column = new ColumnText(writer.getDirectContent());
////	        column.setSimpleColumn(36, 730, 569, 36);
////	        column.addElement(new Paragraph(TEST, font));
////	        column.go();
////	        document.close();
//			
//			JasperCompileManager.compileReportToFile("/home/suresh/UnicodeReport.jrxml","/home/suresh/UnicodeReport.jasper");
//			JasperFillManager.fillReportToFile("/home/suresh/UnicodeReport.jasper", null, new JREmptyDataSource());
//			JasperPrintManager.printReport("/home/suresh/UnicodeReport.jrprint", true);
//			JasperExportManager.exportReportToPdfFile("/home/suresh/UnicodeReport.jrprint","/home/suresh/UnicodeReport.pdf");
//			System.out.println("DONE...");
//	    }

	    public static void main(String[] args) throws IOException{
	    	String age ="age", gender = null;
	    	if(!DPDoctorUtils.anyStringEmpty(age, gender))System.out.println(age+gender);
	    	else System.out.println(age);
	    	
//	        new GeneralTests().createPdf(RESULT);
//	        Document document = new Document();       
//	        PdfWriter writer =       
//	                        PdfWriter.getInstance(document, new FileOutputStream(RESULT));       
//	        document.open();       
//	        int w = 400;
//	        int h = 150;
//
//	        PdfContentByte cb = writer.getDirectContent();
//	        PdfTemplate tp = cb.createTemplate(w, h);
//	        Graphics2D g2 = tp.createGraphicsShapes(w, h);        
//	        g2.drawString("मराठी ग्रीटींग्स, मराठी शुभेच्छापत्रे", 20, 100);                
//	        g2.toString();
//	        System.out.println(g2.toString());
//	        cb.addTemplate(tp, 50, 400);
//	        document.close();        
//.aggregate( { $match:{ _id:{ $in:[ ObjectId("5796fc9d392d94e1619c1a3a"), ObjectId("5796fc9d392d94e1619c1a38"), ObjectId("5796fc9d392d94e1619c1a35") ] } } },
//                        { $group:{ _id:null, id:{ $push:'$_id' }, ordered:{ $first:{ $const:[ ObjectId("5796fc9d392d94e1619c1a3a"), ObjectId("5796fc9d392d94e1619c1a38"), ObjectId("5796fc9d392d94e1619c1a35") ] } } } },
//                        { $unwind:'$ordered' },
//                        { $unwind:'$id' },
//                        { $project:{ _id:'$id', matches:{ $eq:[ '$ordered', '$id' ] } } },
//                        { $match:{ matches:true } }
//) );
//	    	DBObject match = new BasicDBObject("$match",new BasicDBObject("_id", 
//	    					new BasicDBObject("$in", 
//	    							Arrays.asList(new ObjectId("5796fc9d392d94e1619c1a3a"), new ObjectId("5796fc9d392d94e1619c1a38"), new ObjectId("5796fc9d392d94e1619c1a35")))));
//	    	
//	    	DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", null).append("id", new BasicDBObject("$push", "$_id"))
//	    			.append("ordered", new BasicDBObject("$first", new BasicDBObject("$const", Arrays.asList(new ObjectId("5796fc9d392d94e1619c1a3a"), new ObjectId("5796fc9d392d94e1619c1a38"), new ObjectId("5796fc9d392d94e1619c1a35"))))));
//	    	
//	    	DBObject unwind = new BasicDBObject("$unwind", "$ordered");
//	    	DBObject unwind1 = new BasicDBObject("$unwind", "$id");
//	    	DBObject project = new BasicDBObject("$project", new BasicDBObject("_id", "$id").append("matches", new BasicDBObject("$eq", Arrays.asList("$ordered", "$id"))));
//	    	DBObject match1 = new BasicDBObject("$match", new BasicDBObject("matches", true));
//	    	
//    	System.out.println(group);
    	
//    	db.complaint_cl.aggregate( { $match:{ _id:{ $in:[ ObjectId("5796fc9d392d94e1619c1a3a"), ObjectId("5796fc9d392d94e1619c1a38"), ObjectId("5796fc9d392d94e1619c1a35") ] } } },
//                { $project:{ ranks:{ $const:[ { k:ObjectId("5796fc9d392d94e1619c1a3a"), v:0 }, { k:ObjectId("5796fc9d392d94e1619c1a38"), v:1 }, { k:ObjectId("5796fc9d392d94e1619c1a35"), v:2 } ] } } },
//                { $unwind:'$ranks' },
//                { $project:{ rank:'$ranks.v', matches:{ $eq:[ '$_id', '$ranks.k' ] } } },
//                { $match:{ matches:true } },
//                { $sort:{ rank:1 } }
//);

//    	DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", null).append("id", new BasicDBObject("$addToSet", "$_id"))
//    			.append("ordered", new BasicDBObject("$first", new BasicDBObject("$const", clinicalNotesCollection.getComplaints()))));
    //	
//    	DBObject unwind = new BasicDBObject("$unwind", "$ordered");
//    	DBObject unwind1 = new BasicDBObject("$unwind", "$id");
//    	DBObject project = new BasicDBObject("$project", new BasicDBObject("_id", "$id").append("matches", new BasicDBObject("$eq", Arrays.asList("$ordered", "$id"))));
//    	DBObject match1 = new BasicDBObject("$match", new BasicDBObject("matches", true));
    //	
    		  	
    		  	
    		    	
	    }
}  	
