<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html>

	<head>
		<meta charset="UTF-8">
		<title>글 수정</title>
		<style>
			body {
				width: 800px;
				margin: 0 auto;
				padding: 20px;
			}

			input,
			textarea {
				width: 100%;
				margin-bottom: 10px;
				padding: 10px;
				box-sizing: border-box;
			}

			button {
				padding: 10px 20px;
				cursor: pointer;
			}
		</style>
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	</head>

	<body>
		<h2>글 수정하기</h2>

		<div id="updateFormArea">
			<input type="hidden" id="idx" value="">

			<label>제목</label>
			<input type="text" id="title" required>

			<label>작성자</label>
			<input type="text" id="writer" readonly>

			<label>내용</label>
			<textarea id="content" rows="10"></textarea>

			<label for="imageFile">새 이미지를 선택하지 않으면 기존 이미지가 유지됩니다.</label>
			<input type="file" id="imageFile" name="imageFile" accept=".jpg, .jpeg, .png">

			<label for="deleteImage">기존 이미지 삭제</label>
			<input type="checkbox" id="deleteImage" name="deleteImage" style="width: auto;">
			<div style="margin-top: 10px;">
				<button type="button" onclick="updateBoard()"
					style="background-color: #28a745; color: white; border: none;">수정</button>
				<button type="button" onclick="cancelUpdate()" style="padding: 10px 20px;">취소</button>
			</div>
		</div>

		<script>
			const urlParams = new URLSearchParams(window.location.search);
			const idx = urlParams.get('idx');
			const page = urlParams.get('page') || 1;
			const searchType = urlParams.get('searchType') || '';
			const keyword = urlParams.get('keyword') || '';
			const sortType = urlParams.get('sortType') || 'latest';
			const boardGroupIdx = urlParams.get('boardGroupIdx');

			let detailUrl = '/board/view?idx=' + idx + '&page=' + page + '&searchType=' + searchType + '&keyword=' + keyword + '&sortType=' + sortType;

			if (boardGroupIdx) {
				detailUrl += '&boardGroupIdx=' + boardGroupIdx;
			}
			$(document).ready(function () {


				$.ajax({
					type: "GET",
					url: "/board/getDetail",
					data: {idx: idx},
					dataType: "json",
					success: function (board) {

						console.log(board);
						$("#idx").val(board.idx);
						$("#title").val(board.title);
						$("#writer").val(board.writer);
						$("#content").val(board.content);
					},
					error: function () {
						alert("로딩 실패");
					}
				});
			});

			function updateBoard() {
				var title = $("#title").val().trim();
				var writer = $("#writer").val().trim();
				var content = $("#content").val().trim();

				if (title === "" || content === "") {
					alert("제목과 내용은 필수 입력입니다.");
					return;
				}

				const formData = new FormData();

				formData.append("idx", idx);
				formData.append("title", title);
				formData.append("writer", writer);
				formData.append("content", content);
				formData.append("deleteImage", $("#deleteImage").prop("checked"));

				const imageFile = $("#imageFile")[0].files[0];

				if (imageFile) {
					formData.append("imageFile", imageFile);
				}

				$.ajax({
					type: "POST",
					url: "/board/update",
					data: formData,
					processData: false,
					contentType: false,
					success: function (result) {

						console.log(result);
						if (result === "success") {
							alert("수정 완료되었습니다.");
							location.href = detailUrl;
						} else {
							alert("게시글을 수정할 수 없습니다.");
						}
					},
					error: function (xhr) {
						if (xhr.status === 400) {
							alert(xhr.responseText);
							return;
						}
						alert("오류 발생");
					}
				});
			}

			function cancelUpdate() {
				location.href = detailUrl;
			}
		</script>
	</body>

	</html>