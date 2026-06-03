<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>로그인 페이지</title>
	</head>
	<body>
		<h2>로그인</h2>
		<c:if test="${param.error == 'true' }">
			<p style="color:red;">아이디 또는 비밀번호가 올바르지 않습니다.</p>
		</c:if>

		<form id="saveForm" action="/member/loginPost" method="post">
			<p>
				<label>아이디 :</label>
				<input type="text" name="userId" required>
			</p>
			<p>
				<label>비밀번호 :</label>
				<input type="password" name="password" required>
			</p>
			<button type="submit">로그인</button>
		</form>
			<a href="/member/find">아이디/비밀번호 찾기</a> |
			<a href="/member/save">회원가입하러 가기</a> |
			<a href="/">메인 화면으로</a>
	</body>
</html>