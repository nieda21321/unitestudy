<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>홈</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 50px; text-align: center; }
        h1 { color: #333; }
        a { color: #4CAF50; text-decoration: none; font-size: 16px; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>

<h1>홈 페이지</h1>
<p>로그인에 성공했습니다!</p>

<a href="${pageContext.request.contextPath}/logout">로그아웃</a>

</body>
</html>
