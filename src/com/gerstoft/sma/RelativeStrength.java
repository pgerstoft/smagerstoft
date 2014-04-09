package com.gerstoft.sma;

import java.text.DecimalFormat;
import java.util.PriorityQueue;

public class RelativeStrength implements Comparable<RelativeStrength> {

	private static final DecimalFormat TWO_DECIMAL = new DecimalFormat("#.##");

	private static final String[] COLUMNS = new String[] { "3 Month Return",
			"6 Month Return", "12 Month Return", "3,6,12 Average" };

	public final int THREE_MONTH_DAYS = 63;
	public static final int SIX_MONTH_DAYS = 126;
	public static final int TWELVE_MONTH_DAYS = 252;

	private final double threeMonthReturn;
	private final double sixMonthReturn;
	private final double twelveMonthReturn;

	private final String symbol;

	public RelativeStrength(String symbol, PriorityQueue<DailyStockData> data) {

		this.symbol = symbol;

		int day = 0;
		if (data.size() < TWELVE_MONTH_DAYS) {
			threeMonthReturn = -101;
			sixMonthReturn = -101;
			twelveMonthReturn = -101;
		} else {
			double mostRecentClose = data.peek().getCloseAdj();
			double threeMonthOldClose = -101;
			double sixMonthOldClose = -101;
			double twelveMonthOldClose = -101;

			for (DailyStockData stockData : data) {
				if (day == THREE_MONTH_DAYS) {
					threeMonthOldClose = stockData.getCloseAdj();
				} else if (day == SIX_MONTH_DAYS) {
					sixMonthOldClose = stockData.getCloseAdj();
				} else if (day == TWELVE_MONTH_DAYS) {
					twelveMonthOldClose = stockData.getCloseAdj();
					break;
				}
				day++;
			}

			threeMonthReturn = (mostRecentClose - threeMonthOldClose)
					/ threeMonthOldClose * 100;
			sixMonthReturn = (mostRecentClose - sixMonthOldClose)
					/ sixMonthOldClose * 100;
			twelveMonthReturn = (mostRecentClose - twelveMonthOldClose)
					/ twelveMonthOldClose * 100;
		}
	}

	public String getSymbol() {
		return symbol;
	}

	public double get3MonthReturn() {
		return threeMonthReturn;
	}

	public double get6MonthReturn() {
		return sixMonthReturn;
	}

	public double get12MonthReturn() {
		return twelveMonthReturn;
	}

	public double getAverageThreeSixTwelveReturns() {
		return (threeMonthReturn + sixMonthReturn + twelveMonthReturn) / 3;
	}

	@Override
	public String toString() {
		return "3 Month = " + TWO_DECIMAL.format(get3MonthReturn())
				+ " 6 Month = " + TWO_DECIMAL.format(get6MonthReturn())
				+ " 12 Month = " + TWO_DECIMAL.format(get12MonthReturn());
	}

	public static String[] getColumnHeaders() {
		return COLUMNS;
	}

	public String getHMTLEntries() {
		return "<td>" + TWO_DECIMAL.format(get3MonthReturn()) + "</td>"
				+ "<td>" + TWO_DECIMAL.format(get6MonthReturn()) + "</td>"
				+ "<td>" + TWO_DECIMAL.format(get12MonthReturn()) + "</td>"
				+ "<td>"
				+ TWO_DECIMAL.format(getAverageThreeSixTwelveReturns())
				+ "</td>";
	}

	/**
	 * Order from highest return to lowest
	 */
	public int compareTo(RelativeStrength o) {
		return -1
				* Double.compare(getAverageThreeSixTwelveReturns(),
						o.getAverageThreeSixTwelveReturns());
	}
}
