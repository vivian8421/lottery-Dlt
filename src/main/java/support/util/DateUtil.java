package support.util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhuangly
 */
public class DateUtil {

	public static class DateStyle {
		public static final String YYYY_MM = "yyyy-MM";
		public static final String YYYY_MM_DD = "yyyy-MM-dd";
		public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
		public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
		public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

		public static final String YYYYMM = "yyyyMMdd";
		public static final String YYYYMMDD = "yyyyMMdd";
		public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
		public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
		public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

		private static final String ISO8601_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		private static final String TIMESTAMP_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss.S";

		public static final String CRON_STYLE = "ss mm HH dd MM ? yyyy";
	}

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();
	public static final String defaultTimeZone = "GMT+8";

	private static Locale LOCALE = new Locale("zh", "CN");
	/**
	 * 设置本地化
	 */
	public static void setLOCALE(Locale lOCALE) {
		if (lOCALE == null)
			return;
		LOCALE = lOCALE;
	}

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (DateUtil.class) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 获取日期中的某数值。如获取月份
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            日期格式
	 * @return 数值
	 */
	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance(LOCALE);
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期字符串
	 */
	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		String dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	public static boolean isDate(String date) {
		boolean isDate = false;
		if (date != null) {
			if (getDateStyle(date) != null) {
				isDate = true;
			}
		}
		return isDate;
	}

	public static String getDateStyle(String date) {
		if (StringUtil.isEmpty(date)) {
			return null;
		}
		try {
			for (Field defaultFateStyleField : ClassUtil.getClassField(DateStyle.class)) {
				String dateStyle = ClassUtil.getStaticPropertyValue(DateStyle.class, defaultFateStyleField.getName());
				ParsePosition pos = new ParsePosition(0);
				Date dateTmp = getDateFormat(dateStyle).parse(date, pos);
				if (pos.getIndex() != date.length()) {
					continue;
				}
				if (!date.equals(DateToString(dateTmp, dateStyle))) {
					continue;
				}
				return dateStyle;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static Date getNowTime() {
		return new Date();
	}
	public static Date getNowDate() {
		return StringToDate(getNowFormatDate(), DateStyle.YYYY_MM_DD);
	}

	public static String getNowFormatDate() {
		return DateToString(getNowTime(), DateStyle.YYYY_MM_DD);
	}
	public static String getNowFormatTime() {
		return DateToString(getNowTime(), DateStyle.YYYY_MM_DD_HH_MM_SS);
	}
	public static String getNowFormatTime(String dateStyle) {
		return DateToString(getNowTime(), dateStyle);
	}

	public static String getFormatDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}
	public static String getFormatTime(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS);
	}
	public static String getFormatTime(Date date, String dateStyle) {
		return DateToString(date, dateStyle);
	}

	public static Date getDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	public static Date getDateTime(int year, int month, int day, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	public static int getYear(Date date) {
		return getInteger(date, Calendar.YEAR);
	}

	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	public static int getMonth(Date date) {
		return getInteger(date, Calendar.MONTH) + 1;
	}

	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	public static int getDay(Date date) {
		return getInteger(date, Calendar.DATE);
	}

	public static int getHour(String date) {
		return getHour(StringToDate(date));
	}

	public static int getHour(Date date) {
		return getInteger(date, Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(String date) {
		return getMinute(StringToDate(date));
	}

	public static int getMinute(Date date) {
		return getInteger(date, Calendar.MINUTE);
	}

	public static int getSecond(String date) {
		return getSecond(StringToDate(date));
	}

	public static int getSecond(Date date) {
		return getInteger(date, Calendar.SECOND);
	}

	/**
	 * 星期格式化枚举
	 */
	public static enum Week {
		MONDAY("星期一", "Monday", "Mon.", 1), TUESDAY("星期二", "Tuesday", "Tues.", 2), WEDNESDAY("星期三", "Wednesday", "Wed.", 3), THURSDAY("星期四", "Thursday", "Thur.", 4), FRIDAY("星期五", "Friday", "Fri.", 5), SATURDAY("星期六", "Saturday", "Sat.", 6), SUNDAY("星期日", "Sunday", "Sun.", 7);
		public String CN;
		public String EN;
		public String ENS;
		public int NUM;
		private Week(String name_cn, String name_en, String name_enShort, int number) {
			this.CN = name_cn;
			this.EN = name_en;
			this.ENS = name_enShort;
			this.NUM = number;
		}
	}
	public static Week getWeek(Date date) {
		Week week = null;
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.setTime(date);
		int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (weekNumber) {
			case 0 :
				week = Week.SUNDAY;
				break;
			case 1 :
				week = Week.MONDAY;
				break;
			case 2 :
				week = Week.TUESDAY;
				break;
			case 3 :
				week = Week.WEDNESDAY;
				break;
			case 4 :
				week = Week.THURSDAY;
				break;
			case 5 :
				week = Week.FRIDAY;
				break;
			case 6 :
				week = Week.SATURDAY;
				break;
		}
		return week;
	}
	public static Week getWeek(String date) {
		Week week = null;
		String dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			week = getWeek(myDate);
		}
		return week;
	}

	public static String DateToString(Date date, String dateStyle) {
		if (date == null) {
			return null;
		}
		try {
			return getDateFormat(dateStyle).format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date StringToDate(String date) {
		String dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	public static Date StringToDate(String date, String dateStyle) {
		if (date == null) {
			return null;
		}
		try {
			return getDateFormat(dateStyle).parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String StringToString(String date, String dateStyle) {
		String oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, dateStyle);
	}

	public static String StringToString(String date, String oldDateStyle, String newDateStyle) {
		return DateToString(StringToDate(date, oldDateStyle), newDateStyle);
	}

	public static String CalToStringTime(Calendar calendar) {
		return getDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS).format(calendar.getTime());
	}

	public static long DateToMillis(Date date) {
		if (date == null)
			return System.currentTimeMillis();
		return date.getTime();
	}
	public static Date MillisToDate(long millis) {
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	public static String DateToISOString(Date date, String timeZoneStr) {
		TimeZone srcTimeZone = TimeZone.getTimeZone(defaultTimeZone);
		TimeZone destTimeZone = TimeZone.getTimeZone(timeZoneStr);
		Long targetTime = date.getTime() - srcTimeZone.getRawOffset() + destTimeZone.getRawOffset();
		date = new Date(targetTime);
		return DateToString(date, DateStyle.ISO8601_YYYY_MM_DD_HH_MM_SS);
	}

	public static Date ISOStringToDate(String dateStr, String timeZoneStr) {
		Date date = StringToDate(dateStr, DateStyle.ISO8601_YYYY_MM_DD_HH_MM_SS);
		TimeZone srcTimeZone = TimeZone.getTimeZone(timeZoneStr);
		TimeZone destTimeZone = TimeZone.getTimeZone(defaultTimeZone);
		Long targetTime = date.getTime() - srcTimeZone.getRawOffset() + destTimeZone.getRawOffset();
		return new Date(targetTime);
	}

	public static Date TimestampToDate(Timestamp timestamp) {
		try {
			return getDateFormat(DateStyle.TIMESTAMP_YYYY_MM_DD_HH_MM_SS).parse(timestamp.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static class index {

		/**
		 * 获取指定年第一天DateTime 2015-01-01 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		static public Date getYearFirstDateTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			if (date != null)
				calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年份第一天DateTime 2015-01-01 00:00:00
		 * 
		 * @param year
		 * @return
		 */
		static public Date getYearFirstDateTime(int year) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年份最后一天DateTime 2016-01-01 00:00:00
		 * 
		 * @param year
		 * @return
		 */
		static public Date getYearLastDateTime(int year) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.set(Calendar.YEAR, year);
			calendar.add(Calendar.YEAR, 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年月第一天DateTime 2015-10-01 00:00:00
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		static public Date getMonthFirstDateTime(int year, int month) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年月最后一天DateTime 2015-10-01 00:00:00 ~ 2015-11-01 00:00:00
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		static public Date getMonthLastDateTime(int year, int month) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			// calendar.set(Calendar.DATE,
			// calendar.getActualMaximum(Calendar.DATE));
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年月第一天DateTime 2015-10-01 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		static public Date getMonthFirstDateTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			if (date != null)
				calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定年月最后一天DateTime 2015-10-01 00:00:00 ~ 2015-11-01 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		static public Date getMonthLastDateTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			if (date != null)
				calendar.setTime(date);
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定日期星期第一天DateTime 2015-10-01 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		static public Date getWeekFirstDateTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			if (date != null)
				calendar.setTime(date);
			int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if (week == 0)
				week = 7;
			calendar.add(Calendar.DATE, 1 - week);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取指定日期星期最后时刻DateTime 2015-06-01 00:00:00 ～2015-06-08 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		static public Date getWeekLastDateTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			if (date != null)
				calendar.setTime(date);
			int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if (week == 0)
				week = 7;
			calendar.add(Calendar.DATE, 8 - week);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 获取某年的第几周的开始日期
		 * 
		 * @param year
		 * @param week
		 * @return
		 */
		public static Date getWeekFirstDayTime(int year, int week) {
			Calendar c = new GregorianCalendar();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);

			Calendar cal = (GregorianCalendar) c.clone();
			cal.add(Calendar.DATE, week * 7);

			return getWeekFirstDayTime(cal.getTime());
		}

		/**
		 * 获取某年的第几周的结束日期
		 * 
		 * @param year
		 * @param week
		 * @return
		 */
		public static Date getWeekLastDayTime(int year, int week) {
			Calendar c = new GregorianCalendar();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);

			Calendar cal = (GregorianCalendar) c.clone();
			cal.add(Calendar.DATE, week * 7);

			return getWeekLastDataTime(cal.getTime());
		}

		/**
		 * 获取当前时间所在周的开始日期
		 * 
		 * @param date
		 * @return
		 */
		public static Date getWeekFirstDayTime(Date date) {
			Calendar c = new GregorianCalendar();
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setTime(date);
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
			return c.getTime();
		}

		/**
		 * 获取当前时间所在周的结束日期
		 * 
		 * @param date
		 * @return
		 */
		public static Date getWeekLastDataTime(Date date) {
			Calendar c = new GregorianCalendar();
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setTime(date);
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
			return c.getTime();
		}

		/**
		 * 返回传入日期的最小时间，例如2015-09-30 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		public static Date getDayFirstDataTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 返回传入日期的最小时间，例如2015-09-30 00:00:00
		 * 
		 * @param date
		 * @return
		 */
		public static Date getDayFirstDataTime(String dateStr) {
			Date date = StringToDate(dateStr);
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			return calendar.getTime();
		}

		/**
		 * 返回传入日期的最大时间，例如2015-09-30 23:59:59
		 * 
		 * @param date
		 * @return
		 */
		public static Date getDayLastDataTime(Date date) {
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
		}

		/**
		 * 返回传入日期的最大时间，例如2015-09-30 23:59:59
		 * 
		 * @param dateStr
		 * @return
		 */
		public static Date getDayLastDataTime(String dateStr) {
			Date date = StringToDate(dateStr);
			Calendar calendar = Calendar.getInstance(LOCALE);
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
		}

		/**
		 * 获取当前时间所在年的周数
		 * 
		 * @param date
		 * @return
		 */
		public static int getYearWeekNum(Date date) {
			Calendar c = new GregorianCalendar();
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setMinimalDaysInFirstWeek(7);
			c.setTime(date);

			return c.get(Calendar.WEEK_OF_YEAR);
		}

		/**
		 * 获取当前时间所在年的最大周数
		 * 
		 * @param year
		 * @return
		 */
		public static int getYearWeekNum(int year) {
			Calendar c = new GregorianCalendar();
			c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);

			return getYearWeekNum(c.getTime());
		}

		/**
		 * 获取传入的日期月份有多少天
		 * 
		 * @return
		 */
		public static int getMonthMaxDay(Date date) {
			Calendar cal = Calendar.getInstance(LOCALE);
			cal.setTime(date);
			return cal.getActualMaximum(Calendar.DATE);
		}

	}
	public static class compare {

		public static boolean after(Date date, Date otherDate) {
			return compare.getIntervalTimes(date, otherDate) > 0;
		}

		public static boolean before(Date date, Date otherDate) {
			return compare.getIntervalTimes(date, otherDate) < 0;
		}

		public static boolean afterEquals(Date date, Date otherDate) {
			return compare.getIntervalTimes(date, otherDate) >= 0;
		}

		public static boolean beforEquals(Date date, Date otherDate) {
			return compare.getIntervalTimes(date, otherDate) <= 0;
		}

		public static boolean after(String date, String otherDate) {
			return compare.getIntervalTimes(StringToDate(date), StringToDate(otherDate)) > 0;
		}

		public static boolean before(String date, String otherDate) {
			return compare.getIntervalTimes(StringToDate(date), StringToDate(otherDate)) < 0;
		}

		public static boolean afterEquals(String date, String otherDate) {
			return compare.getIntervalTimes(StringToDate(date), StringToDate(otherDate)) >= 0;
		}

		public static boolean beforeEquals(String date, String otherDate) {
			return compare.getIntervalTimes(StringToDate(date), StringToDate(otherDate)) <= 0;
		}

		public static int getIntervalDays(Date date, Date otherDate) {
			return (int) Math.abs(compare.getIntervalTimes(date, otherDate) / (1000 * 60 * 60 * 24));
		}

		public static long getIntervalTimes(Date time, Date otherTime) {
			if (time == null || otherTime == null) {
				throw new NullPointerException();
			}
			return time.getTime() - otherTime.getTime();
		}

	}
	public static class opt {

		/**
		 * 增加日期的年份。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param year
		 *            增加数量。可为负数
		 * @return 增加年份后的日期字符串
		 */
		public static String addYear(String date, int year) {
			return addInteger(date, Calendar.YEAR, year);
		}

		/**
		 * 增加日期的年份。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addYear
		 *            增加数量。可为负数
		 * @return 增加年份后的日期
		 */
		public static Date addYear(Date date, int addYear) {
			return addInteger(date, Calendar.YEAR, addYear);
		}

		/**
		 * 增加日期的月份。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addMonth
		 *            增加数量。可为负数
		 * @return 增加月份后的日期字符串
		 */
		public static String addMonth(String date, int addMonth) {
			return addInteger(date, Calendar.MONTH, addMonth);
		}

		/**
		 * 增加日期的月份。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addMonth
		 *            增加数量。可为负数
		 * @return 增加月份后的日期
		 */
		public static Date addMonth(Date date, int addMonth) {
			return addInteger(date, Calendar.MONTH, addMonth);
		}

		/**
		 * 增加日期的天数。失败返回null。
		 * 
		 * @param date
		 *            日期字符串
		 * @param addDay
		 *            增加数量。可为负数
		 * @return 增加天数后的日期字符串
		 */
		public static String addDay(String date, int addDay) {
			return addInteger(date, Calendar.DATE, addDay);
		}

		/**
		 * 增加日期的天数。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addDay
		 *            增加数量。可为负数
		 * @return 增加天数后的日期
		 */
		public static Date addDay(Date date, int addDay) {
			return addInteger(date, Calendar.DATE, addDay);
		}

		/**
		 * 增加日期的小时。失败返回null。
		 * 
		 * @param date
		 *            日期字符串
		 * @param addHour
		 *            增加数量。可为负数
		 * @return 增加小时后的日期字符串
		 */
		public static String addHour(String date, int addHour) {
			return addInteger(date, Calendar.HOUR_OF_DAY, addHour);
		}

		/**
		 * 增加日期的小时。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addHour
		 *            增加数量。可为负数
		 * @return 增加小时后的日期
		 */
		public static Date addHour(Date date, int addHour) {
			return addInteger(date, Calendar.HOUR_OF_DAY, addHour);
		}

		/**
		 * 增加日期的分钟。失败返回null。
		 * 
		 * @param date
		 *            日期字符串
		 * @param addMinute
		 *            增加数量。可为负数
		 * @return 增加分钟后的日期字符串
		 */
		public static String addMinute(String date, int addMinute) {
			return addInteger(date, Calendar.MINUTE, addMinute);
		}

		/**
		 * 增加日期的分钟。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addMinute
		 *            增加数量。可为负数
		 * @return 增加分钟后的日期
		 */
		public static Date addMinute(Date date, int addMinute) {
			return addInteger(date, Calendar.MINUTE, addMinute);
		}

		/**
		 * 增加日期的秒钟。失败返回null。
		 * 
		 * @param date
		 *            日期字符串
		 * @param addSecond
		 *            增加数量。可为负数
		 * @return 增加秒钟后的日期字符串
		 */
		public static String addSecond(String date, int addSecond) {
			return addInteger(date, Calendar.SECOND, addSecond);
		}

		/**
		 * 增加日期的秒钟。失败返回null。
		 * 
		 * @param date
		 *            日期
		 * @param addSecond
		 *            增加数量。可为负数
		 * @return 增加秒钟后的日期
		 */
		public static Date addSecond(Date date, int addSecond) {
			return addInteger(date, Calendar.SECOND, addSecond);
		}

		public static Date addDate(Date date, int addYear, int addMonth, int addDay) {
			return opt.operatTime(date, addYear, addMonth, addDay, 0, 0, 0);
		}

		public static Date addTime(Date date, int addHour, int addMinute, int addSecond) {
			return opt.operatTime(date, 0, 0, 0, addHour, addMinute, addSecond);
		}

		public static Date operatTime(Date date, int addYear, int addMonth, int addDay, int addHour, int addMinute, int addSecond) {
			Date addYearTemp = opt.addYear(date, addYear);
			Date addMonthTemp = opt.addMonth(addYearTemp, addMonth);
			Date addDayTemp = opt.addDay(addMonthTemp, addDay);
			Date addHourTemp = opt.addHour(addDayTemp, addHour);
			Date addMinuteTemp = opt.addMinute(addHourTemp, addMinute);
			Date addSecondTemp = opt.addSecond(addMinuteTemp, addSecond);
			return addSecondTemp;
		}

	}
}