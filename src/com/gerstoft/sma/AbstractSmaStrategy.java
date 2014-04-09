package com.gerstoft.sma;

import java.util.PriorityQueue;

public abstract class AbstractSmaStrategy {

	private final String symbol;
	private PriorityQueue<DailyStockData> data;
	private final int smaValue;

	public AbstractSmaStrategy(String symbol, int smaValue) {
		this.symbol = symbol;
		this.smaValue = smaValue;
	}

	public void setStockData(PriorityQueue<DailyStockData> data) {
		this.data = data;
		computeSMAs();
	}

	protected abstract void computeSMAs();

	public String getSymbol() {
		return symbol;
	}

	public int getSMAValue() {
		return smaValue;
	}

	public PriorityQueue<DailyStockData> getStockData() {
		return data;
	}

	public void downloadData() {
		StockDataGetter getter = new StockDataGetter(symbol, smaValue * 2);
		getter.downloadData();

		PriorityQueue<DailyStockData> data = getter.getStockData();
		if (data.isEmpty()) {
			throw new IllegalStateException(symbol + " has no data");
		}

		setStockData(data);
	}
}
