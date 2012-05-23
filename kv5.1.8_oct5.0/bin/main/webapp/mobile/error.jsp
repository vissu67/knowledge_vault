<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page isErrorPage="true"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.openkm.core.Config"%>
<%@page import="com.openkm.core.HttpSessionManager"%>
<%@page import="com.openkm.bean.HttpSessionInfo"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/mobile.css" type="text/css" />
  <title>OpenKM Error</title>
</head>
<body>
  <div id="box-error">
    <div id="logo"></div>
    <div id="text-error">
      <center><img src="<%=request.getContextPath()%>/img/error.png"/></center>
    </div>
    <div id="form-error">
      <div id="error"><%=exception.getMessage()%></div>
      <center><input type="button" value="Go back" onclick="history.go(-1)"/></center>
    </div>
  </div>
</body>
</html>