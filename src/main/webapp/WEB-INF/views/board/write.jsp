<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
	<div id="writeFormArea">
        <label>제목</label>
        <input type="text"
        		   id="title"
        		   maxlength="100"
        		   placeholder="제목을 입력하세요" />
       		<div style="text-align:right; margin-bottom:10px;">
				<span id="titleLength">0</span>/100
			</div>
	<label>내용</label>
	<textarea  id="content"
					 rows="10"
					 maxlength="3000"
					 placeholder="내용을 입력하세요"></textarea>
		<div style="text-align:right; margin-bottom:10px;">
			<span id="contentLength">0</span>/3000
		</div>

		<div style="margin-top: 10px;">
			<button type="button" onclick="saveBoard()" style="background-color: #007bff; color: white; border: none;">등록</button>
			<button type="button" onclick= "location.href='/?page=${searchDTO.page}&searchType=${searchDTO.searchType}&keyword=${searchDTO.keyword}'">취소</button>
		</div>
	</div>

	<script>
		$(document).ready(function() {
			$("#title").on("input", function() {
			$("#titleLength").text($(this).val().length);
			});

			$("#content").on("input", function() {
			$("#contentLength").text($(this).val().length);
			});
		});
			function saveBoard() {
				var title = $("#title").val().trim();
				var content = $("#content").val().trim();

				if (title === "") {
					alert("제목을 입력해주세요.");
					$("#title").focus();
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
						content: content
					},
					success: function(result) {
						if (result === "success") {
							alert("게시글이 성공적으로 등록되었습니다!")
							location.href = "/";
						} else  if(result === "loginRequired") {
							alert("로그인 후 작성 가능합니다.");
							location.href = "/member/login";
						} else {
						alert("게시글 등록에 실패했습니다.");
					}
				},
				error: function(xhr) {
					if (xhr.status === 401) {
						alert("로그인 후 작성 가능합니다.");
						location.href = "/member/login";
						return;
					}
					alert("서버 통신 중 오류가 발생했습니다.");
				}
			});
		}
			</script>
</body>
</html>