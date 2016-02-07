package common.util.web;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.springframework.beans.factory.annotation.Value;

public class DPDoctorUtils {

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Value(value = "${OTP_VALIDATION_TIME_DIFFERENCE}")
    private String otpTimeDifference;

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

    public static String formatAsSolrDate(Date date) {
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	df.setTimeZone(TimeZone.getTimeZone("UTC"));
	String solrDate = df.format(date);
	return solrDate;
    }

    private static DPDoctorUtils getInstance() {
	return new DPDoctorUtils();
    }

    public static String getSHA3SecurePassword(String password) throws UnsupportedEncodingException {
	DigestSHA3 md = new DigestSHA3(256);
	md.update(password.getBytes("UTF-8"));
	byte[] digest = md.digest();

	BigInteger bigInt = new BigInteger(1, digest);
	return bigInt.toString(16);
    }

    public static String generateRandomId() {
	char[] chars = "ABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
	Random r = new Random(System.currentTimeMillis());
	char[] id = new char[8];
	for (int i = 0; i < 8; i++) {
	    id[i] = chars[r.nextInt(chars.length)];
	}
	return "H" + new String(id);
    }
}
