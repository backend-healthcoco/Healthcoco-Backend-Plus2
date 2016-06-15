package common.util.web;

import org.apache.commons.lang3.RandomStringUtils;

public class LoginUtils {
    public static String generateOTP() {
	return RandomStringUtils.randomNumeric(6);
    }
}
