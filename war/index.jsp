<!DOCTYPE HTML>

<html>
<head>
<title>SMA Gerstoft</title>
</head>

<body>
	<%@include file="_nav_bar.html"%>
	<div class="container">
		<h1 class="page-header">SMA Relative Strength</h1>
		<h3>Overview</h3>
		<p>
			This website is meant to help implement the <a
				href="http://papers.ssrn.com/sol3/papers.cfm?abstract_id=962461">Quantitative
				Tactical Asset Allocation</a> strategy described by <a
				href="http://mebfaber.com/"> Mebane Faber</a>. The stocks in the
			tables are ranked by the average of their 3, 6, and 12 months
			returns. This is an optimization to QTAA that Mebane Faber calls <a
				href="http://papers.ssrn.com/sol3/papers.cfm?abstract_id=1585517">Relative
				Strength</a>.
		</p>
		<p>In a <a href="http://mebfaber.com/2012/03/05/how-to-get-to-20-or-extensions-to-qtaa/">backtest</a>, this combination of strategies had a 13% return with a max drawdown
			of 10% (compare this to a buy and hold max drawdown of ~50%).</p>

		<h3>Recommended Usage</h3>
		<p>On the last day of every month check the top 5 securities
			sorted on an average of their 3, 6, and 12 months returns. Sell any
			position that is not in the top 5 or below its 200 day moving
			average.</p>
		<p>Buy a security if it is in the top 5 securities and above its
			200 day moving average.</p>
		<p>Every holding is 1/5 of your portfolio. Any money left over is
			put into a bond fund. So if you only have 3 securities then 40% of
			your portfolio is in bonds.</p>
		<p><a href="mailto:philipgerstoft@gmail.com?Subject=QTAA%20questions" target="_top">Let me know</a> if I have been unclear.</p>
	</div>

</body>
</html>
