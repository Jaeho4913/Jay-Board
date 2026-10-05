<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<!DOCTYPE html>
		<html lang="ko">

		<head>
			<link rel="stylesheet" href="/css/board-live.css">
			<meta charset="UTF-8">
			<title>라이브 게시판</title>
		</head>

		<body>
			<h2>
				<c:out value="${boardGroup.boardGroupName}" />
			</h2>
			<form id="livePostForm">
				<textarea id="livePostContent" rows="3" placeholder="내용을 입력해주세요."></textarea>
				<button type="submit">등록</button>
			</form>
			<div id="livePostList" data-board-group-idx="${boardGroup.boardGroupIdx}">
			
			</div>
			<div id="livePostSentinel">이전 글 불러오기</div>
			<button type="button" id="livePostRetry" class="is-hidden">다시 시도</button>
			<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
			<script src="/js/board-live.js"></script>
		</body>

		</html>