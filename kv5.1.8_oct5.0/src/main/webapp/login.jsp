<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Locale"%>
<%@ page import="com.openkm.core.Config"%>
<%@ page import="com.openkm.util.FormatUtil"%>
<%@ page import="com.openkm.dao.LanguageDAO"%>
<%@ page import="com.openkm.dao.bean.Language"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/favicon.ico" />
  <% if (FormatUtil.isMobile(request)) { %>
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/mobile.css" type="text/css" />
  <% } else { %>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/desktop.css" type="text/css" />
  <% } %>
  <%
    Locale locale = request.getLocale();
    Cookie[] cookies = request.getCookies();
    String preset = null;
    
    if (cookies != null) {
      for (int i=0; i<cookies.length; i++) {
        if (cookies[i].getName().equals("lang")) {
          preset = cookies[i].getValue();
        }
      }
    }
    
    if (preset == null || preset.equals("")) {
      preset = locale.getLanguage()+"-"+locale.getCountry();
    }
  %>
  <title>Knowledge Vault Login</title>
</head>
<body onload="document.forms[0].elements[0].focus()">
<% if (!FormatUtil.isMobile(request)) { %>
  <center style="margin-top: 40px;"><%=Config.LOGO_TEXT %></center>
   <% } %>
  <div id="box">
    <div id="logo"></div>
    <div id="error"><%=request.getParameter("error")!=null?"Authentication error":"" %></div>
    <!--div id="text">
      <center><img src="<%=request.getContextPath() %>/img/lock.png"/></center>
      <p>Welcome to OpenKM !</p>
      <p>Use a valid username and password to access to OpenKM user Desktop.</p>
    </div-->
    <div id="form">
      <form name="login" method="post" action="j_security_check" onsubmit="setCookie()">
        <% if (Config.SYSTEM_MAINTENANCE) { %>
          <table border="0" cellpadding="2" cellspacing="0" align="center" class="demo" style="width: 100%">
          <tr><td class="demo_alert">System under maintenance</td></tr>
          </table>
          <input name="j_username" id="j_username" type="hidden" value="<%=Config.SYSTEM_LOGIN_LOWERCASE?Config.ADMIN_USER.toLowerCase():Config.ADMIN_USER%>"/><br/>
        <% } else { %>
          <label for="j_username">User</label><br/>
          <input name="j_username" id="j_username" type="text" <%=Config.SYSTEM_LOGIN_LOWERCASE?"onchange=\"makeLowercase();\"":""%>/><br/><br/>
        <% } %>
        <label for="j_password">Password</label><br/>
        <input name="j_password" id="j_password" type="password"/><br/><br/>
        <!--added by vissu on 23oct -->
        <!-- p style="width:250px;">
		Test Drive Knowledge Vault details<br>
		<b>Username:</b> joebloggs<br>
		<b>Password:</b> joebloggs<br><br>
		
		Username and Password are Case Sensitive.
		</p --> 
     
     
     <%--Commented by vissu on 23oct
       <% if (!FormatUtil.isMobile(request)) { %> 
          <label for="j_language">Language</label><br/>
          <select name="j_language" id="j_language">
          <%
            List<Language> langs = LanguageDAO.findAll();
            String whole = null;
            String part = null;
            
            // Match whole locale
            for (Language lang : langs) {
              String id = lang.getId();
              
              if (preset.equalsIgnoreCase(id)) {
                whole = id;
              } else if (preset.substring(0, 2).equalsIgnoreCase(id.substring(0, 2))) {
                part = id;
              }
            }
            
            
            // Select selected
            for (Language lang : langs) {
              String id = lang.getId();
              String selected = "";
              
              if (whole != null && id.equalsIgnoreCase(whole)) {
                selected = "selected";
              } else if (whole == null && part != null && id.equalsIgnoreCase(part)) {
                selected = "selected";
              } else if (whole == null && part == null && "en-GB".equals(id)) {
                selected = "selected";
              }
              
              out.print("<option "+selected+" value=\""+id+"\">"+lang.getName()+"</option>");
            }
          %>
          </select>
        <% } %>	
        comment ended on 23oct	--%>
        <input value="Login" name="submit" type="submit"/><br/>
      </form>
    </div>
  </div>
  
  <% if (Config.SYSTEM_DEMO) { %>
    <jsp:include flush="true" page="login_demo_users.jsp"/>
  <% } else if (!Config.HIBERNATE_HBM2DDL.equals("none")) { %>
    <table border="0" cellpadding="2" cellspacing="0" align="center" class="demo">
      <tr><td class="demo_title">WARNING</td></tr>
      <tr><td class="demo_alert"><%=Config.PROPERTY_HIBERNATE_HBM2DDL%> = <%=Config.HIBERNATE_HBM2DDL%></td></tr>
    </table>
  <% } %>

  <script type="text/javascript">
    function makeLowercase() {
      var username = document.getElementById('j_username'); 
      username.value = username.value.toLowerCase();
    }

    function setCookie() {
      var exdate = new Date();
      var value = document.getElementById('j_language').value;
      exdate.setDate(exdate.getDate() + 7);
      document.cookie="lang="+escape(value)+";expires="+exdate.toUTCString();
    }
  </script>
  <% if (FormatUtil.isMobile(request)) { %>
    <div>
  <% } else { %>
  <div style="float:right">
  <% } %>
<img style="border: 0px none;" src="../img/footer_logo.png" onclick="javascript:window.open('http://knowledgevault.com.au/')" height="48" width="521">
</div>
</body>
</html>
