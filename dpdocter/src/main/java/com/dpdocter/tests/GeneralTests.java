package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Timing;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.Period;

public class GeneralTests {
	public static void main(String args[]) {
		WorkingSchedule monday = new WorkingSchedule();
		WorkingSchedule tuesday = new WorkingSchedule();
		WorkingSchedule wednesday = new WorkingSchedule();

		WorkingHours workingHours = new WorkingHours();

		Timing from = new Timing();
		from.setHour("11");
		from.setPeriod(Period.AM);

		Timing to = new Timing();
		to.setHour("01");
		to.setPeriod(Period.PM);

		workingHours.setFrom(from);
		workingHours.setTo(to);

		monday.setWorkingDay(Day.MONDAY);
		monday.setWorkingHours(Arrays.asList(workingHours));

		tuesday.setWorkingDay(Day.TUESDAY);
		tuesday.setWorkingHours(Arrays.asList(workingHours));

		wednesday.setWorkingDay(Day.WEDNESDAY);
		wednesday.setWorkingHours(Arrays.asList(workingHours));

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
		location.setWorkingSchedules(Arrays.asList(monday, tuesday));

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

		clinicTiming.setWorkingSchedules(Arrays.asList(monday, tuesday, wednesday));

		System.out.println(Converter.ObjectToJSON(clinicTiming));
	}
}
