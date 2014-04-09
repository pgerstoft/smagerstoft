package com.gerstoft.sma;

import java.util.PriorityQueue;

public class StockDataGetter {

	private final String symbol;
	private final int lookback;

	private PriorityQueue<DailyStockData> stockData;

	public StockDataGetter(final String symbol, final int lookback) {
		this.symbol = symbol;
		this.lookback = lookback;
	}

	public String getSymbol() {
		return symbol;
	}

	public int getLookBack() {
		return lookback;
	}

	/**
	 * Download closing prices
	 */
	public void downloadData() {
		downloadData(new BusinessDay());
	}

	public void downloadData(BusinessDay endDate) {
		BusinessDay end;
		if (endDate.isBusinessDay()) {
			end = endDate;
		} else {
			end = endDate.getPreviousBusinessDay();
		}

		BusinessDay start = end.minusDays(lookback);

		if (!start.isBusinessDay()) {
			start = start.getPreviousBusinessDay();
		}

		setStockData(YahooFinanceReader.getYahooFinanceData(symbol, start, end));
	}

	public PriorityQueue<DailyStockData> getStockData() {
		return stockData;
	}

	public void setStockData(final PriorityQueue<DailyStockData> stockData) {
		this.stockData = stockData;
	}

}