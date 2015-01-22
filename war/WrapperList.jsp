<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
    <![CDATA[

      org.json.JSONObject wrapperObj = new org.json.JSONObject();
      org.json.JSONArray wrapperList = new org.json.JSONArray();
      org.json.JSONArray wrapper1 = new org.json.JSONArray();

      wrapper1.put("abc");
      wrapper1.put("234/");
      wrapper1.put("qed");

      wrapperList.put(wrapper1);

      for (int i = 0; i < 100; i++) {
        org.json.JSONArray row = new org.json.JSONArray();
	row.put(i);
	row.put("ROW1" + i);
	row.put("ROWz" + Math.random());
	wrapperList.put(row);
      }

      org.json.JSONArray columns = new org.json.JSONArray();
      columns.put(new org.json.JSONObject("{title: 'col1'}"));
      columns.put(new org.json.JSONObject("{title: 'col2'}"));
      columns.put(new org.json.JSONObject("{title: 'col3'}"));

      wrapperObj.putOpt("dataset", wrapperList);
      wrapperObj.putOpt("columns", columns);

      org.json.JSONObject spec = new org.json.JSONObject();
      //spec.putOpt("mockup", true);

      spec.putOpt("newest", request.getParameter("newest"));
      spec.putOpt("newest", Boolean.parseBoolean(spec.optString("newest", "false")));

      org.json.JSONObject wrappers = new edu.cmu.mixer.wrapup.WrapperList(spec).getJSONObject();
    ]]>
  </jsp:scriptlet>
  <c:set var="wrapperJSON">
   <jsp:expression>wrappers.toString()</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title> View Wrappers </title>
	<script type="text/javascript" id="jquery" src="/js/jquery-latest.min.js">
	  <!-- JSP workaround -->
	</script>
	<script type="text/javascript" id="dataTables" src="/js/jquery.dataTables.min.js">
	  <!-- JSP workaround -->
	</script>
	<script type="application/json" id="wrapperData">
	  ${wrapperJSON}
	</script>
	<style type="text/css">
	  #main {
	    margin-left: auto;
	    margin-right: auto;
	    width: 50%;
	    text-align: center;
	  }
	</style>
      </head>
      <body>
	<center id="main">	  
	  <table cellpadding="0" cellspacing="0" border="1" class="display" id="mainTable">
	  </table>
	</center>
	<table cellpadding="0" cellspacing="0" border="1" class="display" id="lumber">
	  <thead>
	    <tr>
	      <th>foo</th>
	    </tr>
	  </thead>
	  <tbody>
	    <tr>
	      <td>bar</td>
	    </tr>
	  </tbody>
	</table>
      </body>
      <script id="driver" type="text/javascript">
	jQuery(document).on('ready', function() {
	  var wl = {};
	  wl.data = JSON.parse(jQuery("#wrapperData").text());
	  wl.tablify = function(selector) {
	    var tab = jQuery("#mainTable");
	    var thead = jQuery("#lumber thead").clone().empty();
	    thead.appendTo(tab);
	    var tbody = jQuery("#lumber tbody").clone().empty();
	    tbody.appendTo(tab);
	    var header = jQuery("#lumber thead tr").clone().empty();
	    header.appendTo(thead);
	    this.data.columns.forEach(function(col) {
	      var hCell = jQuery("#lumber thead th").clone();
	      hCell.text(col.title);
	      hCell.appendTo(header);
	    });
	    this.data.dataset.forEach(function(row) {
	      var rowElt = jQuery("#lumber tbody tr").clone().empty();
	      rowElt.appendTo(tbody);
	      row.forEach(function(cell) {
	        var dCell = jQuery("#lumber tbody td").clone();
		dCell.text(cell);
		dCell.appendTo(rowElt);
	      });
	    });
	  };
	  wl.tablify("#mainTable");
	  jQuery("#mainTable").dataTable({
	    //"data": wl.data.dataset,
	    //"columns": wl.data.columns,
	    "order": [],
	  });
	});
      </script>
    </html>
  </jsp:text>
</jsp:root>

