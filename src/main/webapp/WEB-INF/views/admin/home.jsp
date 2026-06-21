<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>관리자 대시보드</title>
	<style>
		body {
			width: 1100px;
			margin: 0 auto;
			padding: 30px;
			font-family: Arial, sans-serif;
		}
		.header {
			display: flex;
			justify-content: space-between;
			aligin-items: center;
			margin-bottom: 30px;
		}
		.menu {
			display: felx;
			gap: 10px;
			margin-bottom: 30px;
		}
		.menu a {
			border: 1px solid #ddd;
			padding: 10px 15px;
			text-decoration: none;
			coloer: #333;
			border-radius: 5px;
		}
		.card-area {
			display: grid;
			grid-template-columns: repeat(4, 1fr);
			gap: 15px;
		}
		.card {
			border: 1px solid #ddd;
			padding: 20px;
			border-radius: 8px;
			background #fafafa;
		}
		.card-title {
			color: #777;
			font-size: 14px;
		}
		.card-value {
			font-size: 26px;
			font-weight: bold;
			margin-top: 10px
		}
	</style>
</head>
<body>

	<div class="header">
		<h2>JAY 게시판 관리자</h2>
		<form action="/admin/logout" method="post">
			<button type="submit">로그아웃</button>
		</form>
	</div>

	<div class="menu">
		<a href="/admin">대시보드</a>
		<a href="/admin/members">회원 관리</a>
		<a href="/admin/boards">게시글 관리</a>
		<a href="/admin/replies">댓글 관리</a>
		<a href="/admin/categories">카테고리 관리</a>
		<a href="/admin/statistics">통계</a>
		<a href="/admin/logs">운영 로그</a>
		<a href="/admin/admins">관리자 관리</a>
	</div>

	<h3>월간 현황</h3>

	<div class="card-area">
		<div class="card">
			<div class="card-title">이번달 가입회원</div>
			<div class="card-value">0</div>
		</div>
		<div class="card">
			<div class="card-title">이번달 게시글</div>
			<div class="card-value">0</div>
		</div>
		<div class="card">
			<div class="card-title">이번달 댓글</div>
			<div class="card-value">0</div>
		</div>
		<div class="card">
			<div class="card-title">이번달 좋아요</div>
			<div class="card-value">0</div>
		</div>
	</div>
</body>
</html>