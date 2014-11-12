
<!DOCTYPE html>
<html>
<head>
<title>SMA Relative Strength Table</title>
<!--Load the AJAX API-->
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript">
  $(".explanation").hide();
  //Load the Visualization API and the ready-made Google table visualization
  google.load('visualization', '1', {'packages':['table']});

  // Set a callback to run when the API is loaded.
  google.setOnLoadCallback(init);

  // Send the query to the data source.
  function init() {

    // Specify the data source URL.
    var query = new google.visualization.Query('smanotification?requester=<%=request.getParameter("requester")%>');

		// Send the query with a callback function.
		query.send(handleQueryResponse);
	}

	// Handle the query response.
	function handleQueryResponse(response) {
		$("#spinner-div").remove();
		$(".explanation").show();
		if (response.isError()) {
			alert('Error in query: ' + response.getMessage() + ' '
					+ response.getDetailedMessage());
			return;
		}

		// Draw the visualization.
		var data = response.getDataTable();
		var chart = new google.visualization.Table(document
				.getElementById('chart_div'));
		chart.draw(data, {
			is3D : true,
			sortColumn : data.getNumberOfColumns() - 1,
			sortAscending : false,
			allowHtml : true,
			cssClassNames: {"Buy" : "buy google-visualization-table-td", "Sell" : "sell", "Hold": "hold"}
		});
	}
</script>
</head>
<body>
	<%@include file="_nav_bar.html"%>
	<div class="container">
		<%@include file="_spinner.html"%>
		
		<!--Div that will hold the visualization-->
		<div id="chart_div"></div>
		<p class="explanation hide">For mutual funds, since there is no High/Low, I use +/- 1
			percent of the close as the high and low.</p>
	</div>
</body>
</html>