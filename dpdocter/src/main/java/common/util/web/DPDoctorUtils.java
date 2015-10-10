package common.util.web;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class DPDoctorUtils {

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

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

    public static String getFinalImageURL(String imageURL) {
	DPDoctorUtils dpDoctorUtils = DPDoctorUtils.getInstance();
	UriInfo uriInfo = dpDoctorUtils.uriInfo;
	String imageUrlRootPath = dpDoctorUtils.imageUrlRootPath;
	String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	return finalImageURL;
    }

    private static DPDoctorUtils getInstance() {
	return new DPDoctorUtils();
    }
}
