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
}
