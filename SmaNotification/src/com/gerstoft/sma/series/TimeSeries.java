package com.gerstoft.sma.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TimeSeries {
	private final String name;

	private final List<TimeSeriesDataItem> series;

	private boolean sorted = false;

	public TimeSeries(String name) {
		super();
		this.name = name;
		series = new ArrayList<TimeSeriesDataItem>();
	}

	public String getName() {
		return name;
	}

	public int getItemCount() {
		return series.size();
	}

	public void add(Date time, Double value) {
		series.add(new TimeSeriesDataItem(time, value));
		sorted = false;
	}

	public TimeSeriesDataItem getDataItem(int i) {
		if (!sorted) {
			Collections.sort(series);
			sorted = true;
		}
		return series.get(i);
	}

}
