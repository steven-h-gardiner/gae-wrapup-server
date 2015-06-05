<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
    org.json.JSONObject o = new org.json.JSONObject();
    o.putOpt("hash", request.getParameter("hash")); 
    o.putOpt("url", request.getParameter("url"));
    System.err.println("REDIRECT WITH " + o.toString());
    if (o.has("url")) {
      response.sendRedirect(o.optString("url"));
      return;      
    }
    response.setStatus(response.SC_NOT_FOUND);
  </jsp:scriptlet>
  
  <jsp:text>
    <html>
      <head>
        <title>Page NOT Found</title>
      </head>
      <body>
        This is probably an error
      </body>
    </html>
  </jsp:text>
</jsp:root>