package com.gerstoft.sma;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelativeStrength implements Comparable<RelativeStrength> {

	private static final DecimalFormat TWO_DECIMAL = new DecimalFormat("#.##");

	public final int AVG_BUSINESS_DAYS_IN_MONTH = BusinessDay.NUMBER_IN_YEAR / 12;

	private final Map<Integer, Double> CACHED_MONTHLY_RETURNS = new HashMap<>();

	private final StockSymbol symbol;
	private final List<DailyStockData> data;

	public RelativeStrength(StockSymbol stockSymbol, List<DailyStockData> data) {

		this.symbol = stockSymbol;
		this.data = data;
	}

	public StockSymbol getSymbol() {
		return symbol;
	}

	public double getReturnNMonthsBack(int monthLookback) {
		if (CACHED_MONTHLY_RETURNS.containsKey(monthLookback)) {
			return CACHED_MONTHLY_RETURNS.get(monthLookback);
		}

		int daysBack = monthLookback * AVG_BUSINESS_DAYS_IN_MONTH;
		double lookBackReturn;

		if (data.size() >= daysBack) {
			double oldClose = data.get(daysBack).getCloseAdj();
			double mostRecentClose = data.get(0).getCloseAdj();

			lookBackReturn = (mostRecentClose - oldClose) / oldClose * 100;
		} else {
			lookBackReturn = -101;
		}

		CACHED_MONTHLY_RETURNS.put(monthLookback, lookBackReturn);

		return lookBackReturn;
	}

	public double getAverageThreeSixTwelveReturns() {
		double sum = 0;
		List<Integer> months = Arrays.asList(3, 6, 12);
		for (int lookback : months) {
			sum += getReturnNMonthsBack(lookback);
		}
		return sum / months.size();
	}

	@Override
	public String toString() {
		return "3 Month = " + TWO_DECIMAL.format(getReturnNMonthsBack(3))
				+ " 6 Month = " + TWO_DECIMAL.format(getReturnNMonthsBack(6))
				+ " 12 Month = " + TWO_DECIMAL.format(getReturnNMonthsBack(12));
	}

	/**
	 * Order from highest return to lowest
	 */
	@Override
	public int compareTo(RelativeStrength o) {
		return -1
				* Double.compare(getAverageThreeSixTwelveReturns(),
						o.getAverageThreeSixTwelveReturns());
	}
}
