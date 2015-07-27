package common.util.web;

import org.apache.commons.lang3.StringUtils;

public class DPDoctorUtils {
    public static boolean anyStringEmpty(String... values) {
	boolean result = false;
	for (String value : values) {
	    if (StringUtils.isEmpty(value)) {
		result = true;
		break;
	    }
	}
	return result;
    }

    public static boolean allStringsEmpty(String... values) {
	boolean result = true;
	for (String value : values) {
	    if (!StringUtils.isEmpty(value)) {
		result = false;
		break;
	    }
	}
	return result;
    }

    public static String getPrefixedNumber(int number) {
	String result = String.valueOf(number);
	if (number < 10) {
	    result = "0" + result;
	}
	return result;
    }
}
