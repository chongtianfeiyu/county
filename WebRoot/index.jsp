<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.sin.CityUtil;"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <script type="text/javascript" src="/jquery-1.8.2.min.js"></script>
  <script type="text/javascript" src="/cityData.js"></script>
  <script type="text/javascript">
	$(function($) {
		$().cityTools({
			provinceId : "province",
			cityId : "city",
			countyId : "county",
			dfCode : "820000",
			backCodeId : "back"
		});
	});
  </script>
  <body>
	<select id="province"></select>
	<select id="city"></select>
	<select id="county"></select>
	<div> 最后返回代码值 <input  id="back" type="text"></div>
  </body>
</html>
