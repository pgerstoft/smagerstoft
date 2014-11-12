package com.gerstoft.sma;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closeables;

/**
 * Reads the csv file from Yahoo Finance
 * 
 * @author pgerstoft
 * 
 */
public final class YahooFinanceReader {
	public List<DailyStockData> getYahooFinanceData(final StockSymbol symbol,
			final BusinessDay start, final BusinessDay end) {
		String url = buildUrl(symbol, start, end);
		return getData(symbol, url);
	}

	private String buildUrl(final StockSymbol symbol, final BusinessDay start,
			final BusinessDay end) {
		StringBuilder uri = new StringBuilder();
		uri.append("http://ichart.finance.yahoo.com/table.csv");
		uri.append("?s=").append(symbol);
		uri.append("&a=").append(start.getMonthOfYear() - 1);
		uri.append("&b=").append(start.getDayOfMonth());
		uri.append("&c=").append(start.getYear());
		uri.append("&d=").append(end.getMonthOfYear() - 1);
		uri.append("&e=").append(end.getDayOfMonth());
		uri.append("&f=").append(end.getYear());
		uri.append("&g=d");
		uri.append("&ignore=.csv");
		System.out.println(uri.toString());
		return uri.toString();
	}

	private List<DailyStockData> getData(final StockSymbol symbol,
			final String url) {

		List<DailyStockData> stockData;

		// Using the synchronous cache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		// read from cache
		stockData = fromByteArray((byte[]) syncCache.get(url));

		// if the data is not in the cache
		if (stockData == null) {
			stockData = downloadData(symbol, url);

			if (!stockData.isEmpty()) {
				// store results in cache
				syncCache.put(url, toByteArray(stockData));
			}
		}

		return stockData;
	}

	private List<DailyStockData> downloadData(final StockSymbol symbol,
			final String url) {
		List<DailyStockData> stockData = new ArrayList<>();
		BufferedReader urlReader = null;

		try {
			urlReader = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));
			String line = urlReader.readLine(); // ignore Header
			line = urlReader.readLine();
			while (line != null) {
				try {
					stockData.add(yahooLineToStockObject(symbol, line));
				} catch (IllegalArgumentException e) {
					// do nothing
				}

				line = urlReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(urlReader);
		}
		Collections.sort(stockData);

		return ImmutableList.copyOf(stockData);
	}

	private byte[] toByteArray(List<DailyStockData> stockData) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(stockData);
			byte[] yourBytes = bos.toByteArray();
			out.close();
			bos.close();
			return yourBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<DailyStockData> fromByteArray(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = new ObjectInputStream(bis);
			List<DailyStockData> o = (List<DailyStockData>) in.readObject();
			bis.close();
			in.close();
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * converts data from yahoo into a stock object
	 */
	public DailyStockData yahooLineToStockObject(final StockSymbol symbol,
			final String line) {
		String[] lineSplit = line.split(",");
		if (lineSplit.length < 5) {
			throw new IllegalArgumentException("Not enough values to parse");
		}
		BusinessDay date = new BusinessDay(new DateTime(lineSplit[0]));
		// double open = Double.valueOf(lineSplit[1]);
		double high = Double.valueOf(lineSplit[2]);
		double low = Double.valueOf(lineSplit[3]);
		double close = Double.valueOf(lineSplit[4]);
		// double volume = Double.valueOf(lineSplit[5]);
		double closeAdj = Double.valueOf(lineSplit[6]);

		return new DailyStockData(symbol, date, high, low, close, closeAdj);
	}

}
