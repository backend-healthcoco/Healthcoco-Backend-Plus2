package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Timing;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.Period;

public class GeneralTests {
	public static void main(String args[]) {
		Location location = new Location();
		
		location.setAlternateNumber("9988776655");
		location.setCity("Nagpur");
		location.setCountry("India");
		location.setHospitalId("H12345");
		location.setImageUrl("imgurl");
		location.setLandmarkDetails("Near Bhole Petrol Pump");
		location.setLatitude(27.27);
		location.setLocality("Dharampeth");
		location.setLocationEmailAddress("clinic@clinics.com");
		location.setLocationName("XYZ Clinic");
		location.setLocationPhoneNumber("0712112233");
		location.setLongitude(42.52);
		location.setMobileNumber("9021703700");
		location.setPostalCode("440010");
		location.setSpecialization(Arrays.asList("ear", "nose", "throat"));
		location.setState("Maharashtra");
		location.setStreetAddress("Dharampeth");
		location.setTagLine("Test Tagline");
		location.setTwentyFourSevenOpen(false);
		location.setWebsiteUrl("websiteUrl");
		location.setWorkingDays(Arrays.asList(Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY));
		Timing from = new Timing();
		from.setHour("11");
		from.setPeriod(Period.AM);
		Timing to = new Timing();
		to.setHour("2");
		to.setPeriod(Period.PM);
		WorkingHours session1 = new WorkingHours();
		session1.setFrom(from);
		session1.setTo(to);
		location.setWorkingSession(Arrays.asList(session1));
		
		System.out.println(Converter.ObjectToJSON(location));
		
		ClinicProfile clinicProfile = new ClinicProfile();
		
		clinicProfile.setLandmarkDetails("Children Traffic Park");
		clinicProfile.setLocationEmailAddress("clinic@clinics.com");
		clinicProfile.setLocationName("XYZ Clinic");
		clinicProfile.setSpecialization(Arrays.asList("ear", "nose", "throat"));
		clinicProfile.setTagLine("Test Tagline");
		clinicProfile.setWebsiteUrl("websiteUrl");
		
		System.out.println(Converter.ObjectToJSON(clinicProfile));
		
		ClinicAddress clinicAddress = new ClinicAddress();
		
		clinicAddress.setAlternateNumber("001122334455");
		clinicAddress.setCity("Nagpur");
		clinicAddress.setCountry("India");
		clinicAddress.setLocality("Dharampeth");
		clinicAddress.setLocationPhoneNumber("0712665544");
		clinicAddress.setMobileNumber("9021703700");
		clinicAddress.setPostalCode("440012");
		clinicAddress.setState("Maharashtra");
		clinicAddress.setStreetAddress("Dharampeth");
		
		System.out.println(Converter.ObjectToJSON(clinicAddress));
		
		ClinicTiming clinicTiming = new ClinicTiming();
		
		clinicTiming.setTwentyFourSevenOpen(false);
		clinicTiming.setWorkingDays(Arrays.asList(Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY));
		Timing from2 = new Timing();
		Timing to2 = new Timing();
		from2.setHour("4");
		from2.setPeriod(Period.PM);
		to2.setHour("10");
		to2.setPeriod(Period.PM);
		WorkingHours session2 = new WorkingHours();
		session2.setFrom(from2);
		session2.setTo(to2);
		clinicTiming.setWorkingSession(Arrays.asList(session1, session2));
		
		System.out.println(Converter.ObjectToJSON(clinicTiming));
	}
}
