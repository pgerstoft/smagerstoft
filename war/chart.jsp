<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html>
<html>
<head>
<title>SMA Relative Strength Chart</title>
<!--Load the AJAX API-->
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
 
  //Load the Visualization API and the ready-made Google table visualization
  google.load('visualization', '1', {'packages':['corechart']});

  // Set a callback to run when the API is loaded.
  google.setOnLoadCallback(init);

  // Send the query to the data source.
  function init() {

    // Specify the data source URL.
    var query = new google.visualization.Query('smachart?symbol=<%=request.getParameter("symbol")%>');

    // Send the query with a callback function.
    query.send(handleQueryResponse);
  }

  // Handle the query response.
  function handleQueryResponse(response) {
	  $("#spinner-div").remove();
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var data = response.getDataTable();
		var chart = new google.visualization.LineChart(document
				.getElementById('chart_div'));
		chart.draw(data, {});
	}
</script>
</head>
<body>
	<%@include file="_nav_bar.html"%>
	<div class="container">
		<div class="row">
			
			<div id class="col-md-12"> 
				<h1 class="page-header"><%=request.getParameter("symbol")%> <small><a href="">Google Finance</a></small></h1>
				
				<%@include file="_spinner.html"%>
				<!--Div that will hold the visualization-->
				<div id="chart_div" style="height: 500px;"></div>
			</div>
		</div>
	</div>
</body>
</html>