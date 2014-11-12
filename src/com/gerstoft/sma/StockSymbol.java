package com.gerstoft.sma;

import java.io.Serializable;

public class StockSymbol implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String symbol;

	public StockSymbol(String symbol) {

		if (symbol == null || symbol.isEmpty() || symbol.length() > 5) {
			throw new IllegalArgumentException(symbol
					+ " is not a valid stock symbol");
		}

		this.symbol = symbol.toUpperCase();
	}

	public boolean isMutualFund() {
		// 4 letters with x at the end.
		return symbol.matches("\\w{4}X");
	}

	@Override
	public String toString() {
		return symbol;
	}
}
