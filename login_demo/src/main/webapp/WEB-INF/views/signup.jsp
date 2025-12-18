<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<h2>회원가입</h2>
<c:if test="${not empty error}">
  <div style="color:red">${error}</div>
</c:if>
<form method="post" action="/signupProc">
  <input type="text" name="memberId" placeholder="아이디" required/><br/>
  <input type="password" name="memberPw" placeholder="비밀번호" required/><br/>
  <input type="text" name="memberName" placeholder="이름" required/><br/>
  <input type="email" name="email" placeholder="이메일"/><br/>
  <button type="submit">회원가입</button>
</form>
</body>
</html>