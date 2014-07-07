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
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

@SuppressWarnings("serial")
public class SmaNotificationServlet extends DataSourceServlet {

	private static final String[] ANDY_SYMBOLS = "MDIDX,ODMAX,PIEQX,AREAX,ATEKX,AUIAX,QUAKX,ABSKX,NMSAX,NTIAX,ALCKX,PEOPX,ANAGX,AGDAX,SNIDX,VIPSX"
			.split(",");

	private static final String[] ANDY_2_SYMBOLS = "DXQLX,DXSLX,DXRLX,SECIX,RYMSX,SSUAX,LGILX,BIPIX,BLPIX,CNPIX,CYPIX,IDPIX,INPIX,LGPIX,LVPIX,MGPIX,MDPIX,MLPIX,OTPIX,PHPIX,SGPIX,SLPIX,SVPIX,TCPIX,UDPIX,ULPIX,UMPIX,HCPIX,UAPIX,RYOIX,RYCIX,RYCVX,RYHIX,RYHGX,RYLIX,RYMDX,RYOCX,RYNVX,RYRIX,RYMKX,RYRHX,RYTNX,RYSPX,RYAWX,RYZAX,RYBHX,RYWAX,RYUIX,SNXFX,SWOBX,SWANX,SWDSX,SFLNX,SFSNX,SWHFX,SWIIX,SWLSX,SWEGX,SWBGX,SWCGX,SWHGX,SWKRX,SWLRX,SWJRX,SWPPX,SWSSX,SWSCX,SWBRX,SWGRX,SWCRX,SWHRX,SWDRX,SWIRX,SWERX,SWTSX"
			.split(",");

	private static final String[] ALEX_SYMBOLS = "BND,DBC,TIP,GSG,RWX,VB,VEU,VNQ,VTI,VWO"
			.split(",");

	private static final String[] PHILIP_SYMBOLS = "ALFA,SWPPX,SWSSX,SWISX,VWO,SWLBX,SWRSX,SCHH,OTPIX,JERTX,USO,GLD,SLV,IEF,GSG"
			.split(",");

	private static final String[] JUSTIN_SYMBOLS = "VMMXX, PFORX, WACPX, VMISX, VSISX, VIFSX, RERGX, MINHX"
			.split(", ");

	private static final String[] KAREN_SYMBOLS = "GENIX,DBC,GLD,JNK,DBA,DBC,SCHA,SCHB,SCHE,SCHF,RWX,SCHH,BND,TIP,IEF"
			.split(",");

	private static final String[] COUNTRY_SYMBOLS = "EGPT EWI EDEN EWP EWK EIRL EPI EWQ EIS EZA EWL EWG EWA SPY QQQ EPOL EWC EWU PGJ EWY EWT EWN EWD EWZ GXG EWO IWM EWH IDX EWS EIDO EWM THD TUR GXC FXI EPU EWW JPP EWJ ITF BRF ECH RSX RBL"
			.split(" ");

	public static Set<String> ALL_SYMBOLS = new HashSet<String>() {
		{
			addAll(Arrays.asList(ANDY_SYMBOLS));
			addAll(Arrays.asList(ANDY_2_SYMBOLS));
			addAll(Arrays.asList(ALEX_SYMBOLS));
			addAll(Arrays.asList(PHILIP_SYMBOLS));
			addAll(Arrays.asList(JUSTIN_SYMBOLS));
			addAll(Arrays.asList(KAREN_SYMBOLS));
			addAll(Arrays.asList(COUNTRY_SYMBOLS));
		}
	};

	@Override
	public DataTable generateDataTable(Query arg0, HttpServletRequest arg1)
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

		String requester = arg1.getParameter("requester").trim();
		String[] symbols;
		if ("karen".equals(requester)) {
			symbols = KAREN_SYMBOLS;
		} else if ("country".equals(requester)) {
			symbols = COUNTRY_SYMBOLS;
		} else if ("andy".equals(requester)) {
			symbols = ANDY_SYMBOLS;
		} else if ("andy2".equals(requester)) {
			symbols = ANDY_2_SYMBOLS;
		} else if ("justin".equals(requester)) {
			symbols = JUSTIN_SYMBOLS;
		} else if ("alex".equals(requester) || "ivy".equals(requester)) {
			symbols = ALEX_SYMBOLS;
		} else {
			symbols = PHILIP_SYMBOLS;
		}

		Map<String, KestnerSmaStrategy> stocks = new HashMap<String, KestnerSmaStrategy>();
		for (String symbol : symbols) {
			stocks.put(symbol, new KestnerSmaStrategy(symbol, ""));
		}

		for (KestnerSmaStrategy stock : stocks.values()) {
			stock.downloadData();
			strength.add(new RelativeStrength(stock.getSymbol(), stock
					.getStockData()));
		}

		// Fill the data table.
		try {
			for (RelativeStrength relativeStrength : strength) {
				KestnerSmaStrategy stock = stocks.get(relativeStrength
						.getSymbol());
				data.addRowFromValues(getLink(stock.getSymbol()), stock
						.getAction(), round(stock.getMostRecentClose()),
						round(stock.getHighSMA()), round(stock.getLowSMA()),
						round(stock.getCloseSMA()), round(relativeStrength
								.get3MonthReturn()), round(relativeStrength
								.get6MonthReturn()), round(relativeStrength
								.get12MonthReturn()), round(relativeStrength
								.getAverageThreeSixTwelveReturns()));
			}

		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}
		return data;
	}

	private String getLink(String s) {
		return "<a href=\"/chart.jsp?symbol=" + s + "\">" + s + "</a>";
	}

}
