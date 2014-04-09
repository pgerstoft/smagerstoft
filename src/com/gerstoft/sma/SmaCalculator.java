package com.gerstoft.sma;

import java.util.PriorityQueue;
import java.util.Queue;

public class SmaCalculator {

	public static double getSMA(final Queue<DailyStockData> data,
			GetDataFunc func, int period) {
		Queue<DailyStockData> temp = new PriorityQueue<DailyStockData>(data);
		double sma;

		int count = 0;
		double sum = 0;

		while (count < period) {
			if (temp.isEmpty()) {
				return -1;
			}
			DailyStockData stock = temp.poll();
			sum += func.getValue(stock);
			count++;
		}

		sma = sum / period;
		return sma;
	}
}
