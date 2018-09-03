package com.gerstoft.sma;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class YahooFinanceReaderV2 {
	public List<DailyStockData> getYahooFinanceData(final StockSymbol symbol,
			final BusinessDay start, final BusinessDay end) {

		List<DailyStockData> result = Lists.newArrayList();

		try {
			Stock stock = YahooFinance.get("GOOG", toCalendar(start),
					toCalendar(end), Interval.DAILY);

			for (HistoricalQuote h : stock.getHistory()) {
				result.add(new DailyStockData(symbol, new BusinessDay(
						new DateTime(h.getDate().toInstant().toEpochMilli())),
						h.getHigh().intValue(), h.getLow().intValue(), h
								.getClose().intValue(), h.getAdjClose()
								.intValue()));
			}

			return result;
		} catch (IOException e) {
			return Lists.newArrayList();
		}

	}

	private static Calendar toCalendar(BusinessDay day) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(day.getMillis());
		return cal;
	}
}
