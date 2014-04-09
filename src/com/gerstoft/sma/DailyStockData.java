package com.gerstoft.sma;

import java.io.Serializable;

import com.google.gson.Gson;

public class DailyStockData implements Comparable<DailyStockData>, Serializable {

	private static final long serialVersionUID = 1L;

	private final double high;
	private final double low;
	private final double close;
	private final double closeAdj;
	private final BusinessDay date;

	public DailyStockData(final BusinessDay date, final double high,
			final double low, final double close, final double closeAdj) {
		this.date = date;
		this.high = high;
		this.low = low;
		this.close = close;
		this.closeAdj = closeAdj;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public double getHighAdj() {
		return high * (1 - (close - closeAdj) / (close));
	}

	public double getLowAdj() {
		return low * (1 - (close - closeAdj) / (close));
	}

	public double getCloseAdj() {
		return closeAdj;
	}

	public BusinessDay getDate() {
		return date;
	}

	/**
	 * Orders by date. Most Recent is first
	 */
	public int compareTo(final DailyStockData stockData) {
		return -1 * this.date.compareTo(stockData.date);
	}

	@Override
	public String toString() {
		return getDate().toString();
	}

	public String toJson() {
		return new Gson().toJson(this);
	}
}
