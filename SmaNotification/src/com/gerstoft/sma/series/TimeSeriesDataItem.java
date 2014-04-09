package com.gerstoft.sma.series;

import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.Days;

public class TimeSeriesDataItem implements Comparable<TimeSeriesDataItem> {

	private static final DateMidnight START_OF_UNIX_EPOCH = new DateMidnight(
			1970, 1, 1);

	private final Date time;
	private final Double value;

	public TimeSeriesDataItem(Date time, Double value) {
		this.time = time;
		this.value = value;
	}

	public int getSerialIndex() {
		return calcSerial();
	}

	/** The number of days in a year up to the end of the preceding month. */
	static final int[] AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = { 0, 0, 31,
			59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };

	private int calcSerial() {
		return Days.daysBetween(START_OF_UNIX_EPOCH,
				new DateMidnight(time.getTime())).getDays();
	}

	public static int leapYearCount(final int yyyy) {

		final int leap4 = (yyyy - 1896) / 4;
		final int leap100 = (yyyy - 1800) / 100;
		final int leap400 = (yyyy - 1600) / 400;
		return leap4 - leap100 + leap400;

	}

	public static boolean isLeapYear(final int yyyy) {
		if ((yyyy % 4) != 0) {
			return false;
		} else if ((yyyy % 400) == 0) {
			return true;
		} else if ((yyyy % 100) == 0) {
			return false;
		} else {
			return true;
		}

	}

	public Date getDate() {
		return time;
	}

	public Number getValue() {
		return value;
	}

	@Override
	public int compareTo(TimeSeriesDataItem arg0) {
		return new Integer(getSerialIndex()).compareTo(arg0.getSerialIndex());
	}

}
