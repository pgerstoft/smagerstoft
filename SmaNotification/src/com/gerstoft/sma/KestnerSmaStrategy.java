package com.gerstoft.sma;

import java.text.DecimalFormat;

public class KestnerSmaStrategy extends AbstractSmaStrategy {

	private static final int KESTNER_SMOOTHING_FACTOR = 200;

	private static final DecimalFormat TWO_DECIMAL = new DecimalFormat("#.##");

	private static final String[] COLUMNS = new String[] { "Symbol",
			"Description", "Action", "Close", "High SMA", "Low SMA",
			"Close SMA" };

	private double highSMA;
	private double lowSMA;
	private double closeSMA;

	private final String description;

	public KestnerSmaStrategy(String symbol, Object object) {
		super(symbol, KESTNER_SMOOTHING_FACTOR);
		if (object == null) {
			this.description = "";
		} else {
			this.description = object.toString();
		}
	}

	public String getDescription() {
		return description;
	}

	@Override
	protected void computeSMAs() {

		if (isMutualFund()) {
			highSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
				public double getValue(DailyStockData data) {
					return data.getCloseAdj() * 1.01;
				}
			}, KESTNER_SMOOTHING_FACTOR);

			lowSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
				public double getValue(DailyStockData data) {
					return data.getCloseAdj() * .99;
				}
			}, KESTNER_SMOOTHING_FACTOR);
		} else {
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
		}
		closeSMA = SmaCalculator.getSMA(getStockData(), new GetDataFunc() {
			public double getValue(DailyStockData data) {
				return data.getCloseAdj();
			}
		}, KESTNER_SMOOTHING_FACTOR);

	}

	public boolean isMutualFund() {
		return getSymbol().matches("\\w{4}X");
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
		return getStockData().peek().getClose();
	}

	public double getSecondMostRecentClose() {
		DailyStockData mostRecentClose = getStockData().poll();
		double close = getStockData().peek().getClose();
		getStockData().add(mostRecentClose);
		return close;
	}

	@Override
	public String toString() {
		if (getStockData() == null) {
			return KestnerSmaStrategy.class.getSimpleName() + " " + getSymbol();
		}

		return getAction() + getValuesString();
	}

	public String getAction() {
		String action;
		if (isBuy()) {
			action = "Buy";
		} else if (isSell()) {
			action = "Sell";
		} else {
			action = "Hold";
		}

		return action;
	}

	public String getValuesString() {
		return getSymbol() + ", Close=" + getMostRecentClose() + ", High SMA="
				+ TWO_DECIMAL.format(highSMA) + ", Low SMA="
				+ TWO_DECIMAL.format(lowSMA) + ", Close SMA="
				+ TWO_DECIMAL.format(closeSMA);
	}

	public static String[] getColumnHeaders() {
		return COLUMNS;
	}

	public String getHMTLEntries() {
		return "<td>" + getSymbol() + "</td>" + "<td>" + getDescription()
				+ "</td>" + "<td>" + getAction() + "</td>" + "<td>"
				+ TWO_DECIMAL.format(getMostRecentClose()) + "</td>" + "<td>"
				+ TWO_DECIMAL.format(getHighSMA()) + "</td>" + "<td>"
				+ TWO_DECIMAL.format(getLowSMA()) + "</td>" + "<td>"
				+ TWO_DECIMAL.format(getCloseSMA()) + "</td>";
	}
}
