package com.dpdocter.tests;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class GeneralTests {

//	private static Logger log = Logger.getLogger(PrescriptionApi.class.getName());
	
    public static void main(String[] args) throws ParseException, IOException {
    	
    	char[] chars = "ABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    	Random r = new Random(System.currentTimeMillis());
    	char[] id = new char[8];
    	for (int i = 0;  i < 8;  i++) {
    	    id[i] = chars[r.nextInt(chars.length)];
    	}
    	System.out.println(new String(id));
    	System.out.println(WordUtils.capitalize(("neha pateliya").toLowerCase()));
    	System.out.println(WordUtils.capitalize(("").toLowerCase()));
//    	System.out.println(WordUtils.capitalize((null).toLowerCase()));
    	/*
    	int no = 0;
		System.out.println(StringUtils.capitalize("AB Special"));
    	for(int i =0 ;i<2;i++){
    		System.out.println(++no);
    		for(int j=0;j<5;j++){
//    			System.out.println("j"+j);
    			if(j==3)break;
    		}
    	}
    	
//    	final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
//    	 
//        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File("/home/suresh/Pictures/background.jpg"));
//        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);
//          
//        final WebTarget target = client.target("http://localhost:8080/dpdocter/api/v1/records/add/");
//        final Object response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
//         
//        //Use response object to verify upload success
//         
//        formDataMultiPart.close();
//        multipart.close();
    */}
    
    
    public static void interval2(int begin, int end, int interval) {
    	  for (int time = begin; time <= end; time += interval) {
    	    System.out.println(String.format("%02d:%02d", time / 60, time % 60));
    	  }
    	}
    
    
    public static boolean backupDataWithOutDatabase(String dumpExePath, String host, String port, String database, String backupPath) {
    	boolean status = false;
    	try {
    	Process p = null;
    	 
    	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    	Date date = new Date();
    	String filepath = "backup-" + database + "-(" + dateFormat.format(date) + ").bson";
    	 
    	String batchCommand = "";
    	
    	batchCommand = dumpExePath + " -h " + host + " --port " + port + " -d "+database + " -o \"" + backupPath + "" + filepath + "\"";
    	
    	 System.out.println(batchCommand);
    	Runtime runtime = Runtime.getRuntime();
    	p = runtime.exec(batchCommand);
    	int processComplete = p.waitFor();
    	 
    	if (processComplete == 0) {
    	status = true;
//    	log.info("Backup created successfully for without DB " + database + " in " + host + ":" + port);
    	} else {
    	status = false;
//    	log.info("Could not create the backup for without DB " + database + " in " + host + ":" + port);
    	}
    	 
    	} catch (IOException ioe) {
//    	log.error(ioe, ioe.getCause());
    	} catch (Exception e) {
//    	log.error(e, e.getCause());
    	}
    	return status;
    	}
}
