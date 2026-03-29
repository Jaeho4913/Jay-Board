<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글쓰기</title>
    <style>
        /* 간단하게 예쁘게 꾸미기 */
        body { width: 800px; margin: 0 auto; padding: 20px; }
        input, textarea { width: 100%; margin-bottom: 10px; padding: 10px; box-sizing: border-box; }
        button { padding: 10px 20px; }
    </style>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

</head>
<body>
    <h2>✏️ 글 작성하기</h2>
	<div id="wirteFormArea">
        <label>제목</label>
        <input type="text"  id="title" placeholder="제목을 입력하세요" />
	<c:choose>
		<c:when test="${not empty sessionScope.loginMember }">
        	<label>작성자</label>
        	<input type="text"  id="writer" value="${sessionScope.loginMember.userName}" readonly="readonly"/>
			<input type="hidden" id="boardPw" value="" />
			</c:when>
		<c:otherwise>
			<label>작성자(닉네임)</label>
			<input type="text" id="writer" placeholder="작성자 닉네임을 입력해주세요." />
			<label>글 비밀번호</label>
			<input type="password" id="boardPw" placeholder="수정/삭제 시 사용할 비밀번호를 입력해주세요." />
		</c:otherwise>
	</c:choose>
	<label>내용</label>
	<textarea  id="content" rows="10" placeholder="내용을 입력하세요"></textarea>

		<div style="margin-top: 10px;">
			<button type="button" onclick="saveBoard()" style="background-color: #007bff; color: white; border: none;">등록</button>
			<button type="button" onclick= "location.href='/?page=${searchDTO.page}&searchType=${searchDTO.searchType}&keyword=${searchDTO.keyword}'">취소</button>
		</div>
	</div>

	<script>
			function saveBoard() {
				var title = $("#title").val().trim();
				var writer = $("#writer").val().trim();
				var boardPw = $("#boardPw").val() ? $("#boardPw").val().trim(): "";
				var content = $("#content").val().trim();

				if (title === "") {
					alert("제목을 입력해주세요.");
					$("#title").focus();
					return;
				}
				if (writer === "") {
					alert("작성자를 입력해주세요.");
					$("#writer").focus();
					return;
				}
				if($("#boardPw").attr("type") !== "hidden" && boardPw === "") {
					alert("게시글 비밀번호를 입력해주세요.");
					$("#boardPw").focus();
					return;
				}

				if (content === "") {
					alert("본문을 입력해주세요.");
					$("#content").focus();
					return;
				}

				$.ajax({
					type: "POST",
					url: "/board/save",
					data: {
						title: title,
						writer: writer,
						boardPw: boardPw,
						content: content
					},
					success: function(result) {
						if (result === "success") {
							alert("게시글이 성공적으로 등록되었습니다!")
							location.href = "/";

						} else {
						alert("게시글 등록에 실패했습니다.")
					}
				},
				error: function(err) {
					alert("서버 통신 중 오류가 발생.");
				}
			});
		}
			</script>
</body>
</html>