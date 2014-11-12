package com.gerstoft.sma;

import java.text.DecimalFormat;
import java.util.List;

public class KestnerSmaStrategy {

	public static final int KESTNER_SMOOTHING_FACTOR = 200;

	private static final DecimalFormat TWO_DECIMAL = new DecimalFormat("#.##");

	private double highSMA;
	private double lowSMA;
	private double closeSMA;

	private final StockSymbol symbol;
	private List<DailyStockData> data;

	public KestnerSmaStrategy(StockSymbol symbol) {
		this.symbol = symbol;
	}

	public void downloadData() {
		this.data = new StockDataGetter(symbol, KESTNER_SMOOTHING_FACTOR * 2)
				.getStockData();
		computeSMAs();
	}

	public StockSymbol getSymbol() {
		return symbol;
	}

	public int getSMAValue() {
		return KESTNER_SMOOTHING_FACTOR;
	}

	public List<DailyStockData> getStockData() {
		return data;
	}

	protected void computeSMAs() {

		highSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
			public double getValue(DailyStockData data) {
				return data.getHighAdj();
			}
		}, KESTNER_SMOOTHING_FACTOR);

		lowSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
			public double getValue(DailyStockData data) {
				return data.getLowAdj();
			}
		}, KESTNER_SMOOTHING_FACTOR);

		closeSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
			public double getValue(DailyStockData data) {
				return data.getCloseAdj();
			}
		}, KESTNER_SMOOTHING_FACTOR);

	}

	public boolean isBuy() {
		return getMostRecentClose() > highSMA;
	}

	public boolean isSell() {
		return getMostRecentClose() < lowSMA;
	}

	public double getCloseSMA() {
		return closeSMA;
	}

	public double getHighSMA() {
		return highSMA;
	}

	public double getLowSMA() {
		return lowSMA;
	}

	public double getMostRecentClose() {
		return getStockData().get(0).getClose();
	}

	public double getSecondMostRecentClose() {
		return getStockData().get(1).getClose();
	}

	@Override
	public String toString() {
		if (getStockData() == null) {
			return KestnerSmaStrategy.class.getSimpleName() + " " + getSymbol();
		}

		return getAction() + getValuesString();
	}

	public String getAction() {
		if (isBuy()) {
			return "Buy";
		} else if (isSell()) {
			return "Sell";
		} else {
			return "Hold";
		}
	}

	public String getValuesString() {
		return getSymbol() + ", Close=" + getMostRecentClose() + ", High SMA="
				+ TWO_DECIMAL.format(highSMA) + ", Low SMA="
				+ TWO_DECIMAL.format(lowSMA) + ", Close SMA="
				+ TWO_DECIMAL.format(closeSMA);
	}

}
