<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>상세 보기</title>
		<style>
			body{
				width: 800px;
				margin: 0 auto;
				padding: 20px;
			}
			table{
				width: 100%;
				border-collapse: collapse;
				margin-bottom: 20px;
				table-layout: fixed;
			}
			th, td{
				border: 1px solid #ddd;
				padding:15px;
			}
			th{
				background-color: #f2f2f2;
				width: 100px;
			}
			#v_content{
				white-space: pre-wrap;
				overflow-wrap: anywhere;
				word-break: break-word;
				line-height: 1.6;
			}
			.btn-area{
				padding: 10px 20px;
				cursor: pointer;
			}
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
				<div id="likeModalBody" style = "padding-right:8px;"></div>
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

			let replyPage = 1;
			const replySize = 10;

			let likePage = 1;
			const likeSize = 10;
			let likeLoading = false;
			let likeEnd = false;

			function handleAuthAjaxError(xhr, message, loginMessage) {
				if (xhr.status === 401) {
					alert(loginMessage || "로그인 후 사용 가능합니다.");
					location.href = "/member/login";
					return;
				}
				alert(message);
			}
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

				$("#likeModalBody").on("scroll", function(){
					let scrollTop = $(this).scrollTop();
					let innerHeight = $(this).innerHeight();
					let scrollHeight = this.scrollHeight;

					if(scrollTop + innerHeight >= scrollHeight - 10) {
						if(!likeLoading && !likeEnd) {
							likePage++;
							getLikeUsersScroll();
						}
					}
				});

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
						error: function(xhr) {
							handleAuthAjaxError(xhr, "오류가 발생했습니다.", "로그인 후 가능합니다." )
						}
					});
				});
				$("#v_likeCnt").on("click", function(){
					likePage = 1;
					likeLoading = false;
					likeEnd = false;

					$("#likeModalBody")
						.empty()
						.css({
							"max-height" : "none",
							"overflow-y" : "hidden"
						});
						$("#likeModal").show();

						getLikeUsersScroll();
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

						if (response.auth === true || response.isAuth ===  true) {
							let btnHtml = '<button onclick="location.href=\'/board/update?idx=' + idx +'\'">수정</button>' +
												'<button onclick= "authDelete()">삭제</button>';
							$("#authBtnArea").html(btnHtml);
						}
					},
					error: function() {
						alert("상세 정보를 불러오지 못했습니다.");
					}
				});
			}
			function getLikeUsersScroll() {
				if(likeLoading || likeEnd){
					return;
				}
				likeLoading = true;

				$.ajax({
					type : "GET",
					url : "/board/likeUsers",
					data : {
						idx : idx,
						page : likePage,
						size : likeSize
					},
					dataType : "json",
					success : function(res) {
						if(res.status !== "success") {
							$("#likeModalBody").append("<div>공감 목록을 불러오지 못했습니다.</div>");
							return;
						}
						let users = res.likeUsers;

						if (likePage === 1) {
							if(res.totalCount > likeSize){
								$("#likeModalBody").css({
									"max-height" : "400px",
									"overflow-y" : "auto"
								});
							} else {
								$("#likeModalBody").css({
									"max-height" : "none",
									"overflow-y" : "hidden"
								});
							}
						}
						if(!users || users.length === 0) {
							if(likePage === 1) {
								$("#likeModalBody").append("<div>공감한 사용자가 없습니다.</div>");
							}
							likeEnd = true;
							return;
						}
						let html = "";

						users.forEach(function(user){
							html +=  "<div style='height:42px; line-height:42px; border-bottom:1px solid #eee;'>";
							html += user.userName + "(" + user.userId + ")";
							html += "</div>";
						});
						$("#likeModalBody").append(html);
						if (likePage >= res.totalPage || users.length < likeSize) {
							likeEnd = true;
						}
					},
					error : function() {
						alert("공감 목록을 불러오지 못했습니다.");
					},
					complete : function() {
						likeLoading = false;
					}
				});
			}

			function formatReplyTime(time) {
				if(!time) return "";

				return time.replace("T", " ").substring(0, 16);
			}
			function getReplyList(page) {
				if(!page) {
					page = 1;
				}
				replyPage = page;

				$.ajax({
					type : "GET",
					url : "/board/replies",
					data : {
						boardIdx : idx,
						page : replyPage,
						size : replySize
					},
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

									let createdReplyTime = formatReplyTime(reply.createdAt);
									let updatedReplyTime = formatReplyTime(reply.updatedAt);

									let replyTime = createdReplyTime;

									if(updatedReplyTime && updatedReplyTime !== createdReplyTime) {
										replyTime = updatedReplyTime + "  수정됨";
									}

									html += '<div style="border-bottom:1px solid #eee; padding:10px 0;">';
									html += '<div style="font-weight:bold;">' + reply.userName + '</div>';
									html += '<div id="replyView_' + reply.replyIdx + '">';
									html += '<div style="margin:5px 0; white-space:pre-wrap; overflow-wrap:anywhere; word-break:break-word;">' + reply.content + '</div>';
									html += '<div style="font-size:12px; color:#777;">' + replyTime + '</div>';

									if(reply.myReply === true) {
										html += '<div style="margin-top:5px;">';
										html += '	<button type="button" onclick="showReplyEdit(' + reply.replyIdx +')">수정</button>';
										html += '	<button type="button" onclick="deleteReply(' + reply.replyIdx +')">삭제</button>';
										html += '</div>'
									}
									html += '</div>';
										if(reply.myReply === true) {
										html += '<div id="replyEdit_' + reply.replyIdx + '" style="display:none; margin-top:5px;">';
										html += '	<textarea id="replyEditContent_' + reply.replyIdx + '" maxlength="1000" style="width:100%; height:80px; padding:10px; resize:none;">' + reply.content +'</textarea>';
										html += '	<div style="text-align:right; margin-top:5px;">';
										html += '		<span id="replyEditLength_' + reply.replyIdx + '">' +reply.content.length +'</span>/1000';
										html += '	</div>';
										html += '	<div style="margin-top:5px;">';
										html += '		<button type="button" onclick="updateReply(' + reply.replyIdx +')">저장</button>';
										html += '		<button type="button" onclick="cancelReplyEdit(' + reply.replyIdx +')">취소</button>';
										html += '	</div>';
										html +='</div>';
										}
									html += '</div>';
								});
						}
						html += renderReplyPaging(res.page, res.startPage, res.endPage, res.totalPage);

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
							getReplyList(1);
						}
					},
					error: function(xhr) {
						handleAuthAjaxError(xhr, "댓글 등록 중 오류가 발생했습니다.", "로그인 후 작성 가능합니다." )
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
							getReplyList(1);
						}
					},
					error: function(xhr) {
						handleAuthAjaxError(xhr, "댓글 삭제 중 오류가 발생했습니다.", "로그인 후 이용 가능합니다." )
					}
				});
			}
			function showReplyEdit(replyIdx){
				$("#replyView_"+ replyIdx).hide();
				$("#replyEdit_" + replyIdx).show();

				let content = $("#replyEditContent_" + replyIdx).val();
				$("#replyEditLength_" + replyIdx).text(content.length);
				$("#replyEditContent_" + replyIdx).focus();
			}
			function cancelReplyEdit(replyIdx){
				getReplyList(replyPage);
			}
			function updateReply(replyIdx){
				let content = $("#replyEditContent_" + replyIdx).val();

				if (content == null || content.trim() === "") {
					alert("댓글 내용을 입력해주세요.");
					$("#replyEditContent_" + replyIdx).focus();
					return;
				}
				content = content.trim();

				if(content.length > 1000) {
					alert("댓글은 1000자 이하로 작성해주세요.")
					$("#replyEditContent_" + replyIdx).focus();
					return;
				}
				$.ajax({
					type: "POST",
					url: "/board/reply/update",
					data: {
						replyIdx: replyIdx,
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
							alert("댓글이 수정되었습니다.");
							getReplyList(replyPage);
						}
					},
					error: function(xhr) {
						handleAuthAjaxError(xhr, "댓글 수정 중 오류가 발생했습니다.", "로그인 후 사용 가능합니다." )
					}
				});
			}

			function renderReplyPaging(page, startPage, endPage, totalPage) {
				if (!totalPage || totalPage <= 1) {
					return "";
				}
				let html = '<div style="margin-top:15px; text-align:center;">';

				if(page > 1) {
					html += '<button type="button" onclick="getReplyList(' + (page-1) + ')">이전</button> ';
				}

				for (let i = startPage; i <= endPage; i++) {
					if (i === page) {
						html += '<button type="button" disabled style="font-weight:bold;">' + i + '</button>';
					} else {
						html += '<button type="button" onclick="getReplyList(' + i + ')">' + i + '</button>';
					}
				}

				if(page < totalPage) {
					html += '<button type="button" onclick="getReplyList(' + (page+1) + ')">다음</button> ';
				}

				html += '</div>';

				return html;
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
					error: function(xhr) {
						handleAuthAjaxError(xhr, "게시글 삭제 중 오류가 발생했습니다.", "로그인 후 사용 가능합니다." )
					}
				})

			}
			</script>
		</div>
	</body>
</html>