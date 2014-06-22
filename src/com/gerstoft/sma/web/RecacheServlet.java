package com.gerstoft.sma.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gerstoft.sma.KestnerSmaStrategy;

public class RecacheServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		for (String symbol : SmaNotificationServlet.ALL_SYMBOLS) {
			KestnerSmaStrategy stock = new KestnerSmaStrategy(symbol, "");
			try {
				stock.downloadData();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

}
