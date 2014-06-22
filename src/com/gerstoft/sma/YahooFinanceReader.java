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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Reads the csv file from Yahoo Finance
 * 
 * @author pgerstoft
 * 
 */
public final class YahooFinanceReader {
	// Timeout to avoid requests being rejected
	private static final int WAIT = 1000;

	private static YahooFinanceReader instance;

	private volatile long lastYahooRequest = 0;

	private YahooFinanceReader() {
		// private constructor
	}

	private static synchronized YahooFinanceReader getYahooFinanceReader() {
		if (instance == null) {
			instance = new YahooFinanceReader();
		}

		return instance;
	}

	public static PriorityQueue<DailyStockData> getYahooFinanceData(
			final String symbol, final BusinessDay start, final BusinessDay end) {
		String url = buildUrl(symbol, start, end);
		return getYahooFinanceReader().getData(url);
	}

	private static String buildUrl(final String symbol,
			final BusinessDay start, final BusinessDay end) {
		StringBuilder uri = new StringBuilder();
		uri.append("http://ichart.finance.yahoo.com/table.csv");
		uri.append("?s=").append(symbol.toUpperCase(Locale.US));
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

	private PriorityQueue<DailyStockData> getData(final String url) {

		PriorityQueue<DailyStockData> stockData;

		// Using the synchronous cache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		stockData = fromByteArray((byte[]) syncCache.get(url)); // read from
																// cache

		if (stockData == null) {
			stockData = new PriorityQueue<DailyStockData>();
			BufferedReader urlReader = null;

			try {
				long timeSinceLastRequest = System.currentTimeMillis()
						- lastYahooRequest;
				if (timeSinceLastRequest < WAIT) {
					try {
						Thread.sleep(WAIT - timeSinceLastRequest);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lastYahooRequest = System.currentTimeMillis();
				urlReader = new BufferedReader(new InputStreamReader(new URL(
						url).openStream()));
				String line = urlReader.readLine(); // ignore Header
				line = urlReader.readLine();
				while (line != null) {
					try {
						stockData.add(yahooLineToStockObject(line));
					} catch (IllegalArgumentException e) {
						// do nothing
					}

					line = urlReader.readLine();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (urlReader != null) {
					try {
						urlReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (!stockData.isEmpty()) {
				syncCache.put(url, toByteArray(stockData)); // populate cache
				System.out.print("to cache");
			}
		} else {
			System.out.print("from cache");
		}

		return stockData;
	}

	private static byte[] toByteArray(PriorityQueue<DailyStockData> stockData) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out;
		try {
			out = new ObjectOutputStream(bos);
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
	private static PriorityQueue<DailyStockData> fromByteArray(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = new ObjectInputStream(bis);
			PriorityQueue<DailyStockData> o = (PriorityQueue<DailyStockData>) in
					.readObject();
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
	public static DailyStockData yahooLineToStockObject(final String line) {
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

		return new DailyStockData(date, high, low, close, closeAdj);
	}

}
