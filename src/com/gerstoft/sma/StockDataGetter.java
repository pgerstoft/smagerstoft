package com.gerstoft.sma;

import java.util.List;

public class StockDataGetter {

	private final StockSymbol symbol;
	private final int lookback;
	// the last day we are downloading data for (today or earlier)
	private final BusinessDay lastDay;

	private List<DailyStockData> stockData;

	public StockDataGetter(final StockSymbol symbol, final int lookback) {
		this(symbol, lookback, new BusinessDay());
	}

	public StockDataGetter(final StockSymbol symbol, final int lookback,
			final BusinessDay lastDay) {
		this.symbol = symbol;
		this.lookback = lookback;
		this.lastDay = lastDay;
	}

	public StockSymbol getSymbol() {
		return symbol;
	}

	public int getLookBack() {
		return lookback;
	}

	public BusinessDay getLastDayOfSeries() {
		return lastDay;
	}

	public List<DailyStockData> getStockData() {
		if (stockData == null) {
			downloadData();
		}

		return stockData;
	}

	/**
	 * Download closing prices
	 */
	private void downloadData() {
		BusinessDay last = getValidBusinessDay(lastDay);
		BusinessDay first = getValidBusinessDay(last.minusDays(lookback));

		this.stockData = new YahooFinanceReaderV2().getYahooFinanceData(symbol,
				first, last);

		if (stockData.isEmpty()) {
			throw new IllegalStateException(symbol + " has no data");
		}
	}

	private BusinessDay getValidBusinessDay(BusinessDay day) {
		if (!day.isBusinessDay()) {
			return day.getPreviousBusinessDay();
		}

		return day;
	}
}