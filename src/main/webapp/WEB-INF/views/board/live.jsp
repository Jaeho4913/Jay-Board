<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<!DOCTYPE html>
		<html lang="ko">

		<head>
			<meta charset="UTF-8">
			<title>라이브 게시판</title>
		</head>

		<body>
			<h2>
				<c:out value="${boardGroup.boardGroupName}" />
			</h2>
			<div id="livePostList" data-board-group-idx="${boardGroup.boardGroupIdx}">

			</div>
			<div id="livePostSentinel">이전 글 불러오기</div>
			<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
			<script src="/js/board-live.js"></script>
		</body>

		</html>