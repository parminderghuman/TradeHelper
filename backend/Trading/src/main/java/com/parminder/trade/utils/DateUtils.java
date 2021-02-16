package com.parminder.trade.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {
	static SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat completeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	static SimpleDateFormat minuteDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Date parseDayFormate(String date) throws ParseException {

		return dayDateFormat.parse(date);

	}

	public static String parseDayFormate(Date date) {

		return dayDateFormat.format(date);

	}

	public static Date parseminuteDateFormat(String date) throws ParseException {

		return minuteDateFormat.parse(date);

	}

	public static String parseminuteDateFormat(Date date) {

		return minuteDateFormat.format(date);

	}

	public static Date parseCompletFormate(String timestamp) throws Exception {
		try {
			SimpleDateFormat completeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

			return completeDateFormat.parse(timestamp.substring(0, timestamp.length() - 5).trim());

		} catch (ParseException e) {
			System.out.println("DateUtils : parseCompletFormate : " + timestamp + " = "
					+ timestamp.substring(0, timestamp.length() - 5).trim());
			throw new Exception(e);
		}
	}

	public static boolean isDateAreOnSameDay(Date date1, Date date2) {
		Date dat1 = new Date(date1.getTime());
		Date dat2 = new Date(date2.getTime());
		dat1 = getDayStartDate(dat1);
		dat2 = getDayStartDate(dat2);
		if (dat1.getTime() == dat2.getTime()) {
			return true;
		}
		return false;
	}

	public static Date getDayStartDate(Date date) {
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		date.setTime(((long) date.getTime() / 1000) * 1000);
		return date;

	}

	public static Date getPastFirstDate() {
		Date startDate = new Date();
		startDate.setDate(0);
		startDate.setMonth(0);
		startDate.setYear(2000 - 1900);
		return startDate;
	}

}
