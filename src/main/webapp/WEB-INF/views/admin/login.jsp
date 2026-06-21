<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>관리자</title>
	<style>
		body {
			width: 400px;
			margin: 100px auto;
			font-family: Arial, sans-serif;
		}
		.login-box {
			border: 1px solid #ddd;
			padding: 30px;
			border-radius: 8px;
		}
		input {
			width: 100%;
			padding: 10px;
			margin-bottom: 10px;
			box-sizing: border-box;
		}
		button {
			width: 100%;
			padding: 10px;
			cursor: pointer;
		}
		.error {
			color: red;
			margin-bottom: 10px;
		}
		.logout {
			color: green;
			margin-bottom : 10px;
		}
	</style>
</head>
<body>

	<div class="login-box">
		<h2>관리자 로그인</h2>

		<% if (request.getParameter("error") != null) { %>
			<div class="error">관리자 아이디 또는 비밀번호를 확인해주세요.</div>
		<% } %>

		<% if (request.getParameter("logout") != null) { %>
			<div class="logout">로그아웃되었습니다.</div>
		<% } %>

		<form action="/admin/loginPost" method="post">
			<input type="text" name="adminId" placeholder="관리자 ID" required>
			<input type="password" name="password" placeholder="비밀번호" required>
			<button type="submit">로그인</button>
		</form>
	</div>

</body>
</html>