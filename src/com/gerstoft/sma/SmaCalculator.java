package com.gerstoft.sma;

import java.util.List;

public class SmaCalculator {

	public static double getSMA(final List<DailyStockData> data,
			GetDataFunc func, int period) {
		if (period > data.size()) {
			return -1;
		}

		double sum = 0;
		for (DailyStockData item : data.subList(0, period)) {
			sum += func.getValue(item);
		}

		return sum / period;
	}
}
