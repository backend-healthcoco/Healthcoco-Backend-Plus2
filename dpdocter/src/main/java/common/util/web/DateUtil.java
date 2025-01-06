package common.util.web;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.dpdocter.response.StartEndTImeinDateTime;
import com.dpdocter.response.StartEndTimeinDate;
import com.dpdocter.response.StartEndTimeinMillis;

public class DateUtil {
	private static final Logger LOGGER = Logger.getLogger(DateUtil.class);
	private static final String TIMEZONE = "IST";
	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone(TIMEZONE);
	private static SimpleDateFormat sdf;

	public static final int MILLISECOND = 1;
	public static final int SECOND = 1000 * MILLISECOND;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long ONE_DAY_TIME = 24 * HOUR;

	public enum TIME_RESET_ENUM {
		DAY_START, DAY_END
	};

	public static int getDaysDiffFromCurrentDateTo(Date tillDate) {
		LOGGER.info("DateUtil.getCurrentDate : Getting days difference between current date and " + tillDate);
		Calendar calendar = getCurrentDtCal(DEFAULT_TIMEZONE, TIME_RESET_ENUM.DAY_START);
		int daysDiff = Days.daysBetween(new DateTime(calendar.getTimeInMillis()), new DateTime(tillDate)).getDays();
		LOGGER.info(
				"DateUtil.getDaysDiffFromCurrentDateTo : Diff between current date and " + tillDate + "is " + daysDiff);
		return daysDiff;
	}

	public static long addDaysInCurrentDt(int daysToBeAdded, TIME_RESET_ENUM timeReset) {
		Calendar calendar = getCurrentDtCal(DEFAULT_TIMEZONE, timeReset);
		DateTime dateTime = new DateTime(calendar.getTimeInMillis());
		dateTime.plusDays(daysToBeAdded);
		return dateTime.getMillis();
	}

	public static long subtractDaysInCurrentDt(int daysToBeSubracted, TIME_RESET_ENUM timeReset) {
		Calendar calendar = getCurrentDtCal(DEFAULT_TIMEZONE, timeReset);
		DateTime dateTime = new DateTime(calendar.getTimeInMillis());
		dateTime.minusDays(daysToBeSubracted);
		return dateTime.getMillis();
	}

	public static Date addDaysInCurrentDate(int daysToBeAdded, TIME_RESET_ENUM timeReset) {
		Calendar calendar = getCurrentDtCal(DEFAULT_TIMEZONE, timeReset);
		// long aftrDaysAddMillis = calendar.getTimeInMillis() + (daysToBeAdded *
		// ONE_DAY_TIME);
		DateTime dateTime = new DateTime(calendar.getTimeInMillis());
		dateTime.plusDays(daysToBeAdded);
		return new Date(dateTime.getMillis());
	}

	public static Date subtractDaysInCurrentDate(int daysToBeSubracted, TIME_RESET_ENUM timeReset) {
		Calendar calendar = getCurrentDtCal(DEFAULT_TIMEZONE, timeReset);
		DateTime dateTime = new DateTime(calendar.getTimeInMillis());
		dateTime.minusDays(daysToBeSubracted);
		return new Date(dateTime.getMillis());
	}

	public static String convertTimeStampToDateStr(Timestamp timetsamp, DatePatternEnum pattern) {
		sdf = new SimpleDateFormat(pattern.getPattern());
		return sdf.format(new Date(timetsamp.getTime()));
	}

	private static Calendar getCurrentDtCal(TimeZone timeZone, TIME_RESET_ENUM timeReset) {
		Calendar calendar;
		if (timeZone == null)
			calendar = Calendar.getInstance(DEFAULT_TIMEZONE);
		else
			calendar = Calendar.getInstance(timeZone);

		switch (timeReset) {
		case DAY_START:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case DAY_END:
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		default:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		}
		return calendar;
	}

	public StartEndTimeinMillis getCurrentDateStartEndTime(TimeZone timeZone) {
		StartEndTimeinMillis timeinMillis = new StartEndTimeinMillis();
		Calendar calendar;
		if (timeZone == null) {
			calendar = Calendar.getInstance(DEFAULT_TIMEZONE);
		} else {
			calendar = Calendar.getInstance(timeZone);
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timeinMillis.setStartTime(calendar.getTimeInMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		timeinMillis.setEndTime(calendar.getTimeInMillis());

		return timeinMillis;
	}

	public static StartEndTimeinMillis getDateStartEndTime(TimeZone timeZone, Integer days) {
		StartEndTimeinMillis timeinMillis = new StartEndTimeinMillis();
		Calendar calendar;
		if (timeZone == null) {
			calendar = Calendar.getInstance(DEFAULT_TIMEZONE);
		} else {
			calendar = Calendar.getInstance(timeZone);
		}
		if (days != null) {
			calendar.add(Calendar.DATE, days);
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timeinMillis.setStartTime(calendar.getTimeInMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		timeinMillis.setEndTime(calendar.getTimeInMillis());

		return timeinMillis;
	}

	public static StartEndTimeinDate getDateStartEndTimeinDate(TimeZone timeZone, Integer days) {
		StartEndTimeinDate timeinDate = new StartEndTimeinDate();
		Calendar calendar;
		if (timeZone == null) {
			calendar = Calendar.getInstance(DEFAULT_TIMEZONE);
		} else {
			calendar = Calendar.getInstance(timeZone);
		}
		if (days != null) {
			calendar.add(Calendar.DATE, days);
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timeinDate.setStartDate(calendar.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		timeinDate.setEndDate(calendar.getTime());

		return timeinDate;
	}

	public static StartEndTImeinDateTime getDateStartEndTimeinDateTime(TimeZone timeZone, Integer days) {
		StartEndTImeinDateTime timeinDate = new StartEndTImeinDateTime();
		Calendar calendar;
		if (timeZone == null) {
			calendar = Calendar.getInstance(DEFAULT_TIMEZONE);
		} else {
			calendar = Calendar.getInstance(timeZone);
		}
		if (days != null) {
			calendar.add(Calendar.DATE, days);
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		timeinDate.setStartTime(new DateTime(calendar.getTimeInMillis()));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		timeinDate.setEndTime(new DateTime(calendar.getTimeInMillis()));

		return timeinDate;
	}

	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -40);
	}
}