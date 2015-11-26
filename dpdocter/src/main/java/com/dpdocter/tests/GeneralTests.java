package com.dpdocter.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class GeneralTests {

    
	public static void main(String[] args) throws IOException {
//    	String salt = getSalt();
//    	String securePassword = get_SHA_1_SecurePassword("DRPassword123#", salt);
//        System.out.println(securePassword);
        
//        SHA3Digest md = new SHA3Digest(); //same as DigestSHA3 md = new SHA3.Digest256();
//        md.update("secret".getBytes("UTF-8"));
//        byte[] digest = md.digest();
    	
//    	new ClassPathXmlApplicationContext("scheduler.xml");
    	System.out.println("start");
    	String host = "localhost", port="27017", dbname= "dpdocter_db" , mongoDbExportFolder="/home/suresh/";
    	
//    	String command = "mongodump --host " + host + " --port " + port
//                + " -d " + dbname + " -o " + mongoDbExportFolder ;
//    Process p = Runtime.getRuntime().exec(command);
//    System.out.println(command+"start");
    	
    	System.out.println(getTimeDiff(new Date(System.currentTimeMillis() - (6 * 60 * 60 * 1000)), new Date()));
    	
    	System.out.println(new Date(Long.parseLong("1448432566628")));
    	
    }

	public static String getTimeDiff(Date dateOne, Date dateTwo) {       
		String diff = "";        
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());       
		diff = String.format("%d hour(s) %d min(s)", TimeUnit.MILLISECONDS.toHours(timeDiff),      
				TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		return diff;
	}
    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }
    
    private static String get_SHA_1_SecurePassword(String passwordToHash, String salt) throws UnsupportedEncodingException
    {
        String generatedPassword = null;
        DigestSHA3 md = new DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
		  md.update(passwordToHash.getBytes("UTF-8"));
       	  byte[] digest = md.digest();
       	  
       	BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.toString(16);
//	    return generatedPassword;
    }
}
