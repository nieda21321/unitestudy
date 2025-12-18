<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .login-container { background-color: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 300px; }
        h2 { text-align: center; margin-bottom: 20px; }
        input[type=text], input[type=password] { width: 100%; padding: 10px; margin: 10px 0; border-radius: 5px; border: 1px solid #ccc; }
        button { width: 100%; padding: 10px; border: none; border-radius: 5px; background-color: #4CAF50; color: white; font-size: 16px; cursor: pointer; }
        button:hover { background-color: #45a049; }
        .error { color: red; font-size: 14px; margin-bottom: 10px; text-align: center; }
        .signup-link { text-align: center; margin-top: 10px; font-size: 14px; }
    </style>
</head>
<body>

<div class="login-container">
    <h2>로그인</h2>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/loginProc" method="post">
        <input type="text" name="username" placeholder="아이디" required />
        <input type="password" name="password" placeholder="비밀번호" required />
        <button type="submit">로그인</button>
    </form>

    <div class="signup-link">
        <a href="${pageContext.request.contextPath}/signup">회원가입</a>
    </div>
</div>

</body>
</html>
