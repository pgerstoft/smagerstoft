package com.gerstoft.sma;

import static com.gerstoft.sma.util.Round.round;

import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;

import com.gerstoft.sma.series.MovingAverage;
import com.gerstoft.sma.series.TimeSeries;
import com.gerstoft.sma.series.TimeSeriesDataItem;
import com.google.common.base.Strings;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

public class SmaChartServlet extends DataSourceServlet {

	private static final long serialVersionUID = 1L;
	public static final int MOVING_AVERAGE = 200;
	public static final int DAYS_IN_YEAR = 365;
	public static final int BUSINESS_DAYS_IN_YEAR = 252;

	@Override
	public DataTable generateDataTable(Query arg0, HttpServletRequest arg1)
			throws DataSourceException {

		String symbol = arg1.getParameter("symbol");

		// Create a data table,
		DataTable data = new DataTable();

		if (Strings.isNullOrEmpty(symbol)) {
			return data;
		}

		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("date", ValueType.DATE, "Date"));

		cd.add(new ColumnDescription("close", ValueType.NUMBER, "Close"));
		cd.add(new ColumnDescription("highsma", ValueType.NUMBER, "High SMA"));
		cd.add(new ColumnDescription("lowsma", ValueType.NUMBER, "Low SMA"));
		cd.add(new ColumnDescription("closesma", ValueType.NUMBER, "Close SMA"));
		data.addColumns(cd);

		System.out.println(symbol);

		KestnerSmaStrategy stock = new KestnerSmaStrategy(symbol, "");
		stock.downloadData();

		TimeSeries close = new TimeSeries("Close");
		TimeSeries closeForSMA = new TimeSeries("Close");
		TimeSeries highForSMA = new TimeSeries("High");
		TimeSeries lowForSMA = new TimeSeries("Low");

		Queue<DailyStockData> dataQueue = stock.getStockData();
		int size = dataQueue.size();
		int count = 0;
		for (DailyStockData stockData : dataQueue) {
			java.util.Date time = new Date(stockData.getDate().getMillis());

			if (size - count >= MOVING_AVERAGE) {
                close.add(time, stockData.getCloseAdj());
				count++;
			}

			closeForSMA.add(time, stockData.getCloseAdj());
			lowForSMA.add(time, stockData.getLowAdj());
			highForSMA.add(time, stockData.getHighAdj());
		}

		int newMA = (int) ((double) MOVING_AVERAGE / BUSINESS_DAYS_IN_YEAR * DAYS_IN_YEAR);

		TimeSeries closeSma = MovingAverage.createMovingAverage(closeForSMA,
				"Close 200 Day MA", newMA, newMA);
		TimeSeries highSma = MovingAverage.createMovingAverage(highForSMA,
				"High 200 Day MA", newMA, newMA);
		TimeSeries lowSma = MovingAverage.createMovingAverage(lowForSMA,
				"Low 200 Day MA", newMA, newMA);

		// Fill the data table.
		try {

			for (int i = 0; i < close.getItemCount() - 1; i++) {
				TimeSeriesDataItem closeItem = close.getDataItem(i);
				TimeSeriesDataItem closeSmaItem = closeSma.getDataItem(i);
				TimeSeriesDataItem highSmaItem = highSma.getDataItem(i);
				TimeSeriesDataItem lowSmaItem = lowSma.getDataItem(i);

				Date date = closeSmaItem.getDate();

				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
				Value v = ValueType.DATE.createValue(calendar);

				TableRow tr = new TableRow();
				tr.addCell(v);

				tr.addCell(round(closeItem.getValue()));
				tr.addCell(round(highSmaItem.getValue()));
				tr.addCell(round(lowSmaItem.getValue()));
				tr.addCell(round(closeSmaItem.getValue()));

				data.addRow(tr);
			}

		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}
		return data;
	}
}
