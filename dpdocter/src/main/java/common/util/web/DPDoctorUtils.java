package common.util.web;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.OrQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

public class DPDoctorUtils {

	@Context
	private UriInfo uriInfo;

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

	public static boolean anyStringEmpty(ObjectId... values) {
		boolean result = false;
		for (ObjectId value : values) {
			if (value == null) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean allStringsEmpty(ObjectId... values) {
		boolean result = true;
		for (ObjectId value : values) {
			if (value != null) {
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

	public static String formatAsSolrDate(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String solrDate = df.format(date);
		return solrDate;
	}

	public static char[] getSHA3SecurePassword(char[] password) throws UnsupportedEncodingException {
		DigestSHA3 md = new DigestSHA3(256);
		byte[] buffer = new byte[password.length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) password[i];
		}
		md.update(buffer);
		byte[] digest = md.digest();

		BigInteger bigInt = new BigInteger(1, digest);
		return bigInt.toString(16).toCharArray();
	}

	public static String generateRandomId() {
		char[] chars = "ABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random r = new Random(System.currentTimeMillis());
		char[] id = new char[8];
		for (int i = 0; i < 8; i++) {
			id[i] = chars[r.nextInt(chars.length)];
		}
		return new String(id);
	}

	public static double distance(double lat1, double lon1, double lat2, double lon2, String string) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (string == "K") {
			dist = dist * 1.609344;
		} else if (string == "N") {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public static char[] generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		char[] result = new char[salt.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (char) salt[i];
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static SearchQuery createGlobalQuery(Resource resource, int page, int size, String updatedTime,
			Boolean discarded, String sortBy, String searchTerm, Collection<String> specialities, String category,
			String disease, String... searchTermFieldName) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.mustNot(QueryBuilders.existsQuery("doctorId")).mustNot(QueryBuilders.existsQuery("locationId"))
				.mustNot(QueryBuilders.existsQuery("hospitalId"));
		if (!DPDoctorUtils.anyStringEmpty(disease))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("diseases", disease));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {

			if (searchTermFieldName[0].equalsIgnoreCase("genericNames.name")) {
				boolQueryBuilder.must(QueryBuilders.nestedQuery("genericNames",
						boolQuery().must(QueryBuilders.matchPhrasePrefixQuery("genericNames.name", searchTerm))));
			} else {
				if (searchTermFieldName.length == 1)
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
				else
					boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
			}
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (resource.equals(Resource.COMPLAINT) || resource.equals(Resource.OBSERVATION)
				|| resource.equals(Resource.INVESTIGATION) || resource.equals(Resource.DIAGNOSIS)
				|| resource.equals(Resource.NOTES) || resource.equals(Resource.PROVISIONAL_DIAGNOSIS)
				|| resource.equals(Resource.GENERAL_EXAMINATION) || resource.equals(Resource.SYSTEMIC_EXAMINATION)
				|| resource.equals(Resource.PRESENT_COMPLAINT) || resource.equals(Resource.HISTORY_OF_PRESENT_COMPLAINT)
				|| resource.equals(Resource.MENSTRUAL_HISTORY) || resource.equals(Resource.OBSTETRIC_HISTORY)
				|| resource.equals(Resource.INDICATION_OF_USG) || resource.equals(Resource.PV)
				|| resource.equals(Resource.ECG) || resource.equals(Resource.XRAY) || resource.equals(Resource.ECHO)
				|| resource.equals(Resource.HOLTER)) {
			if (specialities != null && !specialities.isEmpty()) {
				OrQueryBuilder orQueryBuilder = new OrQueryBuilder();
				orQueryBuilder.add(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("speciality")));
				for (String speciality : specialities) {
					orQueryBuilder.add(QueryBuilders.matchQuery("speciality", speciality));
				}
				boolQueryBuilder.must(QueryBuilders.orQuery(orQueryBuilder));
			} else
				boolQueryBuilder.mustNot(QueryBuilders.existsQuery("speciality"));
		}

		if (!DPDoctorUtils.anyStringEmpty(category)) {
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("categories", category));
		}
		SearchQuery searchQuery = null;
		if (resource.getType().equalsIgnoreCase(Resource.DRUG.getType())) {
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(new PageRequest(0, 50))
					.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
		} else if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		}

		return searchQuery;
	}

	public static SearchQuery createCustomQuery(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String searchTerm, String category,
			String disease, String... searchTermFieldName) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.must(QueryBuilders.termQuery("doctorId", doctorId));
		if (!DPDoctorUtils.anyStringEmpty(disease))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("diseases", disease));
		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
			boolQueryBuilder.must(QueryBuilders.termQuery("locationId", locationId))
					.must(QueryBuilders.termQuery("hospitalId", hospitalId));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {
			if (searchTermFieldName[0].equalsIgnoreCase("genericNames.name")) {
				boolQueryBuilder.must(QueryBuilders.nestedQuery("genericNames",
						boolQuery().must(QueryBuilders.matchPhrasePrefixQuery("genericNames.name", searchTerm))));
			} else {
				if (searchTermFieldName.length == 1)
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
				else
					boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
			}
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("categories", category));
		}
		SearchQuery searchQuery = null;

		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (sortBy.equalsIgnoreCase("rankingCount")) {
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(0, 50))
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.DESC)).build();
			} else {
				if (size > 0)
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
				else
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
			}
		}

		return searchQuery;
	}

	@SuppressWarnings("deprecation")
	public static SearchQuery createCustomGlobalQuery(Resource resource, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String sortBy,
			String searchTerm, Collection<String> specialities, String category, String disease,
			String... searchTermFieldName) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));

		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			boolQueryBuilder.must(
					QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")),
							QueryBuilders.termQuery("doctorId", doctorId)));

		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			boolQueryBuilder
					.must(QueryBuilders.orQuery(
							QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")),
							QueryBuilders.termQuery("locationId", locationId)))
					.must(QueryBuilders.orQuery(
							QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")),
							QueryBuilders.termQuery("hospitalId", hospitalId)));
		}
		if (!DPDoctorUtils.anyStringEmpty(disease))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("diseases", disease));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {
			if (searchTermFieldName[0].equalsIgnoreCase("genericNames.name")) {
				boolQueryBuilder.must(QueryBuilders.nestedQuery("genericNames",
						boolQuery().must(QueryBuilders.matchPhrasePrefixQuery("genericNames.name", searchTerm))));
			} else {
				if (searchTermFieldName.length == 1)
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
				else
					boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
			}
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (resource.equals(Resource.COMPLAINT) || resource.equals(Resource.OBSERVATION)
				|| resource.equals(Resource.INVESTIGATION) || resource.equals(Resource.DIAGNOSIS)
				|| resource.equals(Resource.NOTES) || resource.equals(Resource.PROVISIONAL_DIAGNOSIS)
				|| resource.equals(Resource.GENERAL_EXAMINATION) || resource.equals(Resource.SYSTEMIC_EXAMINATION)
				|| resource.equals(Resource.PRESENT_COMPLAINT) || resource.equals(Resource.HISTORY_OF_PRESENT_COMPLAINT)
				|| resource.equals(Resource.MENSTRUAL_HISTORY) || resource.equals(Resource.OBSTETRIC_HISTORY)) {
			if (specialities != null && !specialities.isEmpty()) {
				OrQueryBuilder orQueryBuilder = new OrQueryBuilder();
				orQueryBuilder.add(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("speciality")));
				for (String speciality : specialities) {
					orQueryBuilder.add(QueryBuilders.matchQuery("speciality", speciality));
				}
				boolQueryBuilder.must(QueryBuilders.orQuery(orQueryBuilder)).minimumNumberShouldMatch(1);
			} else
				boolQueryBuilder.mustNot(QueryBuilders.existsQuery("speciality"));
		}
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("categories", category));
		}
		SearchQuery searchQuery = null;
		if (resource.getType().equalsIgnoreCase(Resource.DRUG.getType())
				|| resource.getType().equalsIgnoreCase(Resource.TREATMENTSERVICE.getType())) {
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(new PageRequest(0, 80))
					.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
		} else if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		}
		return searchQuery;
	}

	public static Aggregation createGlobalAggregation(int page, int size, String updatedTime, Boolean discarded,
			String sortBy, String searchTerm, Collection<String> specialities, String disease,
			String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (specialities != null && !specialities.isEmpty())
			criteria.and("speciality").in(specialities);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("diseases").is(disease);
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createCustomAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String disease, String searchTerm,
			String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			criteria.and("doctorId").is(new ObjectId(doctorId));
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			criteria.and("locationId").is(new ObjectId(locationId));
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("diseases").is(disease);

		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createCustomGlobalAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String searchTerm,
			Collection<String> specialities, String disease, String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("diseases").is(disease);

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
								.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
						new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
			} else {
				criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
						new Criteria("doctorId").is(null));
			}
		} else
			criteria.and("doctorId").is(null);

		if (specialities != null && !specialities.isEmpty())
			criteria.and("speciality").in(specialities);
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createGlobalAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex("^" + searchTerm, "i");
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
					Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex("^" + searchTerm, "i");
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
					Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomGlobalAggregationForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String searchBy) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex("^" + searchTerm, "i");
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
					Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static void fileValidator(String fileEncoded) throws IOException {
		byte[] base64 = Base64.decodeBase64(fileEncoded);
		InputStream fis = new ByteArrayInputStream(base64);
		Integer fileSizeInMB = (base64.length / (1024 * 1024));
		if (fileSizeInMB > 10) {
			throw new BusinessException(ServiceError.NotAcceptable, "File size greater than 10 mb");
		}
		String contentType = URLConnection.guessContentTypeFromStream(fis);
		if (contentType.equalsIgnoreCase("exe")) {
			throw new BusinessException(ServiceError.NotAcceptable, "Invalid File");
		}

	}

	public static Date addmonth(Date current, int months) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + months));
		current = cal.getTime();
		return current;

	}

	public static DateTime getStartTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		int currentYear = localCalendar.get(Calendar.YEAR);

		return new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

	}

	public static Date getFormTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH);
		int currentYear = localCalendar.get(Calendar.YEAR);
		localCalendar.set(currentYear, currentMonth, currentDay, 0, 0, 0);
		return localCalendar.getTime();

	}

	public static Date getToTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH);
		int currentYear = localCalendar.get(Calendar.YEAR);
		localCalendar.set(currentYear, currentMonth, currentDay, 23, 59, 59);
		return localCalendar.getTime();

	}

	public static DateTime getEndTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		int currentYear = localCalendar.get(Calendar.YEAR);

		return new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

	}

}
