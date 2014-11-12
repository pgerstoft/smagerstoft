package com.gerstoft.sma;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * A wrapper around the joda.time.DateTime class
 * 
 * @author pgerstoft
 * 
 */
public class BusinessDay implements Comparable<BusinessDay>, Serializable {

	public static final int NUMBER_IN_YEAR = 252;

	private static final long serialVersionUID = 1L;

	private static transient Map<Integer, List<DateTime>> computedDates = new HashMap<Integer, List<DateTime>>();

	private final DateTime date;

	public BusinessDay() {
		date = new DateTime();
	}

	public BusinessDay(final String date) {
		this.date = new DateTime(date);
	}

	public BusinessDay(final DateTime date) {
		this.date = date;
	}

	public int getMonthOfYear() {
		return date.getMonthOfYear();
	}

	public int getDayOfMonth() {
		return date.getDayOfMonth();
	}

	public int getYear() {
		return date.getYear();
	}

	public long getMillis() {
		return date.getMillis();
	}

	public BusinessDay minusDays(final int days) {
		DateTime result = date;
		for (int i = 0; i < days; i++) {
			result = result.minusDays(1);
			while (!isBusinessDay(result)) {
				result = result.minusDays(1);
			}
		}
		return new BusinessDay(result);
	}

	public boolean isBusinessDay() {
		return isBusinessDay(date);
	}

	/*
	 * This method will calculate the next business day after the one input.
	 * This means that if the next day falls on a weekend or one of the
	 * following holidays then it will try the next day.
	 * 
	 * Holidays Accounted For: New Year's Day Martin Luther King Jr. Day
	 * President's Day Memorial Day Independence Day Labor Day Columbus Day
	 * Veterans Day Thanksgiving Day Christmas Day
	 */
	private boolean isBusinessDay(final DateTime date) {

		List<DateTime> offlimitDates;

		// Grab the list of dates for the year. These SHOULD NOT be modified.
		synchronized (computedDates) {
			int year = date.getYear();

			// If the map doesn't already have the dates computed, create them.
			if (!computedDates.containsKey(year)) {
				computedDates.put(year, getOfflimitDates(year));
			}
			offlimitDates = computedDates.get(year);
		}

		// Determine if the date is on a weekend.
		boolean onWeekend = isWeekend(date);

		// If it's on a holiday, increment and test again
		// If it's on a weekend, increment necessary amount and test again
		return !offlimitDates.contains(date) && !onWeekend;
	}

	private boolean isWeekend(final DateTime date) {
		int dayOfWeek = date.getDayOfWeek();
		return (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY);
	}

	public BusinessDay getNextBusinessDay() {
		return new BusinessDay(getNextBusinessDay(date));
	}

	/**
	 * 
	 * This method will calculate the next business day after the one input.
	 * This leverages the isBusinessDay heavily, so look at that documentation
	 * for further information.
	 * 
	 * @param startDate
	 *            the Date of which you need the next business day.
	 * @return The next business day. I.E. it doesn't fall on a weekend, a
	 *         holiday or the official observance of that holiday if it fell on
	 *         a weekend.
	 * 
	 */
	private DateTime getNextBusinessDay(final DateTime startDate) {
		// Increment the Date object by a Day and clear out hour/min/sec
		// information
		DateTime nextDay = startDate.plusDays(1);
		// If tomorrow is a valid business day, return it
		if (isBusinessDay(nextDay)) {
			return nextDay;
		}

		// Else we recursively call our function until we find one.
		return getNextBusinessDay(nextDay);
	}

	public BusinessDay getPreviousBusinessDay() {
		return new BusinessDay(getPreviousBusinessDay(date));
	}

	/**
	 * 
	 * This method will calculate the previous business day after the one input.
	 * This leverages the isBusinessDay heavily, so look at that documentation
	 * for further information.
	 * 
	 * @param startDate
	 *            the Date of which you need the next business day.
	 * @return The next business day. I.E. it doesn't fall on a weekend, a
	 *         holiday or the official observance of that holiday if it fell on
	 *         a weekend.
	 * 
	 */
	private DateTime getPreviousBusinessDay(final DateTime startDate) {
		// Increment the Date object by a Day and clear out hour/min/sec
		// information
		DateTime nextDay = startDate.minusDays(1);
		// If tomorrow is a valid business day, return it
		if (isBusinessDay(nextDay)) {
			return nextDay;
			// Else we recursively call our function until we find one.
		} else {
			return getNextBusinessDay(nextDay);
		}
	}

	/*
	 * Based on a year, this will compute the actual dates of
	 * 
	 * Holidays Accounted For: New Year's Day Martin Luther King Jr. Day
	 * President's Day Memorial Day Independence Day Labor Day Columbus Day
	 * Veterans Day Thanksgiving Day Christmas Day
	 */
	private static List<DateTime> getOfflimitDates(final int year) {
		List<DateTime> offlimitDates = new ArrayList<DateTime>();

		// Add in the static dates for the year.
		// New years day
		DateTime newYearsDay = new DateTime(year, DateTimeConstants.JANUARY, 1,
				0, 0);
		offlimitDates.add(offsetForWeekend(newYearsDay));

		// Independence Day
		DateTime independenceDay = new DateTime(year, DateTimeConstants.JULY,
				4, 0, 0);
		offlimitDates.add(offsetForWeekend(independenceDay));

		// Christmas
		DateTime christmasDay = new DateTime(year, DateTimeConstants.DECEMBER,
				25, 0, 0);
		offlimitDates.add(offsetForWeekend(christmasDay));

		// Now deal with floating holidays.
		// Martin Luther King Day
		offlimitDates.add(calculateFloatingHoliday(3, DateTimeConstants.MONDAY,
				year, DateTimeConstants.JANUARY));

		// Presidents Day
		offlimitDates.add(calculateFloatingHoliday(3, DateTimeConstants.MONDAY,
				year, DateTimeConstants.FEBRUARY));

		// Memorial Day
		offlimitDates.add(calculateFloatingHoliday(0, DateTimeConstants.MONDAY,
				year, DateTimeConstants.MAY));

		// Labor Day
		offlimitDates.add(calculateFloatingHoliday(1, DateTimeConstants.MONDAY,
				year, DateTimeConstants.SEPTEMBER));

		// Thanksgiving Day
		DateTime thanksgiving = calculateFloatingHoliday(4,
				DateTimeConstants.THURSDAY, year, DateTimeConstants.NOVEMBER);
		offlimitDates.add(thanksgiving);

		// TODO: good friday

		return offlimitDates;
	}

	/**
	 * This method will take in the various parameters and return a Date objet
	 * that represents that value.
	 * 
	 * Ex. To get Martin Luther Kings BDay, which is the 3rd Monday of January,
	 * the method call woudl be:
	 * 
	 * calculateFloatingHoliday(3, Calendar.MONDAY, year, Calendar.JANUARY);
	 * 
	 * Reference material can be found at:
	 * http://michaelthompson.org/technikos/holidays.php#MemorialDay
	 * 
	 * @param nth
	 *            0 for Last, 1 for 1st, 2 for 2nd, etc.
	 * @param dayOfWeek
	 *            Use Calendar.MODAY, Calendar.TUESDAY, etc.
	 * @param year
	 * @param month
	 *            Use Calendar.JANUARY, etc.
	 * @return
	 */
	private static DateTime calculateFloatingHoliday(final int nth,
			final int dayOfWeek, final int year, final int month) {

		// Determine what the very earliest day this could occur.
		// If the value was 0 for the nth parameter, increment to the following
		// month so that it can be subtracted alter.
		DateTime baseDate = new DateTime(year, month + ((nth <= 0) ? 1 : 0), 1,
				0, 0);

		// Figure out which day of the week that this "earliest" could occur on
		// and then determine what the offset is for our day that we actually
		// need.
		int baseDayOfWeek = baseDate.getDayOfWeek();
		int fwd = dayOfWeek - baseDayOfWeek;

		// Based on the offset and the nth parameter, we are able to determine
		// the offset of days and then
		// adjust our base date.
		return addDays(baseDate, (fwd + (nth - (fwd >= 0 ? 1 : 0)) * 7));
	}

	/*
	 * If the given date falls on a weekend, the method will adjust to the
	 * closest weekday. I.E. If the date is on a Saturday, then the Friday will
	 * be returned, if it's a Sunday, then Monday is returned.
	 */
	private static DateTime offsetForWeekend(final DateTime date) {
		if (date.getDayOfWeek() == DateTimeConstants.SATURDAY) {
			return date.minusDays(1);
		} else if (date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
			return date.plusDays(1);
		} else {
			return date;
		}
	}

	/**
	 * Private method simply adds
	 * 
	 * @param dateToAdd
	 * @param numberOfDay
	 * @return
	 */
	public static DateTime addDays(final DateTime dateToAdd,
			final int numberOfDays) {
		if (dateToAdd == null) {
			throw new IllegalArgumentException("Date can't be null!");
		}

		return dateToAdd.plusDays(numberOfDays);
	}

	@Override
	public int hashCode() {
		int prime = 31;
		return prime + ((date == null) ? 0 : date.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BusinessDay other = (BusinessDay) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (date.getDayOfMonth() != other.date.getDayOfMonth()
				|| date.getYear() != other.date.getYear()
				|| date.getMonthOfYear() != other.date.getMonthOfYear()) {
			return false;
		}
		return true;
	}

	public int compareTo(final BusinessDay arg0) {
		return date.compareTo(arg0.date);
	}

	@Override
	public String toString() {
		return date.toString();
	}
}