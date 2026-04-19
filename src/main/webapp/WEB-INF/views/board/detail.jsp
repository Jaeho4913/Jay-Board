<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>상세 보기</title>
		<style>
			body{width: 800px; margin: 0 auto; padding 20px;}
			table{width: 100%; border-collapse: collapse; margin-bottom: 20px;}
			th, td{border: 1px solid #ddd; padding:15px;}
			th{background-color: #f2f2f2; width: 100px;}
			.btn-area{padding: 10px 20px; cursor: pointer;}
		</style>
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	</head>
	<body>
		<h2>📖 글 상세 보기</h2>

		<table>
			<tr>
				<th>번호</th>
				<td id="v_idx">로딩중...</td>
			</tr>
			<tr>
				<th>작성일</th>
				<td id="v_createdAt">로딩중...</td>
			</tr>
			<tr>
				<th>작성자</th>
				<td id="v_writer">로딩중...</td>
			</tr>
			<tr>
				<th>조회수</th>
				<td id="v_viewCnt">로딩중...</td>
			</tr>
			<tr>
				<th>내용</th>
				<td id="v_content" style="height: 200px; vertical-align: top;">로딩중...</td>
			</tr>
		</table>

		<div class="btn-area">
			<button id="btnList">목록으로</button>
			<span id="authBtnArea"></span>
			<script>
			$(document).ready(function () {
				getDetail();
			});

			const urlParams = new URLSearchParams(window.location.search);
			const idx = urlParams.get('idx');
			const page = urlParams.get('page') || 1;
			const searchType = urlParams.get('searchType') || '';
			const keyword = urlParams.get('keyword') || '';
			const sortType = urlParams.get('sortType') || 'latest';

			function getDetail() {
			$("#btnList").on("click", function(){
				location.href = '/?page=' +page + '&searchType=' + searchType + '&keyword=' + keyword + '&sortType=' + sortType;
				})	;
			$.ajax ({
				type: "GET",
				url: "/board/getDetail",
				data: {idx: idx},
				dataType: "json",
				success: function(response) {
				$("#v_idx").text(response.idx);
				$("#v_createdAt").text(response.createdAt);
				$("#v_writer").text(response.writer);
				$("#v_viewCnt").text(response.viewCnt);
				$("#v_content").text(response.content);

				if (response.isAuth === true) {
					let btnHtml = '<button onclick="location.href=\'/board/update?idx=' + idx +'\'">수정</button>' +
										'<button onclick= "authDelete()">삭제</button>';
					$("#authBtnArea").html(btnHtml);
				} else if (response.isGuest === true) {
					let btnHtml = '<button onclick="guestUpdate()">수정</button>' +
										'<button onclick="guestDelete()">삭제</button>';
					$("#authBtnArea").html(btnHtml);
				}
				}
			})	;
			}
			function guestUpdate() {
				var pw = prompt("글 작성 시 입력한 비밀번호를 입력하세요 : ");
				if (!pw) return;

				fetch('/board/checkGuestPw', {
				method: 'POST',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				body: 'idx=' + idx + '&boardPw=' + encodeURIComponent(pw)
				})
				.then(response => response.text())
				.then(result => {
					if (result === "success") {
						location.href = '/board/update?idx=' + idx + '&boardPw=' + pw;
					} else {
						alert("비밀번호가 일치하지 않습니다.")
					}
				})
				.catch(error => console.error('Error:', error));
			}
			function guestDelete() {
				var pw = prompt("글 작성 시 입력한 비밀번호를 입력하세요: ");
				if (!pw) return;

				if (!confirm("정말 삭제하시겠습니까?")) return;

				fetch('/board/deleteGuestPost', {
					method: 'POST',
					headers: { 'Content-Type': 'application/x-www-form-urlencoded'},
					body: 'idx=' + idx + '&boardPw=' + encodeURIComponent(pw)
				})
				.then(response => response.text())
				.then(result => {
					if (result === "success") {
						alert("게시글이 삭제되었습니다.");
						location.href = '/?page=' + page + '&searchType=' + searchType + '&keyword=' + keyword + '&sortType=' + sortType;
					} else {
						alert("비밀번호가 일치하지 않습니다.");
					}
				})
				.catch(error => console.error('Error: ', error));
			}
			function authDelete() {
				if(!confirm("정말 삭제하시겠습니까?")) return;

				$.ajax({
					type: "GET",
					url: "/board/delete",
					data: {idx: idx},
					success: function(result) {
						console.log(result);
						if (result === "success") {
							alert("게시글이 삭제되었습니다.");
							location.href = '/?page=' + page + '&searchType=' + searchType + '&keyword=' + keyword + '&sortType=' + sortType;
						} else {
							alert("게시글을 삭제할 수 없습니다.")
						}
					},
					error: function() {
						alert("서버 통신 오륙 발생했습니다.")
					}
				})

			}
			</script>
		</div>
	</body>
</html>