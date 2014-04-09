package com.gerstoft.sma.util;

import java.math.BigDecimal;

public class Round {

	public static double round(Number number) {
		return round(number.doubleValue());
	}

	public static double round(double unrounded) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}

}
