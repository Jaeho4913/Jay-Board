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
				<th>공감</th>
				<td>
					<button type="button" id="btnLike">♡</button>
					<span id="v_likeCnt" style="cursor:pointer; text-decoration:underline;">0</span>
				</td>
			</tr>
			<tr>
				<th>내용</th>
				<td id="v_content" style="height: 200px; vertical-align: top;">로딩중...</td>
			</tr>
		</table>

		<div id="replyArea" style="margin-top:30px; border-top:1px solid #ddd; padding-top:20px;">

			<h3>댓글</h3>

				<div id="replyWriteArea" style="margin-bottom:15px;">
					<textarea id="replyContent"
									maxlength="1000"
									placeholder="댓글을 입력하세요."
									style="width:100%; height:80px; padding:10px; resize:none;"></textarea>

					<div style="text-align:right; margin-top:5px;">
						<span id="replyLength">0</span>/1000
						<button type="button" id="btnReplySave">댓글 등록</button>
					</div>
				</div>

				<div id="replyList">
					댓글을 불러오는 중입니다...
				</div>
		</div>

		<div id= "likeModal" style="display:none; position:fixed; left:0; top:0; width:100%; height:100%; background:rgba(0,0,0,0.4); z-index:9999;">
			<div style="
				background:white;
				width:350px;
				max-width: 90%;
				margin:150px auto;
				padding:20px;
			    border-radius:10px;
			 ">
				<h3>좋아요 한 사람</h3>
				<div id="likeModalBody"></div>
				<br>
				<button type="button" id="btnCloseLikeModal">닫기</button>
			</div>
		</div>

		<div class="btn-area">
			<button id="btnList">목록으로</button>
			<span id="authBtnArea"></span>
			<script>
			const urlParams = new URLSearchParams(window.location.search);
			const idx = urlParams.get('idx');
			const page = urlParams.get('page') || 1;
			const searchType = urlParams.get('searchType') || '';
			const keyword = urlParams.get('keyword') || '';
			const sortType = urlParams.get('sortType') || 'latest';

			$(document).ready(function () {
				getDetail();
				getReplyList();

				$("#btnReplySave").on("click", function() {
					saveReply();
				});
				$("#replyContent").on("input", function() {
					$("#replyLength").text($(this).val().length);
				});
				$("#btnCloseLikeModal").on("click", function() {
					$("#likeModal").hide();
				});
				$("#btnList").on("click", function(){
					location.href = '/?page=' + page + '&searchType=' + searchType + '&keyword=' + keyword + '&sortType=' + sortType;
				});

				$("#btnLike").on("click", function() {
					$.ajax({
						type: "POST",
						url: "/board/like",
						data: {idx: idx},
						dataType: "json",
						success: function(res) {
							if (res.status === "loginRequired") {
								alert("로그인 후 가능합니다.");
								return;
							}

							if (res.status === "success") {
								$("#v_likeCnt").text(res.likeCnt);
								$("#btnLike").text(res.likeCheck === true ? "♥" : "♡" );
								$("#likeModalBody").empty();
								$("#likeModal").hide();

								if (res.likeCheck === true) {
									$("#btnLike").text("♥");
								} else {
									$("#btnLike").text("♡");
								}
							}
						},
						error: function() {
							alert("오류가 발생했습니다.");
						}
					});
				});
				$("#v_likeCnt").on("click", function(){
					$.ajax({
						type: "GET",
						url: "/board/likeUsers",
						data: {idx: idx},
						dataType: "json",
						success: function(users){
							let html = "";

							if (!users || users.length ===0) {
								html = "<div>공감한 사용자가 없습니다.</div>";
							} else {
								users.forEach(function(user) {
									html += "<div>"
										+ user.userName
										+ " (" + user.userId + ")"
										+"</div>";
								});
							}
							$("#likeModalBody").html(html);
							$("#likeModal").show();
						},
						error: function() {
							alert("공감 목록을 불러오지 못했습니다.");
						}
					});
				});
			});

			function getDetail() {
				$.ajax({
					type: "GET",
					url: "/board/getDetail",
					data: { idx: idx },
					dataType: "json",
					success: function(response) {
						$("#v_idx").text(response.idx);
						$("#v_createdAt").text(response.createdAt);
						$("#v_writer").text(response.writer);
						$("#v_viewCnt").text(response.viewCnt);
						$("#v_likeCnt").text(response.likeCnt);
						$("#v_content").text(response.content);

						if (response.likeCheck === true) {
							$("#btnLike").text("♥");
						} else {
							$("#btnLike").text("♡");
						}

						if (response.isAuth === true) {
							let btnHtml = '<button onclick="location.href=\'/board/update?idx=' + idx +'\'">수정</button>' +
												'<button onclick= "authDelete()">삭제</button>';
							$("#authBtnArea").html(btnHtml);
						} else if (response.isGuest === true) {
							let btnHtml = '<button onclick="guestUpdate()">수정</button>' +
												'<button onclick="guestDelete()">삭제</button>';
							$("#authBtnArea").html(btnHtml);
						}
					},
					error: function() {
						alert("상세 정보를 불러오지 못했습니다.");
					}
				});
			}

			function getReplyList() {
				$.ajax({
					type : "GET",
					url : "/board/replies",
					data : {boardIdx : idx},
					dataType : "json",
					success : function(res) {
						if(res.status !== "success") {
							$("#replyList").html("<div>" + res.message + "</div>");
							return;
						}
						let replies = res.replyData;
						let html = "";

						if(!replies || replies.length === 0) {
							html = "<div style='color:#777;'>등록된 댓글이 없습니다.</div>";
						} else {
								replies.forEach(function(reply){
									html += '<div style="border-bottom:1px solid #eee; padding:10px 0;">';
									html += '	<div style="font-weight:bold;">' + reply.userName + '</div>';
									html += '	<div style="margin:5px 0;">' + reply.content + '</div>';
									html += '	<div style="font-size:12px; color:#777;">' + reply.createdAt + '</div>';
									html += ' <button type="button" onclick="deleteReply(' + reply.replyIdx +')">삭제</button>';
									html += '</div>';
								});
						}
						$("#replyList").html(html);
					},
					error: function() {
						$("#replyList").html("<div>댓글을 불러오지 못했습니다.</div>");
					}
				});
			}

			function saveReply() {
				let content = $("#replyContent").val();

				if (content == null || content.trim() === "") {
					alert("댓글 내용을 입력해주세요.");
					$("#replyContent").focus();
					return;
				}
				if(content.length > 1000) {
					alert("댓글은 1000자 이하로 작성해주세요.")
					$("#replyContent").focus();
					return;
				}
				$.ajax({
					type: "POST",
					url: "/board/reply/save",
					data: {
						boardIdx: idx,
						content: content
					},
					dataType: "json",
					success: function(res) {
						if(res.status === "loginRequired") {
							alert("로그인 후 댓글 작성해주세요.");
							return;
						}
						if(res.status === "fail"){
							alert(res.message);
							return;
						}
						if(res.status === "success"){
							$("#replyContent").val("");
							$("#replyLength").text("0");
							getReplyList();
						}
					},
					error: function() {
						alert("댓글 등록 중 오류가 발생했습니다.");
					}
				});
			}
			function deleteReply(replyIdx) {
				if(!confirm("댓글을 삭제하시겠습니까?")){
					return;
				}
				$.ajax({
					type:"POST",
					url:"/board/reply/delete",
					data:{
						replyIdx:replyIdx
					},
					dataType:"json",
					success: function(res){
						if(res.status === "loginRequired") {
							alert("로그인 후 댓글 작성해주세요.");
							return;
						}
						if(res.status === "fail"){
							alert(res.message);
							return;
						}
						if(res.status === "success"){
							alert("댓글이 삭제되었습니다.");
							getReplyList();
						}
					},
					error: function() {
						alert("댓글 삭제 중 오류가 발생했습니다.");
					}
				});
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