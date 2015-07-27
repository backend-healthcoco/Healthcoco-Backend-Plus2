package common.util.web;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PrescriptionUtils {
    public static String generatePrescriptionCode() {
	DateTime dateTime = new DateTime(DateTimeZone.UTC);
	DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMddyyyy");
	return dtfOut.print(dateTime) + RandomStringUtils.randomAlphanumeric(6);

    }
}
