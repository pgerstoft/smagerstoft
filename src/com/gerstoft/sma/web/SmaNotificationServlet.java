package com.gerstoft.sma.web;

import static com.gerstoft.sma.util.Round.round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.gerstoft.sma.KestnerSmaStrategy;
import com.gerstoft.sma.RelativeStrength;
import com.gerstoft.sma.StockSymbol;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

@SuppressWarnings("serial")
public class SmaNotificationServlet extends DataSourceServlet {

	private static final String[] ANDY_SYMBOLS = "DXHLX,DXELX,DXQLX,DXSLX,DXRLX,BMPIX,IDPIX,INPIX,OEPIX,SMPIX,SLPIX,SGPIX,TEPIX,ULPIX,UGPIX,UUPIX,UBPIX,UMPIX,UOPIX,HCPIX,WCPIX,ENPIX,UAPIX,RYBIX,RYSIX,RYWVX,RYEIX,RYIIX,RYJHX,RYMDX,RYVYX,RYMKX,RYRHX,RYRSX,RYTNX,RYTIX,SFENX,SWSSX,SWSCX"
			.split(",");

	private static final String[] ALEX_SYMBOLS = "BND,DBC,TIP,GSG,RWX,VB,VEU,VNQ,VTI,VWO"
			.split(",");

	// SSO - SP 2x
	// QLD - QQQ 2x
	// UWM - Russell 2000 2x
	// DRN - US REIT 3x

	private static final String[] PHILIP_SYMBOLS = "TLO,ALFA,SCHX,SCHA,SCHF,VWO,SCHZ,SCHP,SCHH,QQQ,RWX,USO,GLD,SLV,IEF,GSG,JNK,STIP,VNQI,BNDX,SCHV,GMOM,VAMO,GVAL,FYLD,GAA,SYLD"
			.split(",");

	private static final String[] JUSTIN_SYMBOLS = "VMMXX, PFORX, WACPX, VMISX, VSISX, VIFSX, RERGX, MINHX"
			.split(", ");

	private static final String[] KAREN_SYMBOLS = "GENIX,DBC,GLD,JNK,DBA,SCHA,SCHB,SCHE,SCHF,RWX,SCHH,BND,TIP,IEF"
			.split(",");

	private static final String[] COUNTRY_SYMBOLS = "GREK EGPT EWI EDEN EWP EWK EIRL EPI EWQ EIS EZA EWL EWG EWA SPY QQQ EPOL EWC EWU PGJ EWY EWT EWN EWD EWZ GXG EWO IWM EWH IDX EWS EIDO EWM THD TUR GXC FXI EPU EWW JPP EWJ BRF ECH RSX RBL"
			.split(" ");

	private static final String[] KYLE_SYMBOLS = "DXJ Hedj IEV QQQ SPHB VB RWO ALFA AOMIX CVX DEM DFE DODFX EWW IBM JNK PJP RIG SCJ SDRL SPY T TFMAX VALE VEU VTI VWO GS IWM MTU PDN QCOM TEVA ARTQX FCNKX FDIKX FSCRX FSPNX HAINX LADYX PTTRX FBAKX FLPKX PRFDX VMRAX"
			.split(" ");

	private static final String[] DISTRESSED = "vlkay TWTR grek cmg bx cg lnkd aapl seas"
			.split(" ");

	public static Set<String> ALL_SYMBOLS = new HashSet<String>() {
		{
			addAll(Arrays.asList(ANDY_SYMBOLS));
			addAll(Arrays.asList(ALEX_SYMBOLS));
			addAll(Arrays.asList(PHILIP_SYMBOLS));
			addAll(Arrays.asList(JUSTIN_SYMBOLS));
			addAll(Arrays.asList(KAREN_SYMBOLS));
			addAll(Arrays.asList(COUNTRY_SYMBOLS));
			addAll(Arrays.asList(DISTRESSED));
		}
	};

	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request)
			throws DataSourceException {

		// Create a data table,
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("symbol", ValueType.TEXT, "Symbol"));
		cd.add(new ColumnDescription("action", ValueType.TEXT, "Action"));
		cd.add(new ColumnDescription("close", ValueType.NUMBER, "Close"));
		cd.add(new ColumnDescription("highsma", ValueType.NUMBER, "High SMA"));
		cd.add(new ColumnDescription("lowsma", ValueType.NUMBER, "Low SMA"));
		cd.add(new ColumnDescription("closesma", ValueType.NUMBER, "Close SMA"));
		cd.add(new ColumnDescription("3month", ValueType.NUMBER,
				"3 Month Return"));
		cd.add(new ColumnDescription("6month", ValueType.NUMBER,
				"6 Month Return"));
		cd.add(new ColumnDescription("12month", ValueType.NUMBER,
				"12 Month Return"));
		cd.add(new ColumnDescription("avgmonth", ValueType.NUMBER,
				"3,6,12 Average Return"));
		data.addColumns(cd);

		List<RelativeStrength> strength = new ArrayList<RelativeStrength>();

		String requester = request.getParameter("requester").trim();

		String[] symbols;
		if ("kyle".equals(requester)) {
			symbols = KYLE_SYMBOLS;
		} else if ("karen".equals(requester)) {
			symbols = KAREN_SYMBOLS;
		} else if ("distressed".equals(requester)) {
			symbols = DISTRESSED;
		} else if ("country".equals(requester)) {
			symbols = COUNTRY_SYMBOLS;
		} else if ("andy".equals(requester)) {
			symbols = ANDY_SYMBOLS;
		} else if ("justin".equals(requester)) {
			symbols = JUSTIN_SYMBOLS;
		} else if ("alex".equals(requester) || "ivy".equals(requester)) {
			symbols = ALEX_SYMBOLS;
		} else {
			symbols = PHILIP_SYMBOLS;
		}

		Map<StockSymbol, KestnerSmaStrategy> stocks = new HashMap<>();
		for (String symbol : symbols) {
			StockSymbol stockSymbol = new StockSymbol(symbol);
			stocks.put(stockSymbol, new KestnerSmaStrategy(stockSymbol));
		}

		for (KestnerSmaStrategy stock : stocks.values()) {
			stock.downloadData();
			strength.add(new RelativeStrength(stock.getSymbol(), stock
					.getStockData()));
		}

		// Fill the data table.
		try {
			for (RelativeStrength rs : strength) {
				KestnerSmaStrategy stock = stocks.get(rs.getSymbol());
				TableRow row = new TableRow();
				row.addCell(getLink(stock.getSymbol()));

				String action = stock.getAction();

				addCell(row, action, action);
				addCell(row, round(stock.getMostRecentClose()), action);
				addCell(row, round(stock.getHighSMA()), action);
				addCell(row, round(stock.getLowSMA()), action);
				addCell(row, round(stock.getLowSMA()), action);
				addCell(row, round(rs.getReturnNMonthsBack(3)), action);
				addCell(row, round(rs.getReturnNMonthsBack(6)), action);
				addCell(row, round(rs.getReturnNMonthsBack(12)), action);
				addCell(row, round(rs.getAverageThreeSixTwelveReturns()),
						action);

				data.addRow(row);
			}

		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}
		return data;
	}

	private void addCell(TableRow row, String action, String action2) {
		TableCell cell = new TableCell(action);
		// HACK! google-visualization-table-td
		// cell.setCustomProperty("className", action.toLowerCase() + " "
		// + "google-visualization-table-td");
		row.addCell(cell);
	}

	private void addCell(TableRow row, double val, String action) {
		TableCell cell = new TableCell(val);
		// HACK! google-visualization-table-td
		// cell.setCustomProperty("className", action.toLowerCase() + " "
		// + "google-visualization-table-td");
		row.addCell(cell);
	}

	private String getLink(Object s) {
		return "<a href='chart.jsp?symbol=" + s + "'>" + s + "</a>";
	}

}
